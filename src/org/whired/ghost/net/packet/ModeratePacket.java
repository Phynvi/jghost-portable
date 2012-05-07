package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;

/**
 * Used for moderating players
 * @author Whired
 */
public class ModeratePacket extends GhostPacket {

	public static final int DEMOTE = 0, JAIL = 1, KICK = 2, BAN = 3, IP_BAN = 4, UN_BAN = 5, PROMOTE = 6;

	private String playerName;
	private int operation;

	/**
	 * Creates a new moderation packet for receiving
	 */
	public ModeratePacket() {
		super(PacketType.MODERATE_PLAYER);
		this.setReceiveAction(new TransmitAction() {
			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					final WrappedInputStream is = connection.getInputStream();
					playerName = is.readString();
					operation = is.readByte();
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}

	/**
	 * Creates a new moderation packet for the specified player
	 * @param playerName the player to moderate
	 * @param operation the operation to perform, such as {@link #KICK}
	 */
	public ModeratePacket(String playerName, int operation) {
		super(PacketType.MODERATE_PLAYER);
		this.playerName = playerName;
		this.operation = operation;
		this.setSendAction(new TransmitAction() {
			@Override
			public boolean onTransmit(Connection connection) {
				try {
					final WrappedOutputStream os = connection.getOutputStream();
					os.writeString(ModeratePacket.this.playerName);
					os.writeByte(ModeratePacket.this.operation);
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	/**
	 * Gets the player to moderate
	 * @return the player
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Gets the operation to perform
	 * @return the operation
	 */
	public int getOperation() {
		return operation;
	}
}
