public class DatabaseVersionEntry implements IEntry {
	
	private int version;
	
	public DatabaseVersionEntry(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String getName() {
		return "Version";
	}
}
