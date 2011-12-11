package org.whired.ghost.net.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;
import java.util.ArrayList;
import org.whired.ghost.client.ui.GhostUser;
import org.whired.ghost.client.ui.ReflectionPacketBuilderManager;
import org.whired.ghost.net.PacketHandler;
import org.whired.ghost.net.SessionManager;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PlayerMovementPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghost.net.model.player.PlayerList;
import org.whired.rsmap.ui.RSMap; // TODO remove

/**
 * Provides the functionality and layout for a standard implementation of a JFrame that will utilize Ghost's core
 * functionality
 *
 * @author Whired
 */
public abstract class GhostFrame implements Receivable {

	/**
	 * The SessionManager for this frame
	 */
	private final SessionManager sessionManager;
	/**
	 * The user of this frame
	 */
	private GhostUser ghostUser;
	/**
	 * The dialog that builds reflection packets
	 */
	private ReflectionPacketBuilderManager reflectionPacketBuilderManager;
	/**
	 * The map for this frame
	 */
	protected RSMap map;
	
	public PacketHandler packetHandler = new PacketHandler();

	protected PlayerList playerList;
	
	/**
	 * Creates a new ghost frame with the specified session manager
	 * @param sessionManager the session manager to use
	 */
	public GhostFrame(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	/**
	 * Creates a new ghost frame with a default session manager
	 */
	public GhostFrame() {
		this.sessionManager = new SessionManager();
	}
	
	/**
	 * Sets the user of this frame
	 *
	 * @param user the user to set
	 */
	public void setUser(GhostUser user) {
		this.ghostUser = user;
	}

	/**
	 * Gets the user of this frame
	 *
	 * @return the user if one exists, otherwise null
	 */
	public GhostUser getUser() {
		return this.ghostUser;
	}

	/**
	 * Gets the session manager for this frame
	 * @return the session manager
	 */
	public SessionManager getSessionManager() {
		return this.sessionManager;
	}

	/**
	 * Called when a normal chat message is received
	 *
	 * @param sender the player who sent the message
	 * @param message the message that was received
	 */
	public abstract void displayPublicChat(Player sender, String message);

	/**
	 * Called when a private chat message is received
	 *
	 * @param sender the player who sent the message
	 * @param recipient the player who received the message
	 * @param message the message that was transferred
	 */
	public abstract void displayPrivateChat(Player sender, Player recipient, String message);

	public abstract void displayDebug(Level level, String message);
	
	/**
	 * Called when a packet is received; packets that should be handled
	 * internally are handled here
	 * @param packetId the id of the packet that was received
	 * @param connection the connection that received the packet
	 */
	@Override
	public boolean handlePacket(int packetId, int packetLength, Connection connection) {
		Vars.getLogger().fine(this + " received packet " + packetId);
		switch (packetId) {
			case PacketType.PUBLIC_CHAT: // Receive public chat
				PublicChatPacket pc = new PublicChatPacket();
				if (pc.receive(connection))
					displayPublicChat(pc.sender, pc.message);
				break;
			case 2: // Response to a request to list accessors
				try {
					// Response to a request to list accessors
					reflectionPacketBuilderManager.setList((ArrayList<Accessor>) connection.getInputStream().readObject());
				}
				catch (Exception ex) {
					Logger.getLogger(GhostFrame.class.getName()).log(Level.SEVERE, null, ex);
				}
				break;
			//TODO reimplement when ready
			case PacketType.PLAYER_MOVEMENT:
				try {
					PlayerMovementPacket pm = new PlayerMovementPacket();
					if (pm.receive(connection))
						map.uberSprite.location = new java.awt.Point(pm.newAbsX, pm.newAbsY); //						MapPlayer mp = map.getPlayer(pm.playerName);
					//						if(mp == null)
					//						{
					//							mp = new MapPlayer(pm.playerName, 3, pm.newAbsX, pm.newAbsY, map);
					//							DrawingArea.putPlayer(mp);
					//						}
					//mp.moveTo(pm.newAbsX, pm.newAbsY);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				// Notify whatever higher listener that they are to handle this packet
				//Vars.getLogger().fine("Pushing noninternal packet " + packetId + " to external listener " + this.getUser());
				GhostPacket packet = packetHandler.get(packetId);
				if (packet != null) {
					packet.receive(connection);
					packet.notifyReceived();
				}
				else
					return false;
			//return this.getUser().handlePacket(packetId, packetLength, connection);
		}
		return true;
	}

	/**
	 * Displays the window that allows the user to select a chain of accessors that will result in
	 * the formation of a reflection packet
	 */
	protected void displayReflectionManager() {
		throw new UnsupportedOperationException("Momentarily disabled.");
//		if (this.getConnection() != null) {
//			reflectionPacketBuilderManager = new ReflectionPacketBuilderManager((JFrame) this.getOwner(), this.getConnection());
//			reflectionPacketBuilderManager.setPacketLoader(new PacketLoader() {
//				
//				@Override
//				public void loadPacket(final ReflectionPacketContainer container) {
//					bindPacket(container);
//				}
//			});
//			reflectionPacketBuilderManager.performReflection();
//		}
//		else
//			Vars.getLogger().warning("Cannot perform remote reflection - no connection to server.");
	}
	
	/**
	 * Invoked when a reflection packet has been built
	 *
	 * @param container holds information about the packet
	 */
	protected abstract void bindPacket(final ReflectionPacketContainer container);
	
	public PlayerList getPlayerList() {
		return this.playerList;
	}
}
