package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;
import org.whired.ghost.player.GhostPlayer;

public class PlayerConnectionPacket extends GhostPacket {
	public static final int CONNECTING = 1;
	public static final int DISCONNECTING = 0;
	public int connectionType;
	public GhostPlayer player;

	public PlayerConnectionPacket(final GhostPlayer player, final int connectionType) {
		super(PacketType.PLAYER_CONNECTION);
		this.connectionType = connectionType;
		this.player = player;
		this.setSendAction(new TransmitAction() {
			@Override
			public boolean onTransmit(final Connection connection) {
				final WrappedOutputStream os = connection.getOutputStream();
				try {
					os.writeString(PlayerConnectionPacket.this.player.getName());
					os.writeByte(PlayerConnectionPacket.this.player.getRights());
					os.writeByte(PlayerConnectionPacket.this.connectionType);
					return true;
				}
				catch (final Throwable t) {
					return false;
				}
			}
		});
	}

	public PlayerConnectionPacket() {
		super(PacketType.PLAYER_CONNECTION);
		this.setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					final WrappedInputStream is = connection.getInputStream();
					PlayerConnectionPacket.this.player = new GhostPlayer(is.readString(), is.readByte());
					PlayerConnectionPacket.this.connectionType = is.readByte();
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}
}
