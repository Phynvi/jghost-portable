package org.whired.ghostclient.client.user.impl;

/**
 * The default implementation of a user
 * @author Whired
 */
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghostclient.client.user.GhostUser;
import org.whired.ghostclient.client.user.SessionSettings;

public class DefaultUser implements GhostUser {
	
	/**
	 * Provides access to the graphical client
	 */
	private final GhostFrame frame;
	/**
	 * The settings for this user
	 */
	private final SessionSettings settings;
	
	/**
	 * Constructs a new GhostUser
	 * 
	 * @param frame the frame that provides access to the graphical client
	 */
	public DefaultUser(GhostFrame frame, SessionSettings settings) {
		this.frame = frame;
		this.frame.setUser(this);
		this.settings = settings;
	}

	public GhostFrame getFrame() {
		return this.frame;
	}

	/**
	 * Gets the settings for this user
	 *
	 * @return the settings associated with this user
	 */
	@Override
	public SessionSettings getSettings() {
		return this.settings;
	}
}
