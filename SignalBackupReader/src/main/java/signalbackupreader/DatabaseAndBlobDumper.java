package signalbackupreader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import signalbackupreader.entry.*;

public class DatabaseAndBlobDumper {
	
	private static Logger logger = LogManager.getLogger(DatabaseAndBlobDumper.class);
	
	private Path sqliteOutputPath;
	private Path blobOutputDir;
	private boolean doCreateExtraSqlViews = false;
	private Connection connection;
	private SignalBackupReader signalBackupReader;
	private List<String> sqlViewCmds;
	
	public DatabaseAndBlobDumper(Path backupFilePath, Path passphrasePath,
			Path blobOutputDir)
			throws SignalBackupReaderException, SQLException {
		this.blobOutputDir = blobOutputDir;
		this.sqliteOutputPath = this.blobOutputDir.resolve("database.sqlite");
		
		if(!backupFilePath.toFile().exists()) {
			String msg = String.format("Signal backup file '%s' does not exist",
					backupFilePath);
			throw new SignalBackupReaderException(msg);
		}
		
		if(blobOutputDir.toFile().exists()) {
			String msg = String.format("Blob directory '%s' already exists", this.blobOutputDir);
			throw new SignalBackupReaderException(msg);
		}
		
		if(!blobOutputDir.toFile().mkdirs()) {
			String msg = String.format("Cannot create directory '%s'", this.blobOutputDir);
			throw new SignalBackupReaderException(msg);
		}
		
		if(sqliteOutputPath.toFile().exists()) {
			String msg = String.format("Sqlite file '%s' already exists", this.sqliteOutputPath);
			throw new SignalBackupReaderException(msg);
		}
		
		sqlViewCmds = new ArrayList<String>();
		try {
			sqlViewCmds.add(loadResourceToString("v_00_sms_corrected.sql.txt"));
			sqlViewCmds.add(loadResourceToString("v_01_mms_corrected.sql.txt"));
			sqlViewCmds.add(loadResourceToString("v_02_all_names.sql.txt"));
			sqlViewCmds.add(loadResourceToString("v_03_all_messages.sql.txt"));
			sqlViewCmds.add(loadResourceToString("v_04_chats.sql.txt"));
		} catch (IOException e) {
			String msg = "Cannot load internal sql resource file";
			throw new SignalBackupReaderException(msg, e);
		}
		
		logger.info("Reading backup file from '{}'", backupFilePath);
		logger.info("Reading passphrase file from '{}'", passphrasePath);
		logger.info("Writing sqlite file to '{}'", sqliteOutputPath);
		
		this.signalBackupReader = new SignalBackupReader(backupFilePath, passphrasePath);	
		String url = String.format("jdbc:sqlite:%s", sqliteOutputPath);
		this.connection = DriverManager.getConnection(url);
		this.connection.setAutoCommit(false);
	}
	
	public void run() throws SignalBackupReaderException, SQLException {
		logger.info("Start dump");
		
		IEntry entry;
		while ((entry = this.signalBackupReader.readNextEntry()) != null) {
			if (entry instanceof SqlStatementEntry) {
				SqlStatementEntry stmt = (SqlStatementEntry) entry;
				stmt.prepareStatement(connection).execute();
			} else if (entry instanceof IBlobEntry) {
				IBlobEntry blobEntry = (IBlobEntry)entry;
				String filename = String.format("%s.bin", blobEntry.getNamePrefix());
				Path fullpath = blobOutputDir.resolve(filename);
				try {
					Files.write(fullpath, blobEntry.getBlob());
				} catch (IOException e) {
					String msg = String.format("Cannot write file %s", fullpath);
					throw new SignalBackupReaderException(msg, e);
				}
			}
		}

		logger.info("Writing to sql database");
		connection.commit();
		logger.info("Done");
		
		if(isCreateExtraSqlViews()) {
			logger.info("Creating extra views");
			createSqlViews(connection);
			connection.commit();
			logger.info("Done");
		}
		
	}

	public boolean isCreateExtraSqlViews() {
		return doCreateExtraSqlViews;
	}

	public void setCreateExtraSqlViews(boolean doCreateSqlViews) {
		this.doCreateExtraSqlViews = doCreateSqlViews;
	}
	
	private void createSqlViews(Connection connection) throws SQLException {
		for(String sql : this.sqlViewCmds) {
			connection.createStatement().execute(sql);
		}
	}

	private String loadResourceToString(String resourceName) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}
}
