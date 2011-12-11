package org.whired.ghost.net.model.player.event;

import org.whired.ghost.net.model.player.Player;

/**
 * Listens for events pertaining to the player list
 * 
 * @author Whired
 */
public interface PlayerListEventListener {
	/**
	 * Invoked when a player is added to the list
	 * @param player the player that was added
	 */
	public void playerAdded(Player player);

	/**
	 * Invoked when a player is removed from the list
	 * @param player the player that was removed
	 */
	public void playerRemoved(Player player);
}
