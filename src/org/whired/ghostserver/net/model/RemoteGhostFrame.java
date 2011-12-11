package org.whired.ghostserver.net.model;

import java.util.logging.Level;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;

/**
 * Provides access to the client from the server
 * @author Whired
 */
public class RemoteGhostFrame extends GhostFrame {

	public RemoteGhostFrame(Connection connection) {
		getSessionManager().setConnection(connection);
		super.playerList = new RemotePlayerList(this);
	}
	
	@Override
	public void displayPublicChat(Player sender, String message) {
		getSessionManager().getConnection().sendPacket(PacketType.INVOKE_ACCESSOR, Accessor.getClass("org.whired.ghostclient.Main").getField("instance").getMethod("getFrame").getMethod("displayPublicChat", sender, message));
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void bindPacket(ReflectionPacketContainer container) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void displayDebug(Level level, String message) {
		getSessionManager().getConnection().sendPacket(PacketType.INVOKE_ACCESSOR, Accessor.getClass("org.whired.ghostclient.Main").getField("instance").getMethod("getFrame").getMethod("displayDebug", level, message));
	}
}
