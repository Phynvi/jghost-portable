package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;
import org.whired.ghost.player.Player;

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
	 * Creates a new private chat packet to be received
	 */
	public PrivateChatPacket() {
		super(PacketType.PRIVATE_CHAT);
		this.setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedInputStream is = connection.getInputStream();
					PrivateChatPacket.this.sender = new Player(is.readString(), is.readByte());
					PrivateChatPacket.this.recipient = new Player(is.readString(), is.readByte());
					PrivateChatPacket.this.message = is.readString();
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	public PrivateChatPacket(Player sender, Player recipient, String message) {
		super(PacketType.PRIVATE_CHAT);
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
		this.setSendAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedOutputStream os = connection.getOutputStream();
					os.writeString(PrivateChatPacket.this.sender.getName());
					os.writeByte(PrivateChatPacket.this.sender.getRights());
					os.writeString(PrivateChatPacket.this.recipient.getName());
					os.writeByte(PrivateChatPacket.this.recipient.getRights());
					os.writeString(PrivateChatPacket.this.message);
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	public PrivateChatPacket(Player sender, Player recipient, byte[] chatText, int chatTextSize) {
		this(sender, recipient, getUnpackedMessage(chatText, chatTextSize));
	}
}
