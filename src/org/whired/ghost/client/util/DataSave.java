package org.whired.ghost.client.util;

import org.whired.ghost.Vars;
import java.io.*;
import org.whired.ghost.net.model.player.Player;

public class DataSave implements Serializable
{
	public String chatLogDir = "";
	public String commandLogDir = "";
	public String pmLogDir = "";
	public String[] defaultConnect = new String[3];
	public boolean debugOn = false;
	private Player player = new Player("Admin", 4);

	/**
	 * Lazy initialization of settings
	 *
	 * @return the settings that were initialized
	 */
	public static DataSave getSettings()
	{
		DataSave ds = null;
		try
		{
			ObjectInputStream obj = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/.ghost/Settings.NS")));
			ds = (DataSave) obj.readObject();
			obj.close();
		}
		catch (Exception e)
		{
		}
		if (ds == null)
		{
			ds = new DataSave();
		}
		Vars.setDebug(ds.debugOn);
		return ds;
	}

	public static void saveSettings() throws FileNotFoundException, IOException
	{
		ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + "/.ghost/Settings.NS"));
		obj.writeObject(getSettings());
	}

	/**
	 * Gets the player representation of this user
	 * @return the player for this user
	 */
	public Player getPlayer()
	{
		return this.player;
	}
}
