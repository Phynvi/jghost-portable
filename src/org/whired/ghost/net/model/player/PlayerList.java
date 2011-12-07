package org.whired.ghost.net.model.player;

import org.whired.ghost.net.model.GhostFrame;

/**
 * A list of players
 * @author Whired
 */
public abstract class PlayerList {
	
	private final GhostFrame frame;
	
	protected PlayerList(GhostFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * Adds a player to this list
	 * @param player the player to add
	 */
	public abstract void addPlayer(Player player);
	
	/**
	 * Gets the frame that contains this list
	 * @return the frame
	 */
	public GhostFrame getFrame() {
		return frame;
	}
}
