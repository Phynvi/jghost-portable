package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;

/**
 * 
 * @author Whired
 */
public class PublicChatPacket extends GhostChatPacket {

	/**
	 * Creates a new public chat packet on the connection specified
	 * 
	 * @param connection the connection to transfer the packet
	 */
	public PublicChatPacket() {
		super(PacketType.PUBLIC_CHAT);
	}

	@Override
	public boolean receive(Connection connection) {
		try {
			this.sender = (Player) connection.getInputStream().readObject();
			this.message = connection.getInputStream().readString();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean send(Connection connection, Player sender, String message) {
		this.sender = sender;
		this.message = message;
		return sendUnchecked(connection, sender, message);
	}

	public boolean send(Connection connection, Player sender, byte[] chatText, int chatTextSize) {
		this.sender = sender;
		this.message = unpackMessage(chatText, chatTextSize);
		return sendUnchecked(connection, sender, message);
	}
}
