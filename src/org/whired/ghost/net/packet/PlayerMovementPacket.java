package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;

/**
 * An movement packet
 * @author Whired
 */
public class PlayerMovementPacket extends GhostPacket {

	public String playerName;
	public short newAbsX;
	public short newAbsY;

	public PlayerMovementPacket() {
		super(PacketType.PLAYER_MOVEMENT);
		this.setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					final WrappedInputStream is = connection.getInputStream();
					PlayerMovementPacket.this.playerName = is.readString();
					PlayerMovementPacket.this.newAbsX = is.readShort();
					PlayerMovementPacket.this.newAbsY = is.readShort();
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}

	public PlayerMovementPacket(final String playerName, final short newAbsX, final short newAbsY) {
		super(PacketType.PLAYER_MOVEMENT);
		this.playerName = playerName;
		this.newAbsX = newAbsX;
		this.newAbsY = newAbsY;
		this.setSendAction(new TransmitAction() {

			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					final WrappedOutputStream os = connection.getOutputStream();
					os.writeString(PlayerMovementPacket.this.playerName);
					os.writeShort(PlayerMovementPacket.this.newAbsX);
					os.writeShort(PlayerMovementPacket.this.newAbsY);
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}
}
