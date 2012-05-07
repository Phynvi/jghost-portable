package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.player.GhostPlayer;

public class PlayerListUpdatePacket extends GhostPacket {

	public GhostPlayer[] onlinePlayers;

	public PlayerListUpdatePacket() {
		super(PacketType.UPDATE_PLAYER_LIST);
		this.setReceiveAction(new TransmitAction() {
			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					onlinePlayers = connection.getInputStream().readPlayers();
					return true;
				}
				catch (final IOException e) {
				}
				return false;
			}
		});
	}

	public PlayerListUpdatePacket(final GhostPlayer[] onlinePlayers) {
		super(PacketType.UPDATE_PLAYER_LIST);
		this.setSendAction(new TransmitAction() {
			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					connection.getOutputStream().writePlayers(onlinePlayers);
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}
}
