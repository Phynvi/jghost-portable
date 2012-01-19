package org.whired.ghostclient.io.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.whired.ghost.constants.Vars;

/**
 * Represents a MySQL table
 * 
 * @author Whired
 */
public class Table {

	private final String tableName;
	private final Database database;
	private final Column[] columns;

	/**
	 * Creates a new table in the specified database with the specified name
	 * 
	 * @param database the database to add this table to
	 * @param tableName the name for the table
	 * @param structure the structure
	 * @param recreate whether or not to recreate the table if it already
	 *        exists
	 */
	public Table(Database database, String tableName, boolean recreate, Column[] columns) throws SQLException {
		this.tableName = tableName;
		this.database = database;
		this.columns = columns;
		if (recreate) {
			this.drop();
			database.executeStatement("create table " + this.tableName + getStructure());
		}
		else {
			database.executeStatement("create table if not exists " + this.tableName + getStructure());
		}
	}

	public Table(Database database, String tableName, Column[] columns) throws SQLException {
		this(database, tableName, false, columns);
	}

	public void insert(Object... values) throws SQLException {
		database.executePreparedStatement("insert into " + getTableName() + " values(" + MySql.listToSql(values, true) + ")");
	}

	public void insert(String[] columnNames, Object[] values) throws SQLException {
		String stmt = "insert into " + getTableName() + " (" + MySql.listToSql(columnNames, false) + ") values(" + MySql.listToSql(values, true) + ")";
		Vars.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void insertAll(Column[] columns, Object[] values) throws SQLException {
		ArrayList<String> names = new ArrayList<String>();
		for (Column c : columns) {
			names.add(c.getName());
		}
		String stmt = "insert into " + getTableName() + " (" + MySql.listToSql(names.toArray(new String[names.size()]), false) + ") values(" + MySql.listToSql(values, true) + ")";
		Vars.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void replace(String[] columnNames, Object[] values) throws SQLException {
		String stmt = "replace into " + getTableName() + " (" + MySql.listToSql(columnNames, false) + ") values(" + MySql.listToSql(values, true) + ")";
		Vars.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void replaceAll(Column[] columns, Object[] values) throws SQLException {
		ArrayList<String> names = new ArrayList<String>();
		for (Column c : columns) {
			names.add(c.getName());
		}
		String stmt = "replace into " + getTableName() + " (" + MySql.listToSql(names.toArray(new String[names.size()]), false) + ") values(" + MySql.listToSql(values, true) + ")";
		Vars.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void removeRow(String column, String row) throws SQLException {
		database.executeStatement("delete from " + getTableName() + " where " + column + "=" + MySql.wrapQuotes(row));
	}

	public boolean containsRow(String column, String row) throws SQLException {
		ResultSet rs = database.executeQuery("select * from " + getTableName() + " where " + column + "=" + MySql.wrapQuotes(row));
		boolean b = rs.next();
		rs.getStatement().close();
		return b;
	}

	public int getRowCount() throws SQLException {
		ResultSet rs = database.executeQuery("select * from " + getTableName());
		int rowCount = 0;
		while (rs.next()) {
			rowCount++;
		}
		rs.getStatement().close();
		return rowCount;
	}

	public Object[] selectRow(int index) throws SQLException, RowNotFoundException {
		ResultSet rs = database.executeQuery("select * from " + getTableName());
		for (int i = 0; i <= index; i++) {
			if (!rs.next()) {
				throw new RowNotFoundException("Row at index " + index + " not found.");
			}
		}
		ArrayList<Object> values = new ArrayList<Object>();
		for (int i = 0; i < columns.length; i++) {
			values.add(rs.getObject(i + 1));
		}
		rs.getStatement().close();
		return values.toArray(new Object[values.size()]);
	}

	public Object[] selectRow(String columnName, String columnValue) throws SQLException {
		ResultSet rs = database.executeQuery("select * from " + getTableName() + " where " + columnName + "=" + MySql.wrapQuotes(columnValue));
		ArrayList<Object> values = new ArrayList<Object>();
		Vars.getLogger().info("selectRow success? " + rs.next());
		for (int i = 0; i < columns.length; i++) {
			values.add(rs.getObject(i + 1));
		}
		rs.getStatement().close();
		return values.toArray(new Object[values.size()]);
	}

	public Object selectFromRow(String returnColumn, String columnName, String columnValue) throws SQLException {
		ResultSet rs = database.executeQuery("select " + returnColumn + " from " + getTableName() + " where " + columnName + "=" + MySql.wrapQuotes(columnValue));
		Object o = rs.getObject(1);
		rs.getStatement().close();
		return o;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @return the database
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * @return the structure
	 */
	private String getStructure() {
		StringBuilder sb = new StringBuilder("(");
		for (Column c : columns) {
			sb.append(c).append(", ");
		}
		sb.delete(sb.lastIndexOf(", "), sb.length()).append(")");
		String finalStr = sb.toString();
		Vars.getLogger().fine(finalStr);
		return finalStr;
	}

	public final void drop() throws SQLException {
		database.executeStatement("drop table if exists " + getTableName());
	}
}
