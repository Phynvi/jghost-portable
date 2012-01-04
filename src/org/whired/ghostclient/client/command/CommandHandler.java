package org.whired.ghostclient.client.command;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.Vars;

/**
 * Handles commands
 *
 * @author Whired
 */
public class CommandHandler {

	/**
	 * Contains all the commands
	 */
	private HashMap<String, Command> commands = new HashMap<String, Command>();

	/**
	 * Parses and handles command from specified input string
	 * @param input the input to parse and handle
	 * @throws CommandMalformedException when the given input is in an invalid format
	 * @throws CommandNotFoundException  when the parsed command has not been added to {@code commands}
	 */
	public void handleInput(String input) throws CommandMalformedException, CommandNotFoundException {
		// Get rid of whitespace
		input = input.trim();

		// Check that command isn't empty
		if (!input.isEmpty()) {
			String strCommand;
			Command command;
			String[] args = null;
			if (input.contains(" ")) {
				// Get the actual command
				String[] tArr = input.split(" ");
				strCommand = tArr[0];
				String firstArg = tArr[1];

				// Get the arguments
				input = input.substring(strCommand.length() + 1, input.length());

				if (input.contains(" "))
					// Command has more than one argument
					args = input.split(" ");
				else
					// Command has only 1 argument
					args = new String[]{
						firstArg
					};
			}
			else
				// Command has no argument
				strCommand = input;
			command = commands.get(strCommand);
			if (command == null)
				throw new CommandNotFoundException(strCommand + " not found");
			else
				if ((command.getMinArgs() > 0 && args != null && args.length >= command.getMinArgs()) || (command.getMinArgs() == 0))
					try {
						boolean success = command.handle(args);
						String message = "Command " + (success ? "successful" : "failed") + ": " + command;
						if (args != null)
							for (String arg : args)
								message += ">" + arg;
						Vars.getLogger().log(Level.INFO, message);
					}
					catch (Exception e) {
						Vars.getLogger().log(Level.WARNING, "Command failed: {0}", e.toString());
						if (e instanceof NullPointerException && args == null)
							Vars.getLogger().log(Level.INFO, "Are arguments required for this command?");
						Vars.getLogger().log(Level.FINE, null, e);
					}
				else
					Vars.getLogger().log(Level.WARNING, "Command failed. Minimum arguments: " + command.getMinArgs());
		}
		else
			throw new CommandMalformedException("Input cannot be empty");
	}

	public void registerCommand(Command command) {
		Vars.getLogger().log(Level.INFO, "Adding command: {0}", command);
		commands.put(command.toString(), command);
	}

	public void registerCommands(Command[] commands) {
		for(Command c : commands)
			registerCommand(c);
	}
}
