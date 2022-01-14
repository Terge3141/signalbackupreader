package signalbackupreader.entry;

public class EndEntry implements IEntry {

	private boolean end;
	
	public EndEntry(boolean end) {
		this.end = end;
	}
	
	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	@Override
	public String getName() {
		return "End";
	}

}
