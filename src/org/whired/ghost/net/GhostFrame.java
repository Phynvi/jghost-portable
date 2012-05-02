package org.whired.ghost.net;

import org.whired.ghost.player.PlayerList;

/**
 * Provides the functionality and layout for a standard implementation of a JFrame that will utilize Ghost's core functionality
 * @author Whired
 */
public abstract class GhostFrame implements AbstractClient {

	/**
	 * The SessionManager for this frame
	 */
	private final SessionManager sessionManager;

	/**
	 * Gets the list of players for this frame
	 * @return the list
	 */
	public abstract PlayerList getPlayerList();

	/**
	 * Creates a new ghost frame with the specified session manager
	 * @param sessionManager the session manager to use
	 */
	public GhostFrame(final SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	/**
	 * Creates a new ghost frame with a default session manager
	 */
	public GhostFrame() {
		this(new SessionManager());
	}

	/**
	 * Gets the session manager for this frame
	 * @return the session manager
	 */
	public SessionManager getSessionManager() {
		return this.sessionManager;
	}
}
