package org.whired.ghost.net;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.DebugPacket;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.player.PlayerList;
import org.whired.ghost.player.RemotePlayerList;

/**
 * Provides access to the client from the server
 * @author Whired
 */
public class RemoteGhostFrame extends GhostFrame {

	private final PlayerList playerList = new RemotePlayerList(this);

	public RemoteGhostFrame(final SessionManager manager) {
		super(manager);
	}

	@Override
	public void displayPublicChat(final GhostPlayer sender, final String message) {
		if (!new PublicChatPacket(sender, message).send(getSessionManager().getConnection())) {
			Constants.getLogger().warning("Chat not sent");
		}
	}

	@Override
	public void displayPrivateChat(final GhostPlayer sender, final GhostPlayer recipient, final String message) {
		if (!new PrivateChatPacket(sender, recipient, message).send(getSessionManager().getConnection())) {
			Constants.getLogger().warning("Chat not sent");
		}
	}

	@Override
	public void displayDebug(final Level level, final String message) {
		if (!new DebugPacket(level.intValue(), message).send(getSessionManager().getConnection())) {
			Constants.getLogger().warning(message);
		}
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
