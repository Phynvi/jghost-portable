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
	@Override
	public abstract void playerAdded(Player player);
	
	/**
	 * Invoked when a player is removed from the list
	 * @param player the player that was removed
	 */
	@Override
	public abstract void playerRemoved(Player player);

	public abstract Player[] getPlayers();
	
	/**
	 * Gets the frame that contains this list
	 * @return the frame
	 */
	public GhostFrame getFrame() {
		return frame;
	}
}
