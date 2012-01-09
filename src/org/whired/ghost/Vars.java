package org.whired.ghost;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.client.util.GhostFormatter;

/**
 * A collection of configurations
 * 
 * @author Whired
 */
public class Vars {

	public final static String INCORRECTPASSWORD = "The password entered was incorrect.";
	public final static String CONNECTED = "Connection successful.";
	public final static String NORESPONSE = "Connection failed; no response from server.";
	public final static String UNKNOWNHOST = "Connection failed; no server found.";
	public final static String UPDATEFAIL = "Unable to check for updates. This action requires an internet connection.";
	public final static String UPDATENEEDED = "An update has been found. Do '/renew' to update.";
	public final static String UPDATEOK = "Your version of jGHOST.Portable is up to date.";
	public final static int VERSION = 0;
	public final static String FS = System.getProperty("file.separator");
	public final static String LOCAL_CODEBASE = System.getProperty("user.home") + FS + ".ghost" + FS + "cache" + FS;
	private final static Logger logger = LoggerFactory.create();

	private static final class LoggerFactory {

		private final static Logger create() {
			Logger logger = Logger.getLogger("org.whired.ghost");
			logger.setLevel(Level.ALL);
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.ALL);
			ch.setFormatter(new GhostFormatter());
			logger.addHandler(ch);
			logger.setUseParentHandlers(false);
			return logger;
		}
	}

	/**
	 * Specifies whether or not debugging messages are logged
	 * 
	 * @param on whether or not to log all messages
	 */
	public static void setDebug(boolean on) {
		if (on) {
			logger.setLevel(Level.ALL);
			logger.finest("finest");
			logger.finer("finer");
			logger.fine("fine");
			logger.config("config");
			logger.info("info");
			logger.warning("warning");
			logger.severe("severe");
		}
		else {
			logger.setLevel(Level.INFO);
		}
		logger.info("Logger level is now: " + logger.getLevel());
	}

	public static final Logger getLogger() {
		return logger;
	}
}
