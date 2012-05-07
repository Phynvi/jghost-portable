package org.whired.ghost.player.event;

import org.whired.ghost.player.GhostPlayer;

/**
 * Listens for events pertaining to the player list
 * @author Whired
 */
public interface PlayerListEventListener {
	/**
	 * Invoked when a player is added to the list
	 * @param player the player that was added
	 */
	public void playerAdded(GhostPlayer player);

	/**
	 * Invoked when a player is removed from the list
	 * @param player the player that was removed
	 */
	public void playerRemoved(GhostPlayer player);
}
