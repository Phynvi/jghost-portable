package org.whired.ghostclient.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.whired.ghost.Vars;
import org.whired.ghost.client.net.ClientConnection;
import org.whired.ghost.client.util.Command;
import org.whired.ghost.client.util.CommandHandler;
import org.whired.ghost.net.model.player.MapPlayer;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;

/**
 * Contains the logic for driving the UI
 * @author Whired
 */
public class GhostFrameImpl extends GhostFrameUI
{

	public GhostFrameImpl()
	{
		initCommands();
	}

	/**
	 * Initializes the commands that this frame will utilize
	 */
	public void initCommands()
	{
		CommandHandler.addCommand(new Command("test")
		{

			@Override
			public void handle(String[] args)
			{
				try
				{
					chatOutput.getDocument().insertString(0, "args", null);
				}
				catch (BadLocationException ex)
				{
					Logger.getLogger(GhostFrameUI.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		CommandHandler.addCommand(new Command("setrights")
		{

			@Override
			public void handle(String[] args)
			{
				getUser().getSettings().getPlayer().setRights(Integer.parseInt(args[0]));
			}
		});
		CommandHandler.addCommand(new Command("setname")
		{

			@Override
			public void handle(String[] args)
			{
				getUser().getSettings().getPlayer().setName(args[0]);
			}
		});
		CommandHandler.addCommand(new Command("pm")
		{

			@Override
			public void handle(String[] args)
			{
				String message = "";
				for (int i = 1; i < args.length; i++)
				{
					message += args[i] + " ";
				}
				displayPrivateChat(getUser().getSettings().getPlayer(), new Player(args[0], 0), message);
			}
		});
		CommandHandler.addCommand(new Command("connect")
		{

			@Override
			public void handle(String[] args)
			{
				if (args == null)
				{
					String[] con = getUser().getSettings().defaultConnect;
					try
					{
						setConnection(ClientConnection.connect(con[0], Integer.parseInt(con[1]), con[2], GhostFrameImpl.this));
					}
					catch (Exception e)
					{
						Vars.getLogger().warning("Unable to connect to " + con[0] + ":" + con[1]+" - "+e.toString());
						Vars.getLogger().fine(e.getMessage());
					}
				}
				else if (args.length > 0 && args[0] != null)
				{
					if (args.length > 1 && args[1] != null)
					{
						if (args.length > 2 && args[2] != null)
						{
							int port = -1;
							try
							{
								port = Integer.parseInt(args[1]);
							}
							catch (Exception e)
							{
								Vars.getLogger().fine("Exception " + e.getMessage());
								return;
							}
							Vars.getLogger().info("Use /connect to quickly connect to this IP in the future.");
							try
							{
								setConnection(ClientConnection.connect(args[0], port, args[2], GhostFrameImpl.this));
								Vars.getLogger().info("Successfully connected to " + args[0] + ":" + port);
							}
							catch (Exception e)
							{//TODO handle properly
								Vars.getLogger().warning("Unable to connect to " + args[0] + ":" + port);
								Vars.getLogger().fine(e.getMessage());
							}
							getUser().getSettings().defaultConnect[0] = args[0];
							getUser().getSettings().defaultConnect[1] = Integer.toString(port);
							getUser().getSettings().defaultConnect[2] = args[2];
						}
						else
						{
							Vars.getLogger().warning("Please specify a password.");
						}
					}
					else
					{
						Vars.getLogger().warning("Please specify a port.");
					}
				}
				else
				{
					Vars.getLogger().warning("Please specify an IP.");
				}
			}
		});
		loadBoundPackets();
	}

	/**
	 * Loads saved packets from the disk and binds them
	 */
	private void loadBoundPackets()
	{
		for (ReflectionPacketContainer packet : ReflectionPacketContainer.loadAllContainers())
		{
			bindPacket(packet);
		}
	}

	@Override
	protected void bindPacket(final ReflectionPacketContainer packet)
	{
		JRoundedButton button = new JRoundedButton(packet.packetName);
		button.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				ReflectionPacketContainer.invoke(packet, getConnection());
			}
		});
		boundPacketPanel.add(button);
	}

	@Override
	public void restartButActionPerformed(ActionEvent evt)
	{
		playerList.addElement(new MapPlayer("Whired", 4, 2000, 2000, map));
	}

	/**
	 * Displays a chat message to {@code chatOutput}
	 * @param rights the rights of he user
	 * @param name the name of the user
	 * @param message the message the user sent
	 */
	@Override
	public void displayPublicChat(Player sender, String message)
	{
		Icon i = rightsIcons[sender.getRights()];
		try
		{
			Style iconOnly = chatOutput.getStyledDocument().getStyle("iconOnly");
			if (iconOnly == null)
			{
				iconOnly = chatOutput.getStyledDocument().addStyle("iconOnly", null);
			}
			StyleConstants.setIcon(iconOnly, i);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), "dummytext", iconOnly);
			StyleConstants.setBold(chatOutput.getInputAttributes(), true);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), sender.getName(), chatOutput.getInputAttributes());
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
		}
		catch (Exception e)
		{
			Vars.getLogger().severe("Unable to display chat:");
			e.printStackTrace();
		}
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message)
	{
		if (new PrivateChatPacket(getConnection()).send(sender, recipient, message))
		{
			Icon senderIcon = rightsIcons[sender.getRights()];
			Icon recpIcon = rightsIcons[recipient.getRights()];
			try
			{
				Style iconOnly = pmOutput.getStyledDocument().getStyle("iconOnly");
				if (iconOnly == null)
				{
					iconOnly = pmOutput.getStyledDocument().addStyle("iconOnly", null);
				}
				StyleConstants.setIcon(iconOnly, senderIcon);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), "dummytext", iconOnly);
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), sender.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " to ", null);
				if (recipient.getRights() > 0)
				{
					StyleConstants.setIcon(iconOnly, recpIcon);
					pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), "dummytext", iconOnly);
				}
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), recipient.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
			}
			catch (Exception e)
			{
				Vars.getLogger().severe("Unable to display chat:");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void menuActionPerformed(int i, ActionEvent e)
	{
		switch (i)
		{
			case 0: //Connect with dialog
				tryExtendedConnect();
				break;
			case 1: //Connect to default
				doCommand("connect");
				break;
			case 2:
				// TODO Send the file request packet here
				break;
			case 3:
				if (JOptionPane.showConfirmDialog(this.getOwner(), "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					System.exit(0);
				}
				break;
			case 4:
				displayReflectionManager();
				break;
		}
	}
}
