package org.whired.ghostclient;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.player.Rank;
import org.whired.ghostclient.client.command.Command;
import org.whired.ghostclient.client.impl.DefaultController;
import org.whired.ghostclient.client.settings.SettingsFactory;
import org.whired.ghostclient.client.user.impl.DefaultUser;
import org.whired.ghostclient.io.ClientConnection;

public class Main {

	public static DefaultController client;

	public static void main(final String args[]) {
		client = new DefaultController(new DefaultUser(SettingsFactory.loadFromDatabase(Constants.getLocalCodebase())));
		client.getModel().getCommandManager().registerCommands(new Command[] { new Command("setrights", 1) {

			@Override
			public boolean handle(final String[] args) {
				Rank rank;
				try {
					rank = client.getModel().getRankManager().rankForLevel(Integer.parseInt(args[0]));
				}
				catch (final NumberFormatException e) {
					rank = client.getModel().getRankManager().rankForName(args[0]);
				}
				if (rank != null) {
					client.getModel().getUser().getSettings().getPlayer().setRights(rank.getLevel());
					Constants.getLogger().log(Level.INFO, "New rank: {0}", rank.getTitle());
					return true;
				}
				else {
					return false;
				}
			}
		}, new Command("disconnect", 0) {

			@Override
			public boolean handle(final String[] args) {
				final Connection c = client.getModel().getSessionManager().getConnection();
				if (c != null) {
					client.getModel().getSessionManager().removeConnection("User requested");
					return true;
				}
				else {
					Constants.getLogger().info("No connection to server currently exists");
					return false;
				}
			}
		}, new Command("setname", 1) {

			@Override
			public boolean handle(final String[] args) {
				final StringBuilder finalName = new StringBuilder();
				for (final String s : args) {
					finalName.append(s);
					finalName.append(" ");
				}
				final String name = finalName.toString().trim();
				client.getModel().getUser().getSettings().getPlayer().setName(name);
				Constants.getLogger().info("Your name is now " + name);
				return true;
			}
		}, new Command("pm", 2) {

			@Override
			public boolean handle(final String[] args) {
				String message = "";
				for (int i = 1; i < args.length; i++) {
					message += args[i] + " ";
				}
				int rights = 0;
				for (final GhostPlayer p : client.getModel().getPlayerList().getPlayers()) {
					if (p.getName().equals(args[0])) {
						rights = p.getRights();
						break;
					}
				}
				GhostPlayer sender = client.getModel().getUser().getSettings().getPlayer();
				GhostPlayer recipient = new GhostPlayer(args[0], rights);
				client.getModel().displayPrivateChat(sender, recipient, message);
				client.getModel().getSessionManager().getRemoteFrame().displayPrivateChat(sender, recipient, message);
				return true;
			}
		}, new Command("setdebug", 1) {

			@Override
			public boolean handle(final String[] args) {
				if (args[0].equals("on")) {
					Constants.setDebug(true);
					client.getModel().getUser().getSettings().debugOn = true;
					Constants.getLogger().info("Debug mode ON");
				}
				else if (args[0].equals("off")) {
					Constants.setDebug(false);
					client.getModel().getUser().getSettings().debugOn = false;
					Constants.getLogger().info("Debug mode OFF");
				}
				else {
					Constants.getLogger().info("Argument state invalid. Try on/off.");
					return false;
				}
				return true;
			}
		}, new Command("whoami") {
			@Override
			public boolean handle(final String[] args) {
				final GhostPlayer p = client.getModel().getUser().getSettings().getPlayer();
				Constants.getLogger().info("You are " + client.getModel().getRankManager().rankForLevel(p.getRights()).getTitle() + " " + p.getName());
				return true;
			}
		}, new Command("savesession") {
			@Override
			public boolean handle(final String[] args) {
				SettingsFactory.saveToDatabase(Constants.getLocalCodebase(), client.getModel().getUser().getSettings());
				return true;
			}
		}, new Command("addplayer", 2) {
			@Override
			public boolean handle(String[] args) {
				client.getModel().getPlayerList().addPlayer(new GhostPlayer(args[0], Integer.parseInt(args[1])));
				return true;
			}
		}, new Command("connect", 0) {

			@Override
			public boolean handle(final String[] args) {
				if (args == null) {
					final String[] con = client.getModel().getUser().getSettings().defaultConnect;
					if (con[0] == null || con[1] == null || con[2] == null) {
						Constants.getLogger().warning("No default connection saved");
						return false;
					}
					try {
						ClientConnection.connect(con[0], Integer.parseInt(con[1]), con[2], client.getModel().getSessionManager(), client.getModel().getPacketHandler());
						return true;
					}
					catch (final Exception e) {
						Constants.getLogger().warning("Unable to connect to " + con[0] + ":" + con[1] + " - " + e.toString());
						Constants.getLogger().fine(e.getMessage());
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
							catch (final Exception e) {
								Constants.getLogger().warning("Port must be numeric.");
								return false;
							}
							try {
								client.getModel().getSessionManager().setConnection(ClientConnection.connect(args[0], port, args[2], client.getModel().getSessionManager(), client.getModel().getPacketHandler()));
								Constants.getLogger().info("Successfully connected to " + args[0] + ":" + port);
								Constants.getLogger().info("Use /connect to quickly connect to this IP in the future.");
								client.getModel().getUser().getSettings().defaultConnect[0] = args[0];
								client.getModel().getUser().getSettings().defaultConnect[1] = Integer.toString(port);
								client.getModel().getUser().getSettings().defaultConnect[2] = args[2];
								return true;
							}
							catch (final Exception e) {
								Constants.getLogger().warning("Unable to connect to " + args[0] + ":" + port);
								Constants.getLogger().fine(e.getMessage());
								return false;
							}
						}
						else {
							Constants.getLogger().warning("Please specify a password.");
						}
					}
					else {
						Constants.getLogger().warning("Please specify a port.");
					}
				}
				else {
					Constants.getLogger().warning("Please specify an IP.");
				}
				return false;
			}
		} });
	}
}
