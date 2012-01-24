package org.whired.ghost.net;

import java.io.IOException;
import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.DebugPacket;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PlayerConnectionPacket;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghost.player.PlayerList;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * Provides the functionality and layout for a standard implementation of a JFrame that will utilize Ghost's core functionality
 * 
 * @author Whired
 */
public abstract class GhostFrame implements Receivable, AbstractClient {

	/**
	 * The SessionManager for this frame
	 */
	private final SessionManager sessionManager;
	/**
	 * The user of this frame
	 */
	private GhostUser ghostUser;
	private PacketHandler packetHandler = new PacketHandler();

	/**
	 * Gets the list of players for this frame
	 * 
	 * @return the list
	 */
	public abstract PlayerList getPlayerList();

	/**
	 * Creates a new ghost frame with the specified session manager
	 * 
	 * @param sessionManager the session manager to use
	 */
	public GhostFrame(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	/**
	 * Creates a new ghost frame with a default session manager
	 */
	public GhostFrame() {
		this(new SessionManager());
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
	 * 
	 * @return the session manager
	 */
	public SessionManager getSessionManager() {
		return this.sessionManager;
	}

	/**
	 * Called when a packet is received; packets that should be handled internally are handled here
	 * 
	 * @param packetId the id of the packet that was received
	 * @param connection the connection that received the packet
	 * @throws IOException
	 */
	@Override
	public boolean handlePacket(int packetId, Connection connection) throws IOException {
		Constants.getLogger().fine(this + " received packet " + packetId);
		switch (packetId) {
		case PacketType.PUBLIC_CHAT:
			PublicChatPacket pc = new PublicChatPacket();
			if (pc.receive(connection)) {
				displayPublicChat(pc.sender, pc.message);
				packetReceived(pc);
			}
		break;
		case PacketType.PLAYER_CONNECTION:
			PlayerConnectionPacket pcp = new PlayerConnectionPacket();
			if(pcp.receive(connection)) {
				if(pcp.connectionType == PlayerConnectionPacket.CONNECTING)
					this.getPlayerList().addPlayer(pcp.player);
				else if(pcp.connectionType == PlayerConnectionPacket.DISCONNECTING)
					this.getPlayerList().removePlayer(pcp.player);
			}
			break;
		case PacketType.PRIVATE_CHAT:
			PrivateChatPacket prc = new PrivateChatPacket();
			if (prc.receive(connection)) {
				displayPrivateChat(prc.sender, prc.recipient, prc.message);
				packetReceived(prc);
			}
		break;
		case PacketType.AUTHENTICATE_SUCCESS:
			sessionManager.sessionOpened();
			Constants.getLogger().info("Sucessfully connected");
		break;
		case PacketType.DEBUG_MESSAGE:
			DebugPacket dpacket = new DebugPacket();
			if (dpacket.receive(connection)) {
				Constants.getLogger().log(Level.parse(Integer.toString(dpacket.level)), "[REMOTE] " + dpacket.message);
				packetReceived(dpacket);
			}
		break;
		default: // Notify external
			Constants.getLogger().fine("Pushing noninternal packet " + packetId + " to packet handler");
			GhostPacket packet = getPacketHandler().get(packetId);
			if (packet != null)
				if (packet.receive(connection))
					packetReceived(packet);
				else
					return false;
		}
		return true;
	}

	/**
	 * Invoked when a packet has been received used for notifying modules of incoming data
	 * 
	 * @param packet the packet that was received
	 */
	public void packetReceived(GhostPacket packet) {
	}

	/**
	 * @return the packetHandler
	 */
	public PacketHandler getPacketHandler() {
		return packetHandler;
	}

	/**
	 * @param packetHandler the packetHandler to set
	 */
	public void setPacketHandler(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
}
