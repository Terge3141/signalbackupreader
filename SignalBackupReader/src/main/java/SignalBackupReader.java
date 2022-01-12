import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.thoughtcrime.securesms.backup.*;
import org.thoughtcrime.securesms.backup.BackupProtos.Attachment;
import org.thoughtcrime.securesms.backup.BackupProtos.Avatar;
import org.thoughtcrime.securesms.backup.BackupProtos.BackupFrame;
import org.thoughtcrime.securesms.backup.BackupProtos.Header;
import org.thoughtcrime.securesms.backup.BackupProtos.KeyValue;
import org.thoughtcrime.securesms.backup.BackupProtos.SharedPreference;
import org.thoughtcrime.securesms.backup.BackupProtos.SqlStatement;
import org.thoughtcrime.securesms.backup.BackupProtos.Sticker;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;

import at.favre.lib.crypto.HKDF;

public class SignalBackupReader {
	
	private InputStream in;
	private final int HEADER_SIZE = 4;
	private final int PASSPHRASE_LENGH = 30;
	private final String HKDF_INFO = "Backup Export";
	private String passphrase;
	private byte hmacKeys[];
	private byte cypherKey[];
	private long counter;
	private byte[] ivBytes;
	
	public SignalBackupReader(Path backupPath, Path passphrasePath) throws IOException, SignalBackupReaderException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		this.in = new BufferedInputStream(new FileInputStream(backupPath.toFile()));
		this.passphrase = readPassphrase(passphrasePath);
		
		System.out.println("Passphrase:*" + this.passphrase + "*");
		readKeys();
		
		/*while(readBackupFrame()!=null) {
			
		}*/
		/*for(int i=0; i<100; i++) {
			readBackupFrame();
		}*/
	}
	
	public IEntry readBackupFrame() throws IOException, SignalBackupReaderException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] data = nextBlock();
		if(data==null) {
			return null;
		}
		/*byte[] encrypted = Arrays.copyOf(data, data.length-10);
		byte[] theirMac = Arrays.copyOfRange(data, data.length - 10, data.length);
		
		//dumpByteArray("IV", ivBytes);
		//dumpByteArray("IV", this.ivBytes);
		//dumpByteArray("encrypted", encrypted);
		
		byte[] myMac = HKDF.fromHmacSha256().extract(hmacKeys, encrypted);
		myMac = Arrays.copyOf(myMac, theirMac.length);
		//dumpByteArray("theirmac", theirMac);
		//dumpByteArray("mymac", myMac);
		
		if(!Arrays.equals(myMac, theirMac)) {
			throw new SignalBackupReaderException("mymac and theirmac differ");
		}
		
		//SecretKeySpec secretKeySpec = new SecretKeySpec(cypherKey, "AES");
		SecretKey secretKey = new SecretKeySpec(cypherKey, 0, cypherKey.length, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
		
		//cipher.update(encrypted);
		byte[] decrypted = cipher.doFinal(encrypted);
		incCounter();*/
		
		//dumpByteArray("decrypted", decrypted);
		
		byte decrypted[] = decrypt(data, true);
		BackupFrame frame = BackupFrame.parseFrom(decrypted);
		//System.out.println(backupFrame.getVersion());
		/*Set<FieldDescriptor> fds = frame.getAllFields().keySet();
		for(FieldDescriptor fd : fds) {
			System.out.println("fd: " + fds.size() + " " + fd.getFullName());
		}*/
		
		if(frame.hasHeader()) {
			throw new SignalBackupReaderException("Header already parse. File corrupt?");
		}
		else if(frame.hasStatement()) {
			SqlStatement stmt = frame.getStatement();
			System.out.println("Statement: " + stmt.getStatement());
					
			// "sms_fts"
			// "mms_fts"
			// "emoji_search"
			// getStatement().toLowerCase().startsWith("create table sqlite_");
			// Ignore the following statements
			String stmtStr = stmt.getStatement();
			if(stmtStr.contains("sms_fts") || stmtStr.contains("mms_fts") ||
					stmtStr.contains("emoji_search") ||
					stmtStr.toLowerCase().startsWith("create table sqlite_")) {
				return readBackupFrame();
			}

			
			List<Object> parameters = new ArrayList<Object>();
			for (SqlStatement.SqlParameter parameter : stmt.getParametersList()) {
				if (parameter.hasStringParamter()) {
					parameters.add(parameter.getStringParamter());
				} else if (parameter.hasDoubleParameter()) {
					parameters.add(parameter.getDoubleParameter());
				} else if (parameter.hasIntegerParameter()) {
					parameters.add(parameter.getIntegerParameter());
				} else if (parameter.hasBlobParameter()) {
					parameters.add(parameter.getBlobParameter().toByteArray());
				} else if (parameter.hasNullparameter()) {
					parameters.add(null);
				}
			}
			
			return new SqlStatementEntry(stmt.getStatement(), parameters);
		}
		else if(frame.hasPreference()) {
			SharedPreference pref = frame.getPreference();
			
			List<String> stringSetValues = new ArrayList<String>();
			for(int i=0; i<pref.getStringSetValueCount(); i++) {
				stringSetValues.add(pref.getStringSetValue(i));
			}
			
			return new SharedPreferenceEntry(pref.getFile(), pref.getKey(),
					pref.getValue(), pref.getBooleanValue(), stringSetValues);
		}
		else if(frame.hasAttachment() || frame.hasSticker() || frame.hasAvatar()) {
			int length;
			if(frame.hasAttachment()) {
				length = frame.getAttachment().getLength();
			}
			else if(frame.hasSticker()) {
				length = frame.getSticker().getLength();
			}
			else if(frame.hasAvatar()) {
				length = frame.getAvatar().getLength();
			}
			else {
				throw new SignalBackupReaderException("Internal error");
			}
			
			byte encBlob[] = new byte[length+10];
			in.read(encBlob);
			byte decBlob[] = decrypt(encBlob, false);
			
			if(decBlob.length!=length) {
				throw new SignalBackupReaderException("Incorrect length of decrypted message");
			}
			
			if(frame.hasAttachment()) {
				Attachment a = frame.getAttachment();
				return new AttachmentEntry(a.getRowId(), a.getAttachmentId(), decBlob);
			}
			else if(frame.hasSticker()) {
				Sticker s = frame.getSticker();
				return new StickerEntry(s.getRowId(), decBlob);
			}
			else if(frame.hasAvatar()) {
				Avatar a = frame.getAvatar();
				return new AvatarEntry(a.getName(), a.getRecipientId(), decBlob);
			}
			else {
				throw new SignalBackupReaderException("Internal error");
			}
		}
		else if(frame.hasVersion()) {
			return new DatabaseVersionEntry(frame.getVersion().getVersion());
		}
		else if(frame.hasEnd()) {
			return new EndEntry(frame.getEnd());
		}
		else if(frame.hasKeyValue()) {
			KeyValue kv = frame.getKeyValue();
			return new KeyValueEntry(kv.getKey(), kv.getBlobValue().toByteArray(),
					kv.getBooleanValue(), kv.getFloatValue(), kv.getIntegerValue(),
					kv.getLongValue(), kv.getStringValue());
		}
		
		throw new SignalBackupReaderException("Unknown frame type");
	}
	
	private byte[] decrypt(byte[] data, boolean frameMacCheck) throws InvalidKeyException, InvalidAlgorithmParameterException, SignalBackupReaderException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] encrypted = Arrays.copyOf(data, data.length-10);
		byte[] theirMac = Arrays.copyOfRange(data, data.length - 10, data.length);
		
		//dumpByteArray("IV", this.ivBytes);
		//dumpByteArray("encrypted", encrypted);
		
		byte[] myMac;
		if(frameMacCheck) {
			myMac = HKDF.fromHmacSha256().extract(hmacKeys, encrypted);
		}
		else {
			/*HKDF hkdf = HKDF.fromHmacSha256();
			
			hkdf.extract(hmacKeys, ivBytes);
			myMac = hkdf.extract(hmacKeys, encrypted);*/
			byte buf[] = Arrays.copyOf(ivBytes, ivBytes.length + encrypted.length);
		    System.arraycopy(encrypted, 0, buf, ivBytes.length, encrypted.length);
		    myMac = HKDF.fromHmacSha256().extract(hmacKeys, buf);
		}
		
		myMac = Arrays.copyOf(myMac, theirMac.length);
		
		if(!Arrays.equals(myMac, theirMac)) {
			dumpByteArray("theirmac", theirMac);
			dumpByteArray("mymac", myMac);
			throw new SignalBackupReaderException("mymac and theirmac differ");
		}
		
		//SecretKeySpec secretKeySpec = new SecretKeySpec(cypherKey, "AES");
		SecretKey secretKey = new SecretKeySpec(cypherKey, 0, cypherKey.length, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
		
		//cipher.update(encrypted);
		byte[] decrypted = cipher.doFinal(encrypted);
		incCounter();
		
		return decrypted;
	}
	
	private byte[] nextBlock() throws IOException {
		if(in.available()< HEADER_SIZE) {
			return null;
		}
		
		byte buf[] = new byte[HEADER_SIZE];
		in.read(buf);
		//dumpByteArray("headersize bytes", buf);
		int headerSize = getInt(buf);
		//System.out.println(headerSize);
		long tmp = getUintFromBytes(buf);
		//System.out.println(tmp);
		if(tmp>10000) {
			byte[] buf2 = new byte[10];
			in.read(buf2);
			dumpByteArray("buf", buf2);
			throw new IOException();
		}
		byte data[] = new byte[headerSize]; 
		in.read(data);

		return data;
	}
	
	
	// http://jhnet.co.uk/articles/signal_backups
	private void readKeys() throws IOException, NoSuchAlgorithmException {
		/*byte buf[] = new byte[HEADER_SIZE];
		in.read(buf);
		int headerSize = getInt(buf);
		System.out.println(headerSize);*/
		//dumpByteArray(buf);
		
		byte data[] = nextBlock();
		
		BackupFrame backupFrame = BackupFrame.parseFrom(data);
		Header header = backupFrame.getHeader();
		ByteString ivByteString = header.getIv();
		ByteString saltByteString = header.getSalt();
		
		byte salt[] = saltByteString.toByteArray();
		byte passphraseBytes[] = this.passphrase.getBytes(StandardCharsets.US_ASCII);
		byte hash[] = passphraseBytes;
		this.ivBytes = ivByteString.toByteArray();
		counter = getUintFromBytes(Arrays.copyOfRange(ivBytes, 0, 4)); 
		
		dumpByteArray("Salt", salt);
		dumpByteArray("Hash", hash);
		System.out.println("Counter: " + counter);
		
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
		this.cypherKey = Arrays.copyOf(keys, 32);
		this.hmacKeys = Arrays.copyOfRange(keys, 32, 64);
		
		dumpByteArray("Cipherkey: ", cypherKey);
		dumpByteArray("Mackey", hmacKeys);
	}
	
	private void incCounter() {
		counter++;
		counter = counter & 0xFFFFFFFF;
		byte[] counterBytes = bytesFromUint(counter);
		for(int i=0; i<counterBytes.length; i++) {
			this.ivBytes[i] = counterBytes[i];
		}
	}
	
	private int getInt(byte[] arr) {
		ByteBuffer bb = ByteBuffer.wrap(arr);
		return bb.getInt();
	}
	
	private long getUintFromBytes(byte[] arr) {
		byte buf[] = arr;
		if(arr.length<8) {
			buf = new byte[8];
			for(int i=8-arr.length; i<8; i++) {
				buf[i] = arr[i - 8 + arr.length];
			}
		}
		
		ByteBuffer bb = ByteBuffer.wrap(buf);
		return bb.getLong();
	}
	
	private byte[] bytesFromUint(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    byte arr[] = buffer.array();
	    return Arrays.copyOfRange(arr, arr.length-4, arr.length);
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
