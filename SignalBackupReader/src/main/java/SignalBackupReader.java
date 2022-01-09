import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.thoughtcrime.securesms.backup.*;
import org.thoughtcrime.securesms.backup.BackupProtos.BackupFrame;
import org.thoughtcrime.securesms.backup.BackupProtos.Header;

import com.google.protobuf.ByteString;

import at.favre.lib.crypto.HKDF;

public class SignalBackupReader {
	
	private InputStream in;
	private final int HEADER_SIZE = 4;
	private final int PASSPHRASE_LENGH = 30;
	private final String HKDF_INFO = "Backup Export";
	private String passphrase;
	
	public SignalBackupReader(Path backupPath, Path passphrasePath) throws IOException, SignalBackupReaderException {
		this.in = new BufferedInputStream(new FileInputStream(backupPath.toFile()));
		this.passphrase = readPassphrase(passphrasePath);
		
		System.out.println("Passphrase:*" + this.passphrase + "*");
	}
	
	
	// http://jhnet.co.uk/articles/signal_backups
	public void nextFrame() throws IOException, NoSuchAlgorithmException {
		byte buf[] = new byte[HEADER_SIZE];
		in.read(buf);
		int headerSize = getInt(buf);
		System.out.println(headerSize);
		//dumpByteArray(buf);
		
		byte data[] = new byte[headerSize]; 
		in.read(data);
		
		BackupFrame backupFrame = BackupFrame.parseFrom(data);
		Header header = backupFrame.getHeader();
		ByteString ivByteString = header.getIv();
		ByteString saltByteString = header.getSalt();
		
		byte salt[] = saltByteString.toByteArray();
		byte passphraseBytes[] = this.passphrase.getBytes(StandardCharsets.US_ASCII);
		byte hash[] = passphraseBytes;
		
		dumpByteArray("Salt", salt);
		dumpByteArray("Hash1", hash);
		
		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		sha512.update(salt);
		for(int i=0; i<250000; i++) {
			sha512.update(hash);
			sha512.update(passphraseBytes);
			hash = sha512.digest();
		}
		
		byte backupKey[] = Arrays.copyOf(hash, 32);
		dumpByteArray("Backupkey", backupKey);
		
		byte mysalt[] = new byte[0	];
		byte res[] = HKDF.fromHmacSha256().extract(mysalt, backupKey);
		
		byte info[] = HKDF_INFO.getBytes(StandardCharsets.US_ASCII);
		byte keys[] = HKDF.fromHmacSha256().expand(res, info, 64);
		byte cypher_key[] = Arrays.copyOf(keys, 32);
		byte hmac_key[] = Arrays.copyOfRange(keys, 32, 64);
		
		dumpByteArray("Cipherkey: ", cypher_key);
		dumpByteArray("Mackey", hmac_key);
	}
	
	private int getInt(byte[] arr) {
		ByteBuffer bb = ByteBuffer.wrap(arr);
		return bb.getInt();
	}
	
	private String readPassphrase(Path passphrasePath) throws IOException, SignalBackupReaderException {
		String buf = Files.readString(passphrasePath);
		buf = buf.replace(" ", "").replace("\n", "");
		if(buf.length()!=PASSPHRASE_LENGH) {
			String msg = String.format("Passphrase length is not %d but %d. Passhrase is %s",
					PASSPHRASE_LENGH, buf.length(), buf);
			throw new SignalBackupReaderException(msg);
		}
		
		return buf;
	}
	
	private void dumpByteArray(String description, byte[] arr) {
		if(description!=null) {
			System.out.print(description + ": ");
		}
		for(int i=0; i<arr.length; i++) {
			System.out.print(String.format("%02X", arr[i]));
		}
		System.out.println();
	}
}
