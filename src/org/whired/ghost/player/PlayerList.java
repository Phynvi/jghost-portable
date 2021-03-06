package org.whired.ghost.player;

import java.util.HashSet;

import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.player.event.PlayerListEventListener;

/**
 * A list of players
 * @author Whired
 */
public abstract class PlayerList implements PlayerListEventListener {

	private final GhostFrame frame;
	protected final HashSet<PlayerListEventListener> listeners = new HashSet<PlayerListEventListener>();

	protected PlayerList(final GhostFrame frame) {
		this.frame = frame;
	}

	/**
	 * Adds a player to this list
	 * @param player the player to add
	 */
	public void addPlayer(final GhostPlayer player) {
		for (final PlayerListEventListener l : listeners) {
			l.playerAdded(player);
		}
		playerAdded(player);
	}

	public void removePlayer(final GhostPlayer player) {
		for (final PlayerListEventListener l : listeners) {
			l.playerRemoved(player);
		}
		playerRemoved(player);
	}

	/**
	 * Invoked when a player is added to this list
	 * @param player the player that was added
	 */
	@Override
	public abstract void playerAdded(GhostPlayer player);

	/**
	 * Invoked when a player is removed from the list
	 * @param player the player that was removed
	 */
	@Override
	public abstract void playerRemoved(GhostPlayer player);

	public abstract GhostPlayer[] getPlayers();

	/**
	 * Gets the frame that contains this list
	 * @return the frame
	 */
	public GhostFrame getFrame() {
		return frame;
	}
}
