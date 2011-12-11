package org.whired.ghost;

import org.whired.ghost.client.util.GhostFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A collection of configurations
 *
 * @author Whired
 */
public class Vars
{
	public final static String INCORRECTPASSWORD = "The password entered was incorrect.";
	public final static String CONNECTED = "Connection successful.";
	public final static String NORESPONSE = "Connection failed; no response from server.";
	public final static String UNKNOWNHOST = "Connection failed; no server found.";
	public final static String UPDATEFAIL = "Unable to check for updates. This action requires an internet connection.";
	public final static String UPDATENEEDED = "An update has been found. Do '/renew' to update.";
	public final static String UPDATEOK = "Your version of jGHOST.Portable is up to date.";
	public final static int VERSION = 0;
	private static volatile Logger logger = null;

	/**
	 * Specifies whether or not debugging messages are logged
	 *
	 * @param on whether or not to log all messages
	 */
	public static void setDebug(boolean on)
	{
		if(on)
		{
			getLogger().setLevel(Level.ALL);
			getLogger().finest("finest");
			getLogger().finer("finer");
			getLogger().fine("fine");
			getLogger().config("config");
			getLogger().info("info");
			getLogger().warning("warning");
			getLogger().severe("severe");
		}
		else
		{
			//logger.setLevel(Level.INFO);
			getLogger().setLevel(Level.INFO);
		}
		getLogger().info("Logger level is now: " + getLogger().getLevel());
	}

	public static Logger getLogger()
	{
		if(logger == null)
		{
			synchronized(Vars.class)
			{
				if(logger == null)
				{
					logger = Logger.getLogger("org.whired.ghost");
					logger.setLevel(Level.ALL);
					//ConsoleHandler ch = new ConsoleHandler();
					//ch.setLevel(Level.ALL);
					//ch.setFormatter(new GhostFormatter());
					//logger.addHandler(ch);
					logger.setUseParentHandlers(false);
				}
			}
		}
		return logger;
	}
}
