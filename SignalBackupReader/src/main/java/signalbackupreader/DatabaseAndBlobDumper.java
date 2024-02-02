package signalbackupreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import signalbackupreader.entry.*;

public class DatabaseAndBlobDumper {
	
	private static Logger logger = LogManager.getLogger(DatabaseAndBlobDumper.class);
	
	private Path sqliteOutputPath;
	private Path backupFilePath;
	private String passphrase;
	private Path blobOutputDir;
	private boolean doCreateExtraSqlViews = false;
	private boolean allowOverrides = false;
	private Connection connection;
	private SignalBackupReader signalBackupReader;

	public static DatabaseAndBlobDumper of(Path backupFilePath, Path passphrasePath,
			Path blobOutputDir)
			throws SignalBackupReaderException, SQLException {
		logger.info("Reading passphrase file from '{}'", passphrasePath);
		try {
			String passphrase = Files.readString(passphrasePath);
			return new DatabaseAndBlobDumper(backupFilePath, passphrase, blobOutputDir);
		} catch (IOException e) {
			String msg = String.format("Cannot read passphrase file '%s'",
					passphrasePath);
			throw new SignalBackupReaderException(msg, e);
		}
	}
	
	public static DatabaseAndBlobDumper of(Path backupFilePath, String passphrase,
			Path blobOutputDir)
			throws SignalBackupReaderException, SQLException {
		return new DatabaseAndBlobDumper(backupFilePath, passphrase, blobOutputDir);
	}
	
	private DatabaseAndBlobDumper(Path backupFilePath, String passphrase,
			Path blobOutputDir)
			throws SignalBackupReaderException, SQLException {
		this.backupFilePath = backupFilePath;
		this.passphrase = passphrase;
		this.blobOutputDir = blobOutputDir;
		
		this.sqliteOutputPath = this.blobOutputDir.resolve("database.sqlite");
	}
	
	private void checkAndCreateDirs() throws SignalBackupReaderException {
		
		if(!backupFilePath.toFile().exists()) {
			String msg = String.format("Signal backup file '%s' does not exist",
					backupFilePath);
			throw new SignalBackupReaderException(msg);
		}
		
		if(blobOutputDir.toFile().exists()) {
			if(!isAllowOverrides()) {
				String msg = String.format("Blob directory '%s' already exists", this.blobOutputDir);
				throw new SignalBackupReaderException(msg);
			}
		} else {
			if(!blobOutputDir.toFile().mkdirs()) {
				String msg = String.format("Cannot create directory '%s'", this.blobOutputDir);
				throw new SignalBackupReaderException(msg);
			}
		}
		
		if(sqliteOutputPath.toFile().exists()) {
			if(isAllowOverrides()) {
				if(!sqliteOutputPath.toFile().delete()) {
					String msg = String.format("Could not delete sqlite file '%s'", this.sqliteOutputPath);
					throw new SignalBackupReaderException(msg);
				}
			} else {
				String msg = String.format("Sqlite file '%s' already exists", this.sqliteOutputPath);
				throw new SignalBackupReaderException(msg);
			}
		}
	}
	
	private List<String> getSqlViewCmds(int backupFileVersion) throws SignalBackupReaderException {
		List<String> sqlViewCmds = new ArrayList<String>();
		try {
			if(backupFileVersion==0) {
				sqlViewCmds.add(loadResourceToString("create_v0_00_sms_corrected.sql"));
				sqlViewCmds.add(loadResourceToString("create_v0_01_mms_corrected.sql"));
				sqlViewCmds.add(loadResourceToString("create_v0_02_all_names.sql"));
				sqlViewCmds.add(loadResourceToString("create_v0_03_all_messages.sql"));
				sqlViewCmds.add(loadResourceToString("create_v0_04_chats.sql"));
				sqlViewCmds.add(loadResourceToString("create_v0_05_stickers.sql"));
				sqlViewCmds.add(loadResourceToString("create_v0_06_attachments.sql"));
			} else if(backupFileVersion==1) {
				sqlViewCmds.add(loadResourceToString("create_v1_00_types.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_01_masks_bits.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_02_notorig_messagetypes.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_03_masks.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_04_messages_and_masks.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_05_all_names.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_06_messages_mediatypes.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_07_chats.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_08_attachments.sql"));
				sqlViewCmds.add(loadResourceToString("create_v1_09_stickers.sql"));
			} else {
				String msg = String.format("Backup file version %d not supported", backupFileVersion);
				throw new SignalBackupReaderException(msg);
			}
		} catch (IOException e) {
			String msg = "Cannot load internal sql resource file";
			throw new SignalBackupReaderException(msg, e);
		}
		
		return sqlViewCmds;
	}
	
	public void run() throws SignalBackupReaderException, SQLException {
		logger.info("Start dump");
		
		checkAndCreateDirs();
		
		
		logger.info("Reading backup file from '{}'", backupFilePath);
		logger.info("Writing sqlite file to '{}'", sqliteOutputPath);
		
		this.signalBackupReader = new SignalBackupReader(backupFilePath, passphrase);	
		String url = String.format("jdbc:sqlite:%s", sqliteOutputPath);
		this.connection = DriverManager.getConnection(url);
		this.connection.setAutoCommit(false);
		
		List<String> sqlViewCmds = getSqlViewCmds(signalBackupReader.getBackupFileVersion());
		
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
			executeStatements(connection, sqlViewCmds);
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
	
	public boolean isAllowOverrides() {
		return this.allowOverrides;
	}
	
	public void setAllowsOverrides(boolean allowOverrides) {
		this.allowOverrides = allowOverrides;
	}
	
	public Path getSqliteOutputPath() {
		return sqliteOutputPath;
	}

	private void executeStatements(Connection connection, List<String> sqlStatements) throws SignalBackupReaderException {
		ScriptRunner scriptRunner = new ScriptRunner(connection);
		scriptRunner.setSendFullScript(false);
	    scriptRunner.setStopOnError(true);
	    scriptRunner.setThrowWarning(true);
	    scriptRunner.setEscapeProcessing(false);
	    scriptRunner.setLogWriter(null);
		
		for(String sql : sqlStatements) {
			try {
				scriptRunner.runScript(new StringReader(sql));
			} catch(RuntimeSqlException e) {
				throw new SignalBackupReaderException("Cannot invoke sql statements", e);
			}
		}
	}

	private String loadResourceToString(String resourceName) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}
}
