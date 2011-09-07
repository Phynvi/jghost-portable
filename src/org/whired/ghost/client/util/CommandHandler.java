package org.whired.ghost.client.util;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Handles commands? TODO This might actually go in Command itself
 * @author Whired
 */
public class CommandHandler
{

	/** Contains all the commands */
	public static HashMap<String, Command> commands = new HashMap<String, Command>();

	private Logger log;

	/**
	 * Creates a new command handler with the specified logger
	 * @param log the logger to use
	 */
	public CommandHandler(Logger log)
	{
		this.log = log;
	}

	/**
	 * Creates a new command handler
	 */
	public CommandHandler(){}

	/**
	 * Parses and handles command from specified input string
	 * @param input the input to parse and handle
	 * @throws CommandMalformedException when the given input is in an invalid format
	 * @throws CommandNotFoundException  when the parsed command has not been added to {@code commands}
	 */
	public void handleInput(String input) throws CommandMalformedException, CommandNotFoundException
	{
		// Get rid of whitespace
		input = input.trim();

		// Check that command isn't empty
		if (!input.isEmpty())
		{
			String strCommand;
			Command command;
			String[] args = null;
			if (input.contains(" "))
			{
				// Get the actual command
				String[] tArr = input.split(" ");
				strCommand = tArr[0];
				String firstArg = tArr[1];

				// Get the arguments
				input = input.replace(strCommand + " ", "");

				if (input.contains(" "))
				{
					// Command has more than one argument
					args = input.split(" ");
				}
				else
				{
					// Command has only 1 argument
					args = new String[]
					{
						firstArg
					};
				}
			}
			else
			{
				// Command has no argument
				strCommand = input;
			}
			command = commands.get(strCommand);
			if (command == null)
			{
				throw new CommandNotFoundException(strCommand + " not found");
			}
			else
			{
				try
				{
					command.handle(args);
					String message = "Command successful: "+command;
					if(args != null)
					{
						for(String arg : args)
						{
							message += ">"+arg;
						}
					}
					log(Level.INFO, message);
				}
				catch(Exception e)
				{
					log(Level.WARNING, "Command failed: "+e.toString());
					if(e instanceof NullPointerException && args == null)
					{
						log(Level.INFO, "Are arguments required for this command?");
					}
					log(e);
				}
			}
		}
		else
		{
			throw new CommandMalformedException("Input cannot be empty");
		}
	}

	/** Whether or not to display debugging output */
	private boolean verbose = false;

	private void log(Level level, String message)
	{
		if(log == null)
			System.out.println(level.getName()+": "+message);
		else
			log.log(level, message);
	}

	private void log(Exception e)
	{
		if(isVerbose())
		{
			e.printStackTrace();
		}
	}

	public static void addCommand(Command command)
	{
		System.out.println("Adding command: "+command);
		commands.put(command.toString(), command);
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose()
	{
		return verbose;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}
}
