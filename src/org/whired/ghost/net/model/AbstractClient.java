package org.whired.ghost.net.model;

import java.util.logging.Level;
import org.whired.ghost.net.event.SessionEventListener;
import org.whired.ghost.net.model.player.Player;

/**
 * 
 * @author Whired
 */
public interface AbstractClient extends SessionEventListener {
	/**
	 * Called when a normal chat message is received
	 * 
	 * @param sender the player who sent the message
	 * @param message the message that was received
	 */
	public void displayPublicChat(Player sender, String message);

	/**
	 * Called when a private chat message is received
	 * 
	 * @param sender the player who sent the message
	 * @param recipient the player who received the message
	 * @param message the message that was transferred
	 */
	public void displayPrivateChat(Player sender, Player recipient, String message);

	public void displayDebug(Level level, String message);
}
