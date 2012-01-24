package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;
import org.whired.ghost.player.Player;

public class PlayerConnectionPacket extends GhostPacket {
	public static final int CONNECTING = 1;
	public static final int DISCONNECTING = 0;
	public int connectionType;
	public Player player;
	
	public PlayerConnectionPacket(Player player, int connectionType) {
		super(PacketType.PLAYER_CONNECTION);
		this.connectionType = connectionType;
		this.player = player;
		this.setSendAction(new TransmitAction() {
			@Override
			public boolean onTransmit(Connection connection) {
				WrappedOutputStream os = connection.getOutputStream();
				try {
					os.writeString(PlayerConnectionPacket.this.player.getName());
					os.writeByte(PlayerConnectionPacket.this.player.getRights());
					os.writeByte(PlayerConnectionPacket.this.connectionType);
					return true;
				}
				catch(Throwable t) {
					return false;
				}
			}
		});
	}
	
	public PlayerConnectionPacket() {
		super(PacketType.PLAYER_CONNECTION);
		this.setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedInputStream is = connection.getInputStream();
					PlayerConnectionPacket.this.player = new Player(is.readString(), is.readByte());
					PlayerConnectionPacket.this.connectionType = is.readByte();
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}
}
