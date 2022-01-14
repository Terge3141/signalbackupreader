package signalbackupreader.entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqlStatementEntry implements IEntry {

	private String statement;
	private List<Object> parameters;
	
	public SqlStatementEntry(String statement, List<Object> parameters) {
		this.statement = statement;
		this.parameters = parameters;
	}
	
	public PreparedStatement prepareStatement(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(statement);
		int i = 1;
		for(Object obj : parameters) {
			stmt.setObject(i, obj);
			i++;
		}
		
		return stmt;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return "SqlStatement";
	}
}
