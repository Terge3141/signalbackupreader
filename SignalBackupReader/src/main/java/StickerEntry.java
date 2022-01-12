
public class StickerEntry implements IEntry {

	private long rowId;
	private byte[] blob;
	
	public StickerEntry(long rowId, byte[] blob) {
		this.rowId = rowId;
		this.blob = blob;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public byte[] getBlob() {
		return blob;
	}

	public void setBlob(byte[] blob) {
		this.blob = blob;
	}
	
	@Override
	public String getName() {
		return "Sticker";
	}
}
