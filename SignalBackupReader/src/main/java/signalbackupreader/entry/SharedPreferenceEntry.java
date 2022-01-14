package signalbackupreader.entry;

import java.util.List;

public class SharedPreferenceEntry implements IEntry {

    private String file;
    private String key;
    private String value;
    private boolean booleanValue;
    private List<String> stringSetValues;
	
	public SharedPreferenceEntry(String file, String key, String value, boolean booleanValue,
			List<String> stringSetValues) {
		this.file = file;
		this.key = key;
		this.value = value;
		this.booleanValue = booleanValue;
		this.stringSetValues = stringSetValues;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public List<String> getStringSetValue() {
		return stringSetValues;
	}

	public void setStringSetValue(List<String> stringSetValue) {
		this.stringSetValues = stringSetValue;
	}

	@Override
	public String getName() {
		return "SharedPreference";
	}

}
