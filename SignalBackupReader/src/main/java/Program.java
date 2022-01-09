import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Program {
	public static void main(String args[]) throws IOException, SignalBackupReaderException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Path backupPath = Paths.get(args[0]);
		Path passphrasePath = Paths.get(args[1]);
		SignalBackupReader sbr = new SignalBackupReader(backupPath, passphrasePath);
		//sbr.read();
	}
}
