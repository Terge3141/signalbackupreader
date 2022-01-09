import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Program {
	public static void main(String args[]) throws IOException, SignalBackupReaderException, NoSuchAlgorithmException {
		Path backupPath = Paths.get(args[0]);
		Path passphrasePath = Paths.get(args[1]);
		SignalBackupReader sbr = new SignalBackupReader(backupPath, passphrasePath);
		sbr.nextFrame();
	}
}
