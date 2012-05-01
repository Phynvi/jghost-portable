package org.whired.ghostclient.client.command;

import java.util.HashMap;
import java.util.logging.Level;

import org.whired.ghost.Constants;

/**
 * Handles commands
 * @author Whired
 */
public class CommandManager {

	/**
	 * Contains all the commands
	 */
	private final HashMap<String, Command> commands = new HashMap<String, Command>();

	/**
	 * Parses and handles command from specified input string
	 * @param input the input to parse and handle
	 * @throws CommandMalformedException when the given input is in an invalid format
	 * @throws CommandNotFoundException when the parsed command has not been added to {@code commands}
	 */
	public void handleInput(String input) throws CommandMalformedException, CommandNotFoundException {
		// Get rid of whitespace
		input = input.trim();

		// Check that command isn't empty
		if (!input.isEmpty()) {
			if (input.equals("help") || input.equals("?")) {
				Constants.getLogger().info("Registered commands:");
				for (String cmd : commands.keySet()) {
					Constants.getLogger().info(cmd);
				}
			}
			else {
				String strCommand;
				Command command;
				String[] args = null;
				if (input.contains(" ")) {
					// Get the actual command
					final String[] tArr = input.split(" ");
					strCommand = tArr[0];
					final String firstArg = tArr[1];

					// Get the arguments
					input = input.substring(strCommand.length() + 1, input.length());

					if (input.contains(" ")) {
						// Command has more than one argument
						args = input.split(" ");
					}
					else {
						// Command has only 1 argument
						args = new String[] { firstArg };
					}
				}
				else {
					// Command has no argument
					strCommand = input;
				}
				command = commands.get(strCommand);
				if (command == null) {
					throw new CommandNotFoundException(strCommand + " not found");
				}
				else if (command.getMinArgs() > 0 && args != null && args.length >= command.getMinArgs() || command.getMinArgs() == 0) {
					try {
						final boolean success = command.handle(args);
						if (!success) {
							String message = "Command failed: " + command;
							if (args != null) {
								for (final String arg : args) {
									message += ">" + arg;
								}
							}
							Constants.getLogger().log(Level.WARNING, message);
						}
					}
					catch (final Exception e) {
						Constants.getLogger().log(Level.WARNING, "Command failed: {0}", e.toString());
						if (e instanceof NullPointerException && args == null) {
							Constants.getLogger().log(Level.INFO, "Are arguments required for this command?");
						}
						Constants.getLogger().log(Level.FINE, null, e);
					}
				}
				else {
					Constants.getLogger().log(Level.WARNING, "Command failed. Minimum arguments: " + command.getMinArgs());
				}
			}
		}
		else {
			throw new CommandMalformedException("Input cannot be empty");
		}
	}

	public void registerCommand(final Command command) {
		Constants.getLogger().log(Level.INFO, "Registering command: {0}", command);
		commands.put(command.toString(), command);
	}

	public void registerCommands(final Command[] commands) {
		for (final Command c : commands) {
			registerCommand(c);
		}
	}
}
