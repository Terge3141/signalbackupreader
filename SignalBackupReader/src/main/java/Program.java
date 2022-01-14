import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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

public class Program {
	public static void main(String args[]) throws SignalBackupReaderException, SQLException {
		Path backupFilePath = Paths.get(args[0]);
		Path passphrasePath = Paths.get(args[1]);
		Path outputDir = Paths.get(args[2]);
		DatabaseAndBlobDumper dumper = new DatabaseAndBlobDumper(backupFilePath, passphrasePath, outputDir);
		dumper.run();
	}
	public static void main2(String args[]) throws IOException, SignalBackupReaderException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SQLException {
		Path backupPath = Paths.get(args[0]);
		Path passphrasePath = Paths.get(args[1]);

		Connection conn = null;
		if(args.length>=3) {
			Path sqlitePath = Paths.get(args[2]);
			String url = String.format("jdbc:sqlite:%s", sqlitePath);
			System.out.println("Using url " + url);
			conn = DriverManager.getConnection(url);
			if (conn != null) {
				conn.setAutoCommit(false);
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
				System.out.println("A new database has been created.");
			}
		}
		
		
		
		if(false) {
			PrintStream ps = new PrintStream(new FileOutputStream("/tmp/log.txt"));
			System.setOut(ps);
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
			System.out.println("Commit");
			conn.commit();
			conn.close();
		}
	}
}
