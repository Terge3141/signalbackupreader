package signalbackupreader.entry;

public class AttachmentEntry implements IEntry, IBlobEntry {

	private long rowId;
	private long attachmentId;
	private byte[] blob;
	
	public AttachmentEntry(long rowId, long attachmentId, byte[] blob) {
		this.rowId = rowId;
		this.attachmentId = attachmentId;
		this.blob = blob;
	}

	@Override
	public String getNamePrefix() {
		return String.format("Attachment_%d", attachmentId);
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
