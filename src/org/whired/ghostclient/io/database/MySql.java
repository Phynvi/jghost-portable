package org.whired.ghostclient.io.database;

import org.whired.ghost.constants.Vars;

/**
 * MySQL utilities
 * 
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

	public static String listToSql(Object[] values, boolean wrapQuotes) {
		StringBuilder builder = new StringBuilder();
		for (Object datum : values) {
			builder = builder.append(datum instanceof String && wrapQuotes ? MySql.wrapQuotes((String) datum) : datum).append(", ");
		}
		builder.delete(builder.lastIndexOf(", "), builder.length());
		String built = builder.toString();
		Vars.getLogger().fine("values: " + built);
		return built;
	}
}
