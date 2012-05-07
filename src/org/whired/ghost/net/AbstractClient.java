package org.whired.ghost.net;

import java.util.logging.Level;

import org.whired.ghost.net.event.SessionEventListener;
import org.whired.ghost.player.GhostPlayer;

/**
 * @author Whired
 */
public interface AbstractClient extends SessionEventListener {
	/**
	 * Called when a normal chat message is received
	 * @param sender the player who sent the message
	 * @param message the message that was received
	 */
	public void displayPublicChat(GhostPlayer sender, String message);

	/**
	 * Called when a private chat message is received
	 * @param sender the player who sent the message
	 * @param recipient the player who received the message
	 * @param message the message that was transferred
	 */
	public void displayPrivateChat(GhostPlayer sender, GhostPlayer recipient, String message);

	public void displayDebug(Level level, String message);
}
