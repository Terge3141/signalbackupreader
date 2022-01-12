
public class KeyValueEntry implements IEntry {
	
	private String key;
	private byte[] blobValue;
	private boolean booleanValue;
	private float floatValue;
	private int integerValue;
	private long longValue;
	private String stringValue;

	public KeyValueEntry(String key, byte[] blobValue, boolean booleanValue, float floatValue, int integerValue,
			long longValue, String stringValue) {
		this.key = key;
		this.blobValue = blobValue;
		this.booleanValue = booleanValue;
		this.floatValue = floatValue;
		this.integerValue = integerValue;
		this.longValue = longValue;
		this.stringValue = stringValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public byte[] getBlobValue() {
		return blobValue;
	}

	public void setBlobValue(byte[] blobValue) {
		this.blobValue = blobValue;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	public int getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(int integerValue) {
		this.integerValue = integerValue;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getName() {
		return "KeyValue";
	}

}
