package org.whired.ghostclient.client;

import org.whired.ghost.net.AbstractClient;
import org.whired.ghost.net.SessionManager;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.RankManager;
import org.whired.ghostclient.client.settings.SessionSettings;

/**
 * A typical ghost client
 * @author Whired
 */
public interface GhostClient extends AbstractClient {
	/**
	 * Parses and handles a command
	 * @param command the command to handle
	 */
	void handleCommand(String command);

	/**
	 * Gets the rank manager for this session
	 * @return the rank manager
	 */
	RankManager getRankManager();

	/**
	 * Gets the session manager that is managing this session
	 * @return the session manager
	 */
	SessionManager getSessionManager();

	/**
	 * Gets the player for the current user
	 * @return the player
	 */
	Player getUserPlayer();

	/**
	 * Saves the settings for this session
	 */
	void saveSessionSettings();

	/**
	 * Restarts the server
	 */
	void restartServer();

	/**
	 * Gets the view for this client
	 * @return the view
	 */
	GhostClientView getView();

	/**
	 * Sets the view for this client
	 * @param view the view to set
	 */
	void setView(GhostClientView view);

	/**
	 * Gets the settings for the current session
	 * @return the settings
	 */
	SessionSettings getSessionSettings();

	/**
	 * Moderates the specified player
	 * @param playerName the name of the player to moderate
	 * @param operation the operation to perform
	 */
	void moderatePlayer(String playerName, int operation);

	/**
	 * Gets the abstract player list for this client
	 * @return the list
	 */
	ClientPlayerList getPlayerList();
}
