package org.whired.ghostclient.io.database;

/**
 * A table column
 * 
 * @author Whired
 */
public class Column {
	private final String toString;

	/**
	 * Creates a new column with the specified name and type
	 * 
	 * @param name the name of the column
	 * @param type the MySql type (IE {@code VARCHAR(20)})
	 */
	public Column(String name, String type) {
		toString = name + " " + type;
	}

	@Override
	public String toString() {
		return toString;
	}
}
