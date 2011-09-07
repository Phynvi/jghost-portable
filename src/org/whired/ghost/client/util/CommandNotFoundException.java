package org.whired.ghost.client.util;

/**
 * Thrown when a command is parsed, but is not found
 * 
 * @author Whired
 */
public class CommandNotFoundException extends Exception
{
	public CommandNotFoundException(String message)
	{
		super(message);
	}
}
