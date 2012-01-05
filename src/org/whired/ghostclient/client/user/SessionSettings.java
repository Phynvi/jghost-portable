package org.whired.ghostclient.client.user;

import java.io.*;
import org.whired.ghost.net.model.player.Player;

public class SessionSettings implements Serializable {

	public String[] defaultConnect = new String[3];
	public boolean debugOn = false;
	private final Player player;
	private String[] tabOrder;
	
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

	public void saveToDisk(String path) throws FileNotFoundException, IOException {
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
		ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + "/.ghost/Settings.NS"));
		obj.writeObject(this);
	}
	
	/**
	 * Gets the player representation of this user
	 * @return the player for this user
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Sets the order of the tabs on the view
	 * @param tabs the tabs, in order
	 */
	public void setTabOrder(String[] tabs) {
		tabOrder = tabs;
	}
	
	/**
	 * Gets the names of tabs in their preferred order
	 * @return the names of the tabs
	 */
	public String[] getTabOrder() {
		return tabOrder;
	}
}
