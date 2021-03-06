package org.whired.ghostclient.client.command;

/**
 * Thrown when a command is parsed, but is not found
 * @author Whired
 */
public class CommandNotFoundException extends Exception {
	public CommandNotFoundException(final String message) {
		super(message);
	}
}
