package org.whired.ghost.constants;

import java.io.File;
import java.text.SimpleDateFormat;
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

	public final static String FS = System.getProperty("file.separator");
	private final static String LOCAL_CODEBASE = ".ghost" + FS + "cache" + FS;
	private final static Logger logger = LoggerFactory.create();
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M k:mm");

	public final static String getLocalCodebase() {
		File f = new File(LOCAL_CODEBASE);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getAbsolutePath() + FS;
	}

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
