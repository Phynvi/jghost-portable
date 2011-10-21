package org.whired.ghost.client.util;

import java.io.*;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.RankDefinitions;

public class SessionSettings implements Serializable {

	public String chatLogDir = "";
	public String commandLogDir = "";
	public String pmLogDir = "";
	public String[] defaultConnect = new String[3];
	public boolean debugOn = false;
	private final Player player;
	private transient RankDefinitions ranks;
	
	public SessionSettings(Player player, RankDefinitions ranks) {
		this.player = player;
		this.ranks = ranks;
	}

	/**
	 * Lazy initialization of settings
	 *
	 * @return the settings that were initialized
	 */
	public static SessionSettings loadFromDisk(RankDefinitions ranks) throws IOException, ClassNotFoundException {
		SessionSettings settings;
		ObjectInputStream obj = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/.ghost/Settings.NS")));
		settings = (SessionSettings) obj.readObject();
		settings.setRanks(ranks);
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

	public RankDefinitions getRanks() {
		return this.ranks;
	}
	
	public void setRanks(RankDefinitions ranks) {
		this.ranks = ranks;
	}
	
	/**
	 * Gets the player representation of this user
	 * @return the player for this user
	 */
	public Player getPlayer() {
		return this.player;
	}
}
