package org.whired.ghostclient.client;

import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.player.PlayerList;

/**
 * @author Whired
 */
public abstract class ClientPlayerList extends PlayerList {

	public ClientPlayerList(final GhostFrame frame) {
		super(frame);
	}

	/**
	 * Invoked when a player on this list is selected
	 * @param player the player that was selected
	 */
	public abstract void playerSelected(GhostPlayer player);

	/**
	 * Removes all players from this list
	 */
	public abstract void removeAll();
}
