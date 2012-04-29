package org.whired.ghostclient.io.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.whired.ghost.Constants;

/**
 * Represents a MySQL table
 * @author Whired
 */
public class Table {

	private final String tableName;
	private final Database database;
	private final Column[] columns;

	/**
	 * Creates a new table in the specified database with the specified name
	 * @param database the database to add this table to
	 * @param tableName the name for the table
	 * @param structure the structure
	 * @param recreate whether or not to recreate the table if it already exists
	 */
	public Table(final Database database, final String tableName, final boolean recreate, final Column[] columns) throws SQLException {
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

	public Table(final Database database, final String tableName, final Column[] columns) throws SQLException {
		this(database, tableName, false, columns);
	}

	public void insert(final Object... values) throws SQLException {
		database.executePreparedStatement("insert into " + getTableName() + " values(" + MySql.listToSql(values, true) + ")");
	}

	public void insert(final String[] columnNames, final Object[] values) throws SQLException {
		final String stmt = "insert into " + getTableName() + " (" + MySql.listToSql(columnNames, false) + ") values(" + MySql.listToSql(values, true) + ")";
		Constants.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void insertAll(final Column[] columns, final Object[] values) throws SQLException {
		final ArrayList<String> names = new ArrayList<String>();
		for (final Column c : columns) {
			names.add(c.getName());
		}
		final String stmt = "insert into " + getTableName() + " (" + MySql.listToSql(names.toArray(new String[names.size()]), false) + ") values(" + MySql.listToSql(values, true) + ")";
		Constants.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void replace(final String[] columnNames, final Object[] values) throws SQLException {
		final String stmt = "replace into " + getTableName() + " (" + MySql.listToSql(columnNames, false) + ") values(" + MySql.listToSql(values, true) + ")";
		Constants.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void replaceAll(final Column[] columns, final Object[] values) throws SQLException {
		final ArrayList<String> names = new ArrayList<String>();
		for (final Column c : columns) {
			names.add(c.getName());
		}
		final String stmt = "replace into " + getTableName() + " (" + MySql.listToSql(names.toArray(new String[names.size()]), false) + ") values(" + MySql.listToSql(values, true) + ")";
		Constants.getLogger().fine("Execute: " + stmt);
		database.executePreparedStatement(stmt);
	}

	public void removeRow(final String column, final String row) throws SQLException {
		database.executeStatement("delete from " + getTableName() + " where " + column + "=" + MySql.wrapQuotes(row));
	}

	public boolean containsRow(final String column, final String row) throws SQLException {
		final ResultSet rs = database.executeQuery("select * from " + getTableName() + " where " + column + "=" + MySql.wrapQuotes(row));
		final boolean b = rs.next();
		rs.getStatement().close();
		return b;
	}

	public int getRowCount() throws SQLException {
		final ResultSet rs = database.executeQuery("select * from " + getTableName());
		int rowCount = 0;
		while (rs.next()) {
			rowCount++;
		}
		rs.getStatement().close();
		return rowCount;
	}

	public Object[] selectRow(final int index) throws SQLException, RowNotFoundException {
		final ResultSet rs = database.executeQuery("select * from " + getTableName());
		for (int i = 0; i <= index; i++) {
			if (!rs.next()) {
				throw new RowNotFoundException("Row at index " + index + " not found.");
			}
		}
		final ArrayList<Object> values = new ArrayList<Object>();
		for (int i = 0; i < columns.length; i++) {
			values.add(rs.getObject(i + 1));
		}
		rs.getStatement().close();
		return values.toArray(new Object[values.size()]);
	}

	public Object[] selectRow(final String columnName, final String columnValue) throws SQLException {
		final ResultSet rs = database.executeQuery("select * from " + getTableName() + " where " + columnName + "=" + MySql.wrapQuotes(columnValue));
		final ArrayList<Object> values = new ArrayList<Object>();
		Constants.getLogger().info("selectRow success? " + rs.next());
		for (int i = 0; i < columns.length; i++) {
			values.add(rs.getObject(i + 1));
		}
		rs.getStatement().close();
		return values.toArray(new Object[values.size()]);
	}

	public Object selectFromRow(final String returnColumn, final String columnName, final String columnValue) throws SQLException {
		final ResultSet rs = database.executeQuery("select " + returnColumn + " from " + getTableName() + " where " + columnName + "=" + MySql.wrapQuotes(columnValue));
		final Object o = rs.getObject(1);
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
		final StringBuilder sb = new StringBuilder("(");
		for (final Column c : columns) {
			sb.append(c).append(", ");
		}
		sb.delete(sb.lastIndexOf(", "), sb.length()).append(")");
		final String finalStr = sb.toString();
		Constants.getLogger().fine(finalStr);
		return finalStr;
	}

	public final void drop() throws SQLException {
		database.executeStatement("drop table if exists " + getTableName());
	}
}
