package org.whired.ghost.net.packet;

import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;

/**
 * Represents a private chat packet
 * 
 * @author Whired
 */
public class PrivateChatPacket extends GhostChatPacket {

	/**
	 * The recipient
	 */
	public Player recipient;

	/**
	 * Creates a new private chat packet on the specified connection
	 * 
	 * @param connection the connection that will transfer the packet
	 */
	public PrivateChatPacket() {
		super(PacketType.PRIVATE_CHAT);
	}

	/**
	 * Receives this packet
	 * 
	 * @return {@code true} if the packet was successfully received, otherwise
	 * {@code false}
	 */
	public boolean receive(Connection connection) {
		try {
			this.sender = (Player) connection.getInputStream().readObject();
			this.recipient = (Player) connection.getInputStream().readObject();
			this.message = connection.getInputStream().readString();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean send(Connection connection, Player sender, Player recipient, String message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
		return sendUnchecked(connection, sender, recipient, message);
	}

	public boolean send(Connection connection, Player sender, Player recipient, byte[] chatText, int chatTextSize) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = unpackMessage(chatText, chatTextSize);
		return sendUnchecked(connection, sender, recipient, message);
	}
}
