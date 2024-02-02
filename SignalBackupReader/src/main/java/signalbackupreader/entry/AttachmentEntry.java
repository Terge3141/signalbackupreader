package signalbackupreader.entry;

public class AttachmentEntry implements IEntry, IBlobEntry {

	private long rowId;
	private long attachmentId;
	private byte[] blob;
	private int backupFileVersion;
	
	public AttachmentEntry(long rowId, long attachmentId, byte[] blob, int backupFileVersion) {
		this.rowId = rowId;
		this.attachmentId = attachmentId;
		this.blob = blob;
		this.backupFileVersion = backupFileVersion;
	}

	@Override
	public String getNamePrefix() {
		if(backupFileVersion==0) {
			return String.format("Attachment_%d", attachmentId);
		}
		
		return String.format("Attachment_%d", rowId);
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(long attachmentId) {
		this.attachmentId = attachmentId;
	}

	@Override
	public byte[] getBlob() {
		return blob;
	}

	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

	@Override
	public String getName() {
		return "Attachment";
	}
}
