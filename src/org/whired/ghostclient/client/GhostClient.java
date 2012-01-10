package org.whired.ghostclient.client;

import org.whired.ghost.net.model.AbstractClient;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.Rank;
import org.whired.ghostclient.client.settings.SessionSettings;

/**
 * A collection of methods that a typical ghost client would utilize
 * 
 * @author Whired
 */
public interface GhostClient extends AbstractClient {
	public void handleCommand(String command);

	public Rank getRankForPlayer(Player player);

	public Player getUserPlayer();

	public void saveSettings();

	public void restartServer();

	public GhostClientView getView();

	public void setView(GhostClientView view);

	public SessionSettings getSettings();

	/**
	 * Gets the abstract player list for this client
	 * 
	 * @return the list
	 */
	public ClientPlayerList getPlayerList();
}
