package org.whired.ghostserver.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.PlayerList;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;

/**
 * Provides access to the client from the server
 * @author Whired
 */
public class RemoteGhostFrame extends GhostFrame {

	private final PlayerList playerList = new RemotePlayerList(this);
	
	public RemoteGhostFrame(Connection connection) {
		getSessionManager().setConnection(connection);
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

	@Override
	public PlayerList getPlayerList() {
		return this.playerList;
	}

	@Override
	public void sessionOpened() {
		Logger.getLogger(RemoteGhostFrame.class.getName()).info("Session opened");
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void sessionClosed(String reason) {
		Logger.getLogger(RemoteGhostFrame.class.getName()).info("Session closed");
		//throw new UnsupportedOperationException("Not supported yet.");
	}
}
