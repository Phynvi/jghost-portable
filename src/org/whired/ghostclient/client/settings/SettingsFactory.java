package org.whired.ghostclient.client.settings;

import java.util.logging.Level;

import org.whired.ghost.constants.Vars;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghostclient.io.database.Column;
import org.whired.ghostclient.io.database.Database;
import org.whired.ghostclient.io.database.Table;

/**
 * Contains methods for database transporting {@link SessionSettings}
 * 
 * @author Whired
 */
public class SettingsFactory {

	private static final String DATABASE_NAME = "Settings.db";

	private static final String USER_TABLE_NAME = "User";
	private static final Column[] USER_TABLE_COLUMNS = new Column[] { new Column("userId", "INTEGER PRIMARY KEY"), new Column("name", "VARCHAR(12) UNIQUE"), new Column("pass", "VARCHAR(50)"), new Column("rights", "TINYINT(255)"), new Column("taborder", "VARCHAR(50)") };

	private static final String CONNECTION_TABLE_NAME = "Connection";
	private static final Column[] CONNECTION_TABLE_COLUMNS = new Column[] { new Column("userId", "INTEGER PRIMARY KEY"), new Column("ip", "VARCHAR(20)"), new Column("port", "VARCHAR(20)"), new Column("pass", "VARCHAR(50)") };

	/**
	 * Loads settings from the database
	 * 
	 * @param databaseDirectory the directory of the database
	 * @return the settings
	 */
	public static SessionSettings loadFromDatabase(String databaseDirectory) {

		try {
			Database db = new Database(databaseDirectory, DATABASE_NAME);

			Table user = new Table(db, USER_TABLE_NAME, USER_TABLE_COLUMNS);
			final String userId = "0"; // TODO Remove later
			if (user.getRowCount() > 0) {
				Object[] val = user.selectRow("userId", userId);
				Player player = new Player((String) val[1], (Integer) val[3]);
				SessionSettings settings = new SessionSettings(player, (Integer) val[0]);
				settings.setTabOrder(decompressTabOrder((String) val[4]));
				try {
					Table connection = new Table(db, CONNECTION_TABLE_NAME, CONNECTION_TABLE_COLUMNS);
					if (connection.getRowCount() > 0) {
						val = connection.selectRow("userId", userId);
						settings.defaultConnect[0] = (String) val[1];
						settings.defaultConnect[1] = (String) val[2];
						settings.defaultConnect[2] = (String) val[3];
					}
				}
				catch (Throwable t) {
					Vars.getLogger().log(Level.WARNING, "Unable to load default connection for " + settings.getPlayer().getName(), t);
				}
				Vars.getLogger().info("Session loaded");
				return settings;
			}
		}
		catch (Throwable t) {
			Vars.getLogger().log(Level.WARNING, "Unable to load settings from database: ", t);
		}
		Vars.getLogger().info("No session found, loading defaults");
		return new SessionSettings(new Player("Admin", 6), 0);
	}

	private final static String DELIMITER = ", ";

	/**
	 * Decompresses a string array that has been compressed by
	 * {@link #compressTabOrder(String[])}
	 * 
	 * @param compressed the compressed string to decompress
	 * @return the decompressed string array
	 */
	private static String[] decompressTabOrder(String compressed) {
		return compressed.split(DELIMITER);
	}

	/**
	 * Compresses a string array so it can be written to the database
	 * 
	 * @param decompressed the decompressed string array
	 * @return the compressed string
	 */
	private static String compressTabOrder(String[] decompressed) {
		if (decompressed.length == 0) {
			return "";
		}
		StringBuilder b = new StringBuilder();
		for (String s : decompressed) {
			b.append(s).append(DELIMITER);
		}
		b.delete(b.lastIndexOf(DELIMITER), b.length());
		return b.toString();
	}

	/**
	 * Saves the specified settings to the database
	 * 
	 * @param databaseDirectory the directory of the database
	 * @param settings the settings to save
	 */
	public static void saveToDatabase(String databaseDirectory, SessionSettings settings) {
		try {
			Database db = new Database(databaseDirectory, DATABASE_NAME);
			Table user = new Table(db, USER_TABLE_NAME, USER_TABLE_COLUMNS);
			user.replace(new String[] { "userId", "name", "pass", "rights", "taborder" }, new Object[] { settings.getUserId(), settings.getPlayer().getName(), "pass", settings.getPlayer().getRights(), compressTabOrder(settings.getTabOrder()) });
			Table connection = new Table(db, CONNECTION_TABLE_NAME, CONNECTION_TABLE_COLUMNS);
			connection.replace(new String[] { "userId", "ip", "port", "pass" }, new Object[] { settings.getUserId(), settings.defaultConnect[0], settings.defaultConnect[1], settings.defaultConnect[2] });
			Vars.getLogger().info("Session saved");
		}
		catch (Throwable t) {
			Vars.getLogger().log(Level.WARNING, "Unable to save settings to database: ", t);
		}
	}
}
