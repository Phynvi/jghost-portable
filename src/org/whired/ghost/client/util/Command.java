package org.whired.ghost.client.util;

import java.util.logging.Logger;

/**
 * Defines the layout of a command
 *
 * @author Whired
 */
public abstract class Command
{
	/** The identifier for this {@code Command} */
	private final String command;
	private final int minArgs;

	/**
	 * Constructs a new {@code Command} with the specified command string
	 *
	 * @param command the identifier for this command
	 */
	public Command(String command, int minArgs)
	{
		this.command = command;
		this.minArgs = minArgs;
	}

	/**
	 * Invoked when a command must be handled
	 *
	 * @param args the arguments for the command. If no arguments are parsed, this is {@code null}.
	 *	Otherwise, each argument is guaranteed to have a {@code length} greater than zero.
	 */
	public abstract boolean handle(String[] args);

	public void printFailure(Logger log, String reason)
	{
		log.warning("Command failed: "+reason);
	}

	public int getMinArgs()
	{
		return this.minArgs;
	}
	
	@Override
	public String toString()
	{
		return command;
	}
}
