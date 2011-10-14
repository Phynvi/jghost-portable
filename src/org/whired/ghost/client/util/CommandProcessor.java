package org.whired.ghost.client.util;

import org.whired.ghost.Vars;
import org.whired.ghost.client.net.ClientConnection;
import org.whired.ghost.client.ui.GhostFrame;
// TODO implement contents as Commands and deprecate this class
public class CommandProcessor
{

	private final GhostFrame ghost;

	public CommandProcessor(String command, final GhostFrame ghost)
	{
		this.ghost = ghost;
		String[] commandSub = new String[4];
		/**
		 * Command actions are handled here.
		 */
		if (command.contains(" "))
		{
			commandSub = command.split(" ");
			int arrayIndex = 0;
			int charIndex = 0;

			//First command
			if (commandSub[0].equals("set"))
			{
				if (commandSub[1].equals("name"))
				{
					if (commandSub[2] != null)
					{
						ghost.getUser().getSettings().getPlayer().setName(commandSub[2]);
						SendSuccess(commandSub, commandSub[2]);
					}
					else
					{
						throwError(2, commandSub);
					}
				}
				else
				{
					if (commandSub[1].equals("path"))
					{
						if (commandSub[2].equals("pmlog"))
						{
							//ghost.getUser().getSettings().pmLogDir = ghost.getPathTo(); // TODO deprecate, this is server-sided.
							if (!ghost.getUser().getSettings().pmLogDir.equals(""))
							{
								SendSuccess(commandSub, ghost.getUser().getSettings().pmLogDir);
							}
							else
							{
								Vars.getLogger().info("PM log file: not set.");
							}
						}
						else
						{
							if (commandSub[2].equals("chatlog"))
							{
								//ghost.getUser().getSettings().chatLogDir = ghost.getPathTo();// TODO deprecate, this is server-sided.
								if (!ghost.getUser().getSettings().pmLogDir.equals(""))
								{
									SendSuccess(commandSub, ghost.getUser().getSettings().chatLogDir);
								}
								else
								{
									Vars.getLogger().info("Chat log file: not set.");
								}
							}
							else
							{
								if (commandSub[2].equals("commandlog"))
								{
									//ghost.getUser().getSettings().commandLogDir = ghost.getPathTo(); // TODO deprecate, this is server-sided.
									if (!ghost.getUser().getSettings().pmLogDir.equals(""))
									{
										SendSuccess(commandSub, ghost.getUser().getSettings().commandLogDir);
									}
									else
									{
										Vars.getLogger().info("Command log file: not set.");
									}
								}
								else
								{
									throwError(2, commandSub);
								}
							}
						}
					}
					else
					{
						if (commandSub[1].equals("debug"))
						{
							if (commandSub[2].equals("on"))
							{
								ghost.getUser().getSettings().debugOn = true;
								Vars.setDebug(true);
								SendSuccess(commandSub, commandSub[2]);
							}
							else
							{
								if (commandSub[2].equals("off"))
								{
									ghost.getUser().getSettings().debugOn = false;
									Vars.setDebug(false);
									SendSuccess(commandSub, commandSub[2]);
								}
								else
								{
									throwError(2, commandSub);
								}
							}
						}
						else
						{
							throwError(1, commandSub);
						}
					}
				}
			}
			else
			{
				if (commandSub[0].equals("connect"))
				{

				}
				else
				{
					if (commandSub[0].equals("open"))
					{
						if (commandSub[1].equals("commandlog"))
						{
							if (!ghost.getUser().getSettings().commandLogDir.equals(""))
							{
								try
								{
									Runtime.getRuntime().exec(new String[]
										   {
											   "cmd.exe", "/c", "\"" + ghost.getUser().getSettings().commandLogDir + "\""
										   });
								}
								catch (Exception a)
								{
									Vars.getLogger().warning("Unable to complete requested action!");
									Vars.getLogger().fine(a.getMessage());
								}
							}
							else
							{
								Vars.getLogger().warning("Command log file is not set! Do '/set' to fix this problem.");
							}
						}
						else
						{
							if (commandSub[1].equals("chatlog"))
							{
								if (!ghost.getUser().getSettings().chatLogDir.equals(""))
								{
									try
									{
										// TODO oh look, platform dependency fail
										Runtime.getRuntime().exec(new String[]
											   {
												   "cmd.exe", "/c", "\"" + ghost.getUser().getSettings().chatLogDir + "\""
											   });
									}
									catch (Exception a)
									{
										Vars.getLogger().warning("Unable to complete requested action!");
										Vars.getLogger().fine(a.getMessage());
									}
								}
								else
								{
									Vars.getLogger().warning("Chat log file is not set! Do '/set' to fix this problem.");
								}
							}
							else
							{
								if (commandSub[1].equals("pmlog"))
								{
									if (!ghost.getUser().getSettings().pmLogDir.equals(""))
									{
										try
										{
											Runtime.getRuntime().exec(new String[]
												   {
													   "cmd.exe", "/c", "\"" + ghost.getUser().getSettings().pmLogDir + "\""
												   });
										}
										catch (Exception a)
										{
											Vars.getLogger().warning("Unable to complete requested action!");
											Vars.getLogger().fine(a.getMessage());
										}
									}
									else
									{
										Vars.getLogger().warning("PM log file is not set! Do '/set' to fix this problem.");
									}
								}
								else
								{
									throwError(1, commandSub);
								}
							}
						}
					}
					else
					{
						if (commandSub[0].equals("get"))
						{
							if (commandSub[1].equals("memusg"))
							{
								System.gc();
								Runtime runtime = Runtime.getRuntime();
								Vars.getLogger().info("Memory usage " + (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L) + "KB");
							}
							else
							{
								throwError(1, commandSub);
							}
						}
						else
						{
							throwError(0, commandSub);
						}
					}
				}
			}
		}
		else
		{
			//All one word commands below.
			if (command.equals("help"))
			{
				Vars.getLogger().info("List of commands and their usages: "); // TODO propogate this
			}
			else
			{
				if (command.equals("exit") || command.equals("quit") || command.equals("q"))
				{
					ghost.requestExit();
				}
				else
				{
					if (command.equals("test"))
					{
						try
						{
							//ghost.getConnection().sendPacket(88, new Player("Derek", 3));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						if (command.equals("connect"))
						{
							if (ghost.getUser().getSettings().defaultConnect[0] != null && ghost.getUser().getSettings().defaultConnect[1] != null && ghost.getUser().getSettings().defaultConnect[2] != null)
							{
								try
								{
									ghost.setConnection(ClientConnection.connect(ghost.getUser().getSettings().defaultConnect[0], Integer.parseInt(ghost.getUser().getSettings().defaultConnect[1]), ghost.getUser().getSettings().defaultConnect[2], ghost));
								}
								catch (Exception e)
								{
									// TODO handle properly
									Vars.getLogger().warning("Unable to connect to "+ghost.getUser().getSettings().defaultConnect[0]+":"+Integer.parseInt(ghost.getUser().getSettings().defaultConnect[1]));
								}
								//System.out.println(ghost.connectionState); // TODO make connectionState part of Connection (connection has a state)
							}
							else
							{
								Vars.getLogger().warning("There is no default connection saved.");
							}
						}
						else
						{
							Vars.getLogger().warning("User command invalid: " + command);
						}
					}
				}
			}
		}
	}

	/**
	 * Informs the user of an error.
	 */
	private void throwError(int index, String[] error)
	{
		String sIndex = null;
		String fullCommand = "";
		String join = ">";
		for (String s : error)
		{
			fullCommand += s + join;
		}
		fullCommand = fullCommand.substring(0, fullCommand.lastIndexOf(join));
		switch (index)
		{
			case 0:
				sIndex = "first";
				break;
			case 1:
				sIndex = "second";
				break;
			case 2:
				sIndex = "third";
				break;
			case 3:
				sIndex = "fourth";
				break;
		}
		if (sIndex != null)
		{
			Vars.getLogger().warning("User command invalid: " + fullCommand + " - " + sIndex + " command, '" + error[index] + "', is invalid.");
		}
		printHelp(fullCommand);
	}

	/**
	 * Notify user of success
	 */
	private void SendSuccess(String[] success, String setting)
	{
		String fullCommand = "";
		String join = ">";
		for (String s : success)
		{
			fullCommand += s + join;
		}
		fullCommand = fullCommand.substring(0, fullCommand.lastIndexOf(join));
		Vars.getLogger().info(fullCommand + " successful (" + setting + ").");
	}

	/**
	 * Command guides are handled here.
	 */
	private void printHelp(String command)
	{
		if (command.contains("connect"))
		{
			Vars.getLogger().info("<CONNECT>");
			Vars.getLogger().info("'/connect [ip] [port] [password]' - Connects to the IP and port specified. Password must match server's password.");
			if (ghost.getUser().getSettings().defaultConnect[0] != null)
			{
				Vars.getLogger().info("'/connect' to connect to " + ghost.getUser().getSettings().defaultConnect[0] + ":" + ghost.getUser().getSettings().defaultConnect[1] + ".");
			}
		}
		else
		{
			if (command.contains("set"))
			{
				Vars.getLogger().info("<SET>");
				Vars.getLogger().info("'/set name [desiredname]' - Sets your display name for the server.");
				Vars.getLogger().info("'/set path {commandlog} {pmlog} {chatlog}' - Sets any 3 of the log directories.");
				Vars.getLogger().info("'/set debug {on} {off}' - Turns debug mode on and off.");
			}
		}
		//Continue else-ifs.
	}
}
