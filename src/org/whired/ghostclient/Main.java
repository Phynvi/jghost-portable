package org.whired.ghostclient;

import javax.swing.ImageIcon;

import org.whired.ghost.client.net.ClientConnection;
import org.whired.ghost.constants.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.DefaultRightsConstants;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.Rank;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghostclient.client.command.Command;
import org.whired.ghostclient.client.impl.DefaultController;
import org.whired.ghostclient.client.settings.SettingsFactory;
import org.whired.ghostclient.client.user.impl.DefaultUser;

public class Main {

	public static DefaultController client;

	public static void main(String args[]) {
		client = new DefaultController(new DefaultUser(SettingsFactory.loadFromDatabase(Vars.getLocalCodebase())));
		client.getModel().getCommandHandler().registerCommands(new Command[] { new Command("setrights", 1) {

			@Override
			public boolean handle(String[] args) {
				client.getModel().getUser().getSettings().getPlayer().setRights(Integer.parseInt(args[0]));
				return true;
			}
		}, new Command("disconnect", 0) {

			@Override
			public boolean handle(String[] args) {
				Connection c = client.getModel().getSessionManager().getConnection();
				if (c != null) {
					client.getModel().getSessionManager().removeConnection("User requested.");
					return true;
				}
				else {
					Vars.getLogger().info("No connection to server currently exists");
					return false;
				}
			}
		}, new Command("setname", 1) {

			@Override
			public boolean handle(String[] args) {
				StringBuilder finalName = new StringBuilder();
				for (String s : args) {
					finalName.append(s);
					finalName.append(" ");
				}
				String name = finalName.toString().trim();
				client.getModel().getUser().getSettings().getPlayer().setName(name);
				Vars.getLogger().info("Your name is now " + name);
				return true;
			}
		}, new Command("pm", 2) {

			@Override
			public boolean handle(String[] args) {
				String message = "";
				for (int i = 1; i < args.length; i++) {
					message += args[i] + " ";
				}
				int rights = 0;
				for (Player p : client.getModel().getPlayerList().getPlayers()) {
					if (p.getName().equals(args[0])) {
						rights = p.getRights();
						break;
					}
				}
				client.getModel().displayPrivateChat(client.getModel().getUser().getSettings().getPlayer(), new Player(args[0], rights, -1, -1), message);
				return true;
			}
		}, new Command("setdebug", 1) {

			@Override
			public boolean handle(String[] args) {
				if (args[0].equals("on")) {
					Vars.setDebug(true);
					client.getModel().getUser().getSettings().debugOn = true;
					Vars.getLogger().info("Debug mode ON");
				}
				else if (args[0].equals("off")) {
					Vars.setDebug(false);
					client.getModel().getUser().getSettings().debugOn = false;
					Vars.getLogger().info("Debug mode OFF");
				}
				else {
					Vars.getLogger().info("Argument state invalid. Try on/off.");
					return false;
				}
				return true;
			}
		}, new Command("whoami") {
			@Override
			public boolean handle(String[] args) {
				Player p = client.getModel().getUser().getSettings().getPlayer();
				Vars.getLogger().info("You are " + client.getModel().getRankHandler().rankForLevel(p.getRights()).getTitle() + " " + p.getName());
				return true;
			}
		}, new Command("savesession") {
			@Override
			public boolean handle(String[] args) {
				SettingsFactory.saveToDatabase(Vars.getLocalCodebase(), client.getModel().getUser().getSettings());
				return true;
			}
		}, new Command("connect", 0) {

			@Override
			public boolean handle(String[] args) {
				if (args == null) {
					String[] con = client.getModel().getUser().getSettings().defaultConnect;
					if (con[0] == null || con[1] == null || con[2] == null) {
						Vars.getLogger().warning("No default connection saved");
						return false;
					}
					try {
						client.getModel().getSessionManager().setConnection(ClientConnection.connect(con[0], Integer.parseInt(con[1]), con[2], client.getModel()));
						return true;
					}
					catch (Exception e) {
						Vars.getLogger().warning("Unable to connect to " + con[0] + ":" + con[1] + " - " + e.toString());
						Vars.getLogger().fine(e.getMessage());
						return false;
					}
				}
				else if (args.length > 0 && args[0] != null) {
					if (args.length > 1 && args[1] != null) {
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
								client.getModel().getSessionManager().setConnection(ClientConnection.connect(args[0], port, args[2], client.getModel()));
								Vars.getLogger().info("Successfully connected to " + args[0] + ":" + port);
								Vars.getLogger().info("Use /connect to quickly connect to this IP in the future.");
								client.getModel().getUser().getSettings().defaultConnect[0] = args[0];
								client.getModel().getUser().getSettings().defaultConnect[1] = Integer.toString(port);
								client.getModel().getUser().getSettings().defaultConnect[2] = args[2];
								return true;
							}
							catch (Exception e) {
								Vars.getLogger().warning("Unable to connect to " + args[0] + ":" + port);
								Vars.getLogger().fine(e.getMessage());
								return false;
							}
						}
						else {
							Vars.getLogger().warning("Please specify a password.");
						}
					}
					else {
						Vars.getLogger().warning("Please specify a port.");
					}
				}
				else {
					Vars.getLogger().warning("Please specify an IP.");
				}
				return false;
			}
		} });
		client.getModel()
				.getRankHandler()
				.registerRanks(
						new Rank[] { new Rank(DefaultRightsConstants.PLAYER, "Player", new ImageIcon(client.getClass().getResource("resources/player.png"))), new Rank(DefaultRightsConstants.VETERAN, "Veteran", new ImageIcon(client.getClass().getResource("resources/veteran.png"))), new Rank(DefaultRightsConstants.DONATOR, "Donator", new ImageIcon(client.getClass().getResource("resources/donator.png"))), new Rank(DefaultRightsConstants.DEVELOPER, "Developer", new ImageIcon(client.getClass().getResource("resources/developer.png"))), new Rank(DefaultRightsConstants.MODERATOR, "Moderator", new ImageIcon(client.getClass().getResource("resources/moderator.png"))),
								new Rank(DefaultRightsConstants.ADMINISTRATOR, "Administrator", new ImageIcon(client.getClass().getResource("resources/administrator.png"))), new Rank(DefaultRightsConstants.OWNER, "Owner", new ImageIcon(client.getClass().getResource("resources/owner.png"))) });
		client.getModel().getPacketHandler().registerPacket(new GhostPacket(PacketType.INVOKE_ACCESSOR) {

			@Override
			public boolean receive(Connection connection) {
				try {
					Accessor a = (Accessor) connection.getInputStream().readObject();
					System.out.println(a.invoke());
				}
				catch (Exception ex) {
					Vars.getLogger().warning("Unable to invoke accessor: " + ex);
					ex.printStackTrace();
				}
				return true;
			}
		});
	}
}
