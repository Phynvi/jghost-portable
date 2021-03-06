package org.whired.ghostclient.client.settings;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghostclient.io.sql.Column;
import org.whired.ghostclient.io.sql.Database;
import org.whired.ghostclient.io.sql.Table;

/**
 * Contains methods for database transporting {@link SessionSettings}
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
	 * @param databaseDirectory the directory of the database
	 * @return the settings
	 */
	public static SessionSettings loadFromDatabase(final String databaseDirectory) {

		try {
			final Database db = new Database(databaseDirectory, DATABASE_NAME);

			final Table user = new Table(db, USER_TABLE_NAME, USER_TABLE_COLUMNS);
			final String userId = "0"; // TODO Remove later
			if (user.getRowCount() > 0) {
				Object[] val = user.selectRow("userId", userId);
				final GhostPlayer player = new GhostPlayer((String) val[1], (Integer) val[3]);
				final SessionSettings settings = new SessionSettings(player, (Integer) val[0]);
				settings.setTabOrder(decompressTabOrder((String) val[4]));
				try {
					final Table connection = new Table(db, CONNECTION_TABLE_NAME, CONNECTION_TABLE_COLUMNS);
					if (connection.getRowCount() > 0) {
						val = connection.selectRow("userId", userId);
						settings.defaultConnect[0] = (String) val[1];
						settings.defaultConnect[1] = (String) val[2];
						settings.defaultConnect[2] = (String) val[3];
					}
				}
				catch (final Throwable t) {
					Constants.getLogger().log(Level.WARNING, "Unable to load default connection for " + settings.getPlayer().getName(), t);
				}
				Constants.getLogger().info("Session loaded");
				return settings;
			}
		}
		catch (final Throwable t) {
			Constants.getLogger().log(Level.WARNING, "Unable to load settings from database: ", t);
		}
		Constants.getLogger().info("No session found, loading defaults");
		return new SessionSettings(new GhostPlayer("Admin", 6), 0);
	}

	private final static String DELIMITER = ", ";

	/**
	 * Decompresses a string array that has been compressed by {@link #compressTabOrder(String[])}
	 * @param compressed the compressed string to decompress
	 * @return the decompressed string array
	 */
	private static String[] decompressTabOrder(final String compressed) {
		return compressed.split(DELIMITER);
	}

	/**
	 * Compresses a string array so it can be written to the database
	 * @param decompressed the decompressed string array
	 * @return the compressed string
	 */
	private static String compressTabOrder(final String[] decompressed) {
		if (decompressed.length == 0) {
			return "";
		}
		final StringBuilder b = new StringBuilder();
		for (final String s : decompressed) {
			b.append(s).append(DELIMITER);
		}
		b.delete(b.lastIndexOf(DELIMITER), b.length());
		return b.toString();
	}

	/**
	 * Saves the specified settings to the database
	 * @param databaseDirectory the directory of the database
	 * @param settings the settings to save
	 */
	public static void saveToDatabase(final String databaseDirectory, final SessionSettings settings) {
		try {
			final Database db = new Database(databaseDirectory, DATABASE_NAME);
			final Table user = new Table(db, USER_TABLE_NAME, USER_TABLE_COLUMNS);
			user.replace(new String[] { "userId", "name", "pass", "rights", "taborder" }, new Object[] { settings.getUserId(), settings.getPlayer().getName(), "pass", settings.getPlayer().getRights(), compressTabOrder(settings.getTabOrder()) });
			final Table connection = new Table(db, CONNECTION_TABLE_NAME, CONNECTION_TABLE_COLUMNS);
			connection.replace(new String[] { "userId", "ip", "port", "pass" }, new Object[] { settings.getUserId(), settings.defaultConnect[0], settings.defaultConnect[1], settings.defaultConnect[2] });
			Constants.getLogger().info("Session saved");
		}
		catch (final Throwable t) {
			Constants.getLogger().log(Level.WARNING, "Unable to save settings to database: ", t);
		}
	}
}
