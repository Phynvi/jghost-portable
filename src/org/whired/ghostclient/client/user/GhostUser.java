package org.whired.ghostclient.client.user;

/**
 * Represents the user (human) of ghost
 * @author Whired
 */
public interface GhostUser {

	/**
	 * Gets the settings for this user
	 *
	 * @return the settings associated with this user
	 */
	public SessionSettings getSettings();
}
