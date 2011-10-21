package org.whired.ghostclient.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.whired.ghost.Vars;
import org.whired.ghost.client.net.ClientConnection;
import org.whired.ghost.client.util.Command;
import org.whired.ghost.client.util.CommandHandler;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.MapPlayer;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.Rank;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;

/**
 * Contains the logic for driving the UI
 *
 * @author Whired
 */
public class GhostFrameImpl extends GhostFrameUI {

	public GhostFrameImpl() {
		initCommands();
	}

	/**
	 * Initializes the commands that this frame will utilize
	 */
	public void initCommands() {
		CommandHandler.addCommand(new Command("setrights", 1) {

			@Override
			public boolean handle(String[] args) {
				getUser().getSettings().getPlayer().setRights(Integer.parseInt(args[0]));
				return true;
			}
		});
		CommandHandler.addCommand(new Command("disconnect", 0) {
			public boolean handle(String[] args) {
				Connection c = getConnection();
				if(c != null) {
					c.terminationRequested("User requested.");
					return true;
				}
				else {
					Vars.getLogger().info("No connection to server currently exists");
					return false;
				}
			}
		});
		CommandHandler.addCommand(new Command("setname", 1) {

			@Override
			public boolean handle(String[] args) {
				StringBuilder finalName = new StringBuilder();
				for(String s : args)
				{
					finalName.append(s);
					finalName.append(" ");
				}
				getUser().getSettings().getPlayer().setName(finalName.toString().trim());
				return true;
			}
		});
		CommandHandler.addCommand(new Command("pm", 2) {

			@Override
			public boolean handle(String[] args) {
				String message = "";
				for (int i = 1; i < args.length; i++)
					message += args[i] + " ";
				int rights = 0;
				for(Object o : playerList.toArray()) {
					if(o instanceof Player) {
						Player p = (Player)o;
						if(p.getName().equals(args[0])) {
							rights = p.getRights();
							break;
						}
					}
				}
				displayPrivateChat(getUser().getSettings().getPlayer(), new Player(args[0], getUser().getSettings().getRanks().forLevel(rights)), message);
				return true;
			}
		});
		CommandHandler.addCommand(new Command("setdebug", 1) {
			public boolean handle(String[] args) {
				if(args[0].equals("on")) {
					Vars.setDebug(true);
					getUser().getSettings().debugOn = true;
					Vars.getLogger().info("Debug mode ON");
				}
				else if(args[0].equals("off")) {
					Vars.setDebug(false);
					getUser().getSettings().debugOn = false;
					Vars.getLogger().info("Debug mode OFF");
				}
				else {
					Vars.getLogger().info("Unknown state");
					return false;
				}
				return true;
			}
		});
		CommandHandler.addCommand(new Command("connect", 0) {

			@Override
			public boolean handle(String[] args) {
				if (args == null) {
					String[] con = getUser().getSettings().defaultConnect;
					if(con[0] == null || con[1] == null || con[2] == null) {
						Vars.getLogger().warning("No default connection saved");
						return false;
					}
					try {
						setConnection(ClientConnection.connect(con[0], Integer.parseInt(con[1]), con[2], GhostFrameImpl.this));
						return true;
					}
					catch (Exception e) {
						Vars.getLogger().warning("Unable to connect to " + con[0] + ":" + con[1] + " - " + e.toString());
						Vars.getLogger().fine(e.getMessage());
						return false;
					}
				}
				else if (args.length > 0 && args[0] != null)
					if (args.length > 1 && args[1] != null)
						if (args.length > 2 && args[2] != null) {
							int port = -1;
							try {
								port = Integer.parseInt(args[1]);
							}
							catch (Exception e) {
								Vars.getLogger().warning("Port must be numeric.");
								return false;
							}
							try {
								setConnection(ClientConnection.connect(args[0], port, args[2], GhostFrameImpl.this));
								Vars.getLogger().info("Successfully connected to " + args[0] + ":" + port);
								Vars.getLogger().info("Use /connect to quickly connect to this IP in the future.");
								getUser().getSettings().defaultConnect[0] = args[0];
								getUser().getSettings().defaultConnect[1] = Integer.toString(port);
								getUser().getSettings().defaultConnect[2] = args[2];
								return true;
							}
							catch (Exception e) {
								Vars.getLogger().warning("Unable to connect to " + args[0] + ":" + port);
								Vars.getLogger().fine(e.getMessage());
								return false;
							}
						}
						else
							Vars.getLogger().warning("Please specify a password.");
					else
						Vars.getLogger().warning("Please specify a port.");
				else
					Vars.getLogger().warning("Please specify an IP.");
				return false;
			}
		});
		loadBoundPackets();
	}

	/**
	 * Loads saved packets from the disk and binds them
	 */
	private void loadBoundPackets() {
		for (ReflectionPacketContainer packet : ReflectionPacketContainer.loadAllContainers())
			bindPacket(packet);
	}

	@Override
	protected void bindPacket(final ReflectionPacketContainer packet) {
		JRoundedButton button = new JRoundedButton(packet.packetName);
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				ReflectionPacketContainer.invoke(packet, getConnection());
			}
		});
		boundPacketPanel.add(button);
	}
	private int curRight = 8;
	@Override
	public void restartButActionPerformed(ActionEvent evt) {
		playerList.addElement(new MapPlayer("whired", getUser().getSettings().getRanks().forLevel(curRight--), 2000, 2000, map)); // TODO actual implementation
	}

	/**
	 * Displays a chat message to {@code chatOutput}
	 * @param rights the rights of he user
	 * @param name the name of the user
	 * @param message the message the user sent
	 */
	@Override
	public void displayPublicChat(Player sender, String message) {
		Icon i = getUser().getSettings().getRanks().forLevel(sender.getRights()).getIcon();
		try {
			Style iconOnly = chatOutput.getStyledDocument().getStyle("iconOnly");
			if (iconOnly == null)
				iconOnly = chatOutput.getStyledDocument().addStyle("iconOnly", null);
			StyleConstants.setIcon(iconOnly, i);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), " ", iconOnly);
			StyleConstants.setBold(chatOutput.getInputAttributes(), true);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), sender.getName(), chatOutput.getInputAttributes());
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
		}
		catch (Exception e) {
			Vars.getLogger().severe("Unable to display chat:");
			e.printStackTrace();
		}
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		if (new PrivateChatPacket().send(getConnection(), sender, recipient, message)) {
			Icon senderIcon = getUser().getSettings().getRanks().forLevel(sender.getRights()).getIcon();
			Icon recpIcon = getUser().getSettings().getRanks().forLevel(recipient.getRights()).getIcon();
			try {
				Style iconOnly = pmOutput.getStyledDocument().getStyle("iconOnly");
				if (iconOnly == null)
					iconOnly = pmOutput.getStyledDocument().addStyle("iconOnly", null);
				StyleConstants.setIcon(iconOnly, senderIcon);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " ", iconOnly);
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), sender.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " to ", null);
				if (recipient.getRights() > 0) {
					StyleConstants.setIcon(iconOnly, recpIcon);
					pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " ", iconOnly);
				}
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), recipient.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
			}
			catch (Exception e) {
				Vars.getLogger().severe("Unable to display chat:");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void menuActionPerformed(int i, ActionEvent e) {
		switch (i) {
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
				if (JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					System.exit(0);
				break;
			case 4:
				displayReflectionManager();
				break;
		}
	}
}
