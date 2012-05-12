package org.whired.ghost.net.packet;

import org.whired.ghost.net.RS2Message;
import org.whired.ghost.player.GhostPlayer;

/**
 * A typical chat packet
 * @author Whired
 */
public abstract class GhostChatPacket extends GhostPacket {

	/**
	 * The sender
	 */
	public GhostPlayer sender;
	/**
	 * The message
	 */
	public String message;

	public GhostChatPacket(final int id) {
		super(id);
	}

	/**
	 * Gets the message as a byte array
	 */
	public byte[] getPackedMessage(final int charSize) {
		return RS2Message.getPackedMessage(message, charSize);
	}

	/**
	 * Unpacks and formats chat that was sent on the specified connection
	 * @return the unpacked and formatted chat
	 */
	protected static String getUnpackedMessage(final byte[] chatText, final int chatLength, final int charSize) {
		return RS2Message.unpackMessage(chatText, chatLength, charSize);
	}
}
