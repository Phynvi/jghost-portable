package org.whired.ghost.net.model.player;

import java.util.HashSet;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.model.player.event.PlayerListEventListener;

/**
 * A list of players
 * @author Whired
 */
public abstract class PlayerList implements PlayerListEventListener {
	
	private final GhostFrame frame;
	private final HashSet<PlayerListEventListener> listeners = new HashSet<PlayerListEventListener>();
	
	protected PlayerList(GhostFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * Adds a player to this list
	 * @param player the player to add
	 */
	public void addPlayer(Player player) {
		for(PlayerListEventListener l : listeners)
			l.playerAdded(player);
		playerAdded(player);
	}
	
	public void removePlayer(Player player) {
		for(PlayerListEventListener l : listeners)
			l.playerRemoved(player);
		playerRemoved(player);
	}
	
	// TODO add selection
	
	/**
	 * Invoked when a player is added to this list
	 * @param player the player that was added
	 */
	public abstract void playerAdded(Player player);
	
	/**
	 * Invoked when a player is removed from the list
	 * @param player the player that was removed
	 */
	public abstract void playerRemoved(Player player);
	
	/**
	 * Gets the currently selected player
	 * @return the selected player, or {@code null} if no player is selected
	 */
	public abstract Player getSelectedPlayer();
	/**
	 * Gets the frame that contains this list
	 * @return the frame
	 */
	public GhostFrame getFrame() {
		return frame;
	}
}
