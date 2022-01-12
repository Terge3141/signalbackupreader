
public class AvatarEntry implements IEntry {

	private String avatarName;
	private String recipientId;
	private byte[] blob;
	
	public AvatarEntry(String avatarName, String recipientId, byte[] blob) {
		this.avatarName = avatarName;
		this.recipientId = recipientId;
		this.blob = blob;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public byte[] getBlob() {
		return blob;
	}

	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	@Override
	public String getName() {
		return "Avatar";
	}
}
