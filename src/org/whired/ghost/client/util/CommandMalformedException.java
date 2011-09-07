package org.whired.ghost.client.util;

/**
 * The exception that is thrown when a command does not follow the proper structure
 * 
 * @author Whired
 */
public class CommandMalformedException extends Exception
{
	public CommandMalformedException(String message)
	{
		super(message);
	}
}
