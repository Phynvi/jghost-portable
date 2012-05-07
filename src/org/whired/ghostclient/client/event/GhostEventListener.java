package org.whired.ghostclient.client.event;

import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.player.event.PlayerListEventListener;

/**
 * @author Whired
 */
public interface GhostEventListener extends PlayerListEventListener {
	/**
	 * Invoked when a private message is logged
	 * @param from the sending player
	 * @param to the receiving player
	 * @param message the message that was logged
	 */
	public void privateMessageLogged(GhostPlayer from, GhostPlayer to, String message);

	/**
	 * Invoked when a public message is logged
	 * @param from the sending player
	 * @param message the message that was logged
	 */
	public void publicMessageLogged(GhostPlayer from, String message);
}
