package signalbackupreader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import signalbackupreader.entry.*;

public class Program {
	
	private static Logger logger = LogManager.getLogger(Program.class);
	
	public static void main(String args[]) throws SignalBackupReaderException, SQLException {
		Path backupFilePath = Paths.get(args[0]);
		Path passphrasePath = Paths.get(args[1]);
		Path outputDir = Paths.get(args[2]);
		DatabaseAndBlobDumper dumper = new DatabaseAndBlobDumper(backupFilePath, passphrasePath, outputDir);
		dumper.run();
	}
	
	public static void main_useSignalBackupReader(String args[]) throws IOException, SignalBackupReaderException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SQLException {
		Path backupPath = Paths.get(args[0]);
		Path passphrasePath = Paths.get(args[1]);

		Connection conn = null;
		if(args.length>=3) {
			Path sqlitePath = Paths.get(args[2]);
			String url = String.format("jdbc:sqlite:%s", sqlitePath);
			logger.info("Using url " + url);
			conn = DriverManager.getConnection(url);
			if (conn != null) {
				conn.setAutoCommit(false);
				DatabaseMetaData meta = conn.getMetaData();
				logger.info("The driver name is " + meta.getDriverName());
				logger.info("A new database has been created.");
			}
		}
		
		SignalBackupReader sbr = new SignalBackupReader(backupPath, passphrasePath);
		IEntry entry;
		while((entry = sbr.readNextEntry()) != null) {
			if(entry instanceof SqlStatementEntry) {
				if(conn != null) {
					SqlStatementEntry stmt = (SqlStatementEntry)entry;
					PreparedStatement pstmt = stmt.prepareStatement(conn);
					pstmt.execute();
				}
			}
		}
		
		if(conn != null) {
			logger.info("Commit");
			conn.commit();
			conn.close();
		}
	}
}
