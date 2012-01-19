package org.whired.ghostserver.server.model;

import java.util.logging.Level;

import org.whired.ghost.constants.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.PlayerList;
import org.whired.ghost.net.packet.DebugPacket;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;

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
		if (!new PublicChatPacket(sender, message).send(getSessionManager().getConnection())) {
			Vars.getLogger().warning("Chat not sent");
		}
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		if (!new PrivateChatPacket(sender, recipient, message).send(getSessionManager().getConnection())) {
			Vars.getLogger().warning("Chat not sent");
		}
	}

	@Override
	public void displayDebug(Level level, String message) {
		if (!new DebugPacket(level.intValue(), message).send(getSessionManager().getConnection())) {
			Vars.getLogger().warning(message);
		}
	}

	@Override
	public PlayerList getPlayerList() {
		return this.playerList;
	}

	@Override
	public void sessionOpened() {
		Vars.getLogger().info("Session opened");
	}

	@Override
	public void sessionClosed(String reason) {
		Vars.getLogger().info("Session closed: " + reason);
	}
}
