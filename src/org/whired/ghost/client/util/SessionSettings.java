package org.whired.ghost.client.util;

import java.io.*;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.RankHandler;

public class SessionSettings implements Serializable {

	public String chatLogDir = "";
	public String commandLogDir = "";
	public String pmLogDir = "";
	public String[] defaultConnect = new String[3];
	public boolean debugOn = false;
	private final Player player;
	
	public SessionSettings(Player player) {
		this.player = player;
	}

	/**
	 * Lazy initialization of settings
	 *
	 * @return the settings that were initialized
	 */
	public static SessionSettings loadFromDisk() throws IOException, ClassNotFoundException {
		SessionSettings settings;
		ObjectInputStream obj = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/.ghost/Settings.NS")));
		settings = (SessionSettings) obj.readObject();
		obj.close();
		return settings;
	}

	public static void saveToDisk(SessionSettings ds) throws FileNotFoundException, IOException {
		File f = new File(System.getProperty("user.home") + "/.ghost/");
		if (!f.exists())
			f.mkdirs();
		ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + "/.ghost/Settings.NS"));
		obj.writeObject(ds);
	}
	
	/**
	 * Gets the player representation of this user
	 * @return the player for this user
	 */
	public Player getPlayer() {
		return this.player;
	}
}
