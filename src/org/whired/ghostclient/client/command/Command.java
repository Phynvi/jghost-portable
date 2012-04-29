package org.whired.ghostclient.client.command;

/**
 * Defines the layout of a command
 * @author Whired
 */
public abstract class Command {

	/** The identifier for this {@code Command} */
	private final String command;
	private final int minArgs;

	/**
	 * Constructs a new {@code Command} with the specified command string
	 * @param command the identifier for this command
	 * @param minArgs the minimum amount of arguments this command will require
	 */
	public Command(final String command, final int minArgs) {
		this.command = command;
		this.minArgs = minArgs;
	}

	/**
	 * Constructs a new {@code Command} with the specified command string and no parameters
	 * @param command the identifier for this command
	 */
	public Command(final String command) {
		this(command, 0);
	}

	/**
	 * Invoked when a command must be handled
	 * @param args the arguments for the command. If no arguments are parsed, this is {@code null}. Otherwise, each argument is guaranteed to have a {@code length} greater than zero.
	 */
	public abstract boolean handle(String[] args);

	public int getMinArgs() {
		return this.minArgs;
	}

	@Override
	public String toString() {
		return command;
	}
}
