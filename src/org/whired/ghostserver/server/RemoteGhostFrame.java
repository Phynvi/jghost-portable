package org.whired.ghostserver.server;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.packet.DebugPacket;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.PlayerList;

/**
 * Provides access to the client from the server
 * 
 * @author Whired
 */
public class RemoteGhostFrame extends GhostFrame {

	private final PlayerList playerList = new RemotePlayerList(this);

	public RemoteGhostFrame(Connection connection) {
		getSessionManager().setConnection(connection);
	}

	@Override
	public void displayPublicChat(Player sender, String message) {
		if (!new PublicChatPacket(sender, message).send(getSessionManager().getConnection()))
			Constants.getLogger().warning("Chat not sent");
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		if (!new PrivateChatPacket(sender, recipient, message).send(getSessionManager().getConnection()))
			Constants.getLogger().warning("Chat not sent");
	}

	@Override
	public void displayDebug(Level level, String message) {
		if (!new DebugPacket(level.intValue(), message).send(getSessionManager().getConnection()))
			Constants.getLogger().warning(message);
	}

	@Override
	public PlayerList getPlayerList() {
		return this.playerList;
	}

	@Override
	public void sessionOpened() {
		Constants.getLogger().info("Session opened");
	}

	@Override
	public void sessionClosed() {
		Constants.getLogger().info("Session closed");
	}
}
