package org.whired.ghostclient;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.whired.ghost.Vars;
import org.whired.ghost.client.net.ClientConnection;
import org.whired.ghost.client.util.Command;
import org.whired.ghost.client.util.SessionSettings;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.Rank;
import org.whired.ghost.net.packet.GhostChatPacket;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghostclient.ui.ClientGhostFrame;
import org.whired.ghostclient.ui.GhostFrameImpl;
import org.whired.ghostclient.ui.GhostUserImpl;
import org.whired.ghostclient.ui.Module;

public class Main {

	public static GhostUserImpl instance;

	public static void main(String args[]) {

		final GhostFrameImpl impl = new GhostFrameImpl();
		impl.commandHandler.registerCommands(new Command[]{
			   new Command("setrights", 1) {

				   @Override
				   public boolean handle(String[] args) {
					   impl.getUser().getSettings().getPlayer().setRights(Integer.parseInt(args[0]));
					   return true;
				   }
			   },
			   new Command("disconnect", 0) {

				   public boolean handle(String[] args) {
					   Connection c = impl.getSessionManager().getConnection();
					   if (c != null) {
						   impl.getSessionManager().removeConnection("User requested.");
						   return true;
					   }
					   else {
						   Vars.getLogger().info("No connection to server currently exists");
						   return false;
					   }
				   }
			   },
			   new Command("setname", 1) {

				   @Override
				   public boolean handle(String[] args) {
					   StringBuilder finalName = new StringBuilder();
					   for (String s : args) {
						   finalName.append(s);
						   finalName.append(" ");
					   }
					   impl.getUser().getSettings().getPlayer().setName(finalName.toString().trim());
					   return true;
				   }
			   },
			   new Command("pm", 2) {

				   @Override
				   public boolean handle(String[] args) {
					   String message = "";
					   for (int i = 1; i < args.length; i++) {
						   message += args[i] + " ";
					   }
					   int rights = 0;
					   for (Object o : impl.playerListModel.toArray()) {
						   if (o instanceof Player) {
							   Player p = (Player) o;
							   if (p.getName().equals(args[0])) {
								   rights = p.getRights();
								   break;
							   }
						   }
					   }
					   impl.displayPrivateChat(impl.getUser().getSettings().getPlayer(), new Player(args[0], rights), message);
					   return true;
				   }
			   },
			   new Command("setdebug", 1) {

				   public boolean handle(String[] args) {
					   if (args[0].equals("on")) {
						   Vars.setDebug(true);
						   impl.getUser().getSettings().debugOn = true;
						   Vars.getLogger().info("Debug mode ON");
					   }
					   else if (args[0].equals("off")) {
						   Vars.setDebug(false);
						   impl.getUser().getSettings().debugOn = false;
						   Vars.getLogger().info("Debug mode OFF");
					   }
					   else {
						   Vars.getLogger().info("Unknown state");
						   return false;
					   }
					   return true;
				   }
			   },
			   new Command("connect", 0) {

				   @Override
				   public boolean handle(String[] args) {
					   if (args == null) {
						   String[] con = impl.getUser().getSettings().defaultConnect;
						   if (con[0] == null || con[1] == null || con[2] == null) {
							   Vars.getLogger().warning("No default connection saved");
							   return false;
						   }
						   try {
							   impl.getSessionManager().setConnection(ClientConnection.connect(con[0], Integer.parseInt(con[1]), con[2], impl));
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
									   impl.getSessionManager().setConnection(ClientConnection.connect(args[0], port, args[2], impl));
									   Vars.getLogger().info("Successfully connected to " + args[0] + ":" + port);
									   Vars.getLogger().info("Use /connect to quickly connect to this IP in the future.");
									   impl.getUser().getSettings().defaultConnect[0] = args[0];
									   impl.getUser().getSettings().defaultConnect[1] = Integer.toString(port);
									   impl.getUser().getSettings().defaultConnect[2] = args[2];
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
			   }
		   });
		impl.rankHandler.registerRanks(new Rank[] { 
			new Rank(0, "Player", new ImageIcon(impl.getClass().getResource("resources/player.png"))),
			new Rank(1, "Veteran", new ImageIcon(impl.getClass().getResource("resources/veteran.png"))),
			new Rank(2, "Donator", new ImageIcon(impl.getClass().getResource("resources/donator.png"))),
			new Rank(3, "Developer", new ImageIcon(impl.getClass().getResource("resources/developer.png"))),
			new Rank(4, "Moderator", new ImageIcon(impl.getClass().getResource("resources/moderator.png"))),
			new Rank(5, "Administrator", new ImageIcon(impl.getClass().getResource("resources/administrator.png"))),
			new Rank(6, "Owner", new ImageIcon(impl.getClass().getResource("resources/owner.png")))
		});
		impl.packetHandler.registerPacket(new GhostPacket(PacketType.INVOKE_ACCESSOR) {

			@Override
			public boolean receive(Connection connection) {
				try {
					Accessor a = (Accessor) connection.getInputStream().readObject();
					System.out.println(a.invoke());
				}
				catch (Exception ex) {
					Vars.getLogger().warning("Unable to invoke accessor: "+ex);
					ex.printStackTrace();
				}
				return true;
			}
			
		});
		final Module testModule = new Module() {

			@Override
			public String getModuleName() {
				return "Longmodulename";
			}
			
			private final JLabel label = new JLabel("Hello, this is a test module!");
			private ClientGhostFrame frame = null;
			@Override
			public Component getComponent() {
				return label;
			}

			@Override
			public void moduleActivated() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void moduleDeactivated() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void setFrame(ClientGhostFrame frame) {
				this.frame = frame;
				System.out.println("Current list is: "+ frame.getPlayerList());
				frame.getPlayerList().addPlayer(new Player("Test", 2));
			}

			@Override
			public void packetReceived(GhostPacket packet) {
				System.out.println("Packet "+packet.getId()+" received.");
				if(packet instanceof GhostChatPacket) {
					GhostChatPacket p = (GhostChatPacket) packet;
					p.sender.setName("tits");
					frame.getUser().getSettings().getPlayer().setName(p.sender.getName());
				}
			}

			@Override
			public boolean listensFor(int id) {
				return true;
			}
		};
		impl.packetHandler.get(PacketType.INVOKE_ACCESSOR).addReceiveListener(testModule);
		impl.addModule(testModule);
		
		SessionSettings settings;
		try {
			settings = SessionSettings.loadFromDisk();
		}
		catch (Exception e) {
			e.printStackTrace();
			settings = new SessionSettings(new Player("Admin", 6));
		}
		instance = new GhostUserImpl(impl, settings);
	}
}
