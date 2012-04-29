package org.whired.ghostclient.io.sql;

/**
 * MySQL utilities
 * @author Whired
 */
public class MySql {
	public static String wrapQuotes(String str) {
		if (str == null) {
			return str;
		}
		if (!str.startsWith("'")) {
			str = "'" + str;
		}
		if (!str.endsWith("'") || str.equals("'")) {
			str += "'";
		}
		return str;
	}

	public static String listToSql(final Object[] values, final boolean wrapQuotes) {
		StringBuilder builder = new StringBuilder();
		for (final Object datum : values) {
			builder = builder.append(datum instanceof String && wrapQuotes ? MySql.wrapQuotes((String) datum) : datum).append(", ");
		}
		return builder.delete(builder.lastIndexOf(", "), builder.length()).toString();
	}
}
