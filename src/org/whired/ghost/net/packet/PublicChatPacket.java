package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;
import org.whired.ghost.net.model.player.Player;

/**
 * @author Whired
 */
public class PublicChatPacket extends GhostChatPacket {

	/**
	 * Creates a new public chat packet to be received
	 */
	public PublicChatPacket() {
		super(PacketType.PUBLIC_CHAT);
		this.setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedInputStream is = connection.getInputStream();
					PublicChatPacket.this.sender = new Player(is.readString(), is.readByte());
					PublicChatPacket.this.message = is.readString();
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	/**
	 * Creates a new public chat packet to be sent
	 * 
	 * @param sender the player that sent the message
	 * @param message the message that was sent
	 */
	public PublicChatPacket(Player sender, String message) {
		super(PacketType.PUBLIC_CHAT);
		this.sender = sender;
		this.message = message;
		this.setSendAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedOutputStream os = connection.getOutputStream();
					os.writeString(PublicChatPacket.this.sender.getName());
					os.writeByte(PublicChatPacket.this.sender.getRights());
					os.writeString(PublicChatPacket.this.message);
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	/**
	 * Creates a new public chat packet to be sent
	 * 
	 * @param sender the player that sent the message
	 * @param packedMessage the packed message that was sent
	 * @param packedSize the packed size of the message that was sent
	 */
	public PublicChatPacket(Player sender, byte[] packedMessage, int packedSize) {
		this(sender, getUnpackedMessage(packedMessage, packedSize));
	}
}
