package org.whired.ghostclient.client.event;

import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.event.PlayerListEventListener;

/**
 * @author Whired
 */
public interface GhostEventListener extends PlayerListEventListener {
	/**
	 * Invoked when a private message is logged
	 * @param from the sending player
	 * @param to the receiving player
	 * @param message the message that was logged
	 */
	public void privateMessageLogged(Player from, Player to, String message);

	/**
	 * Invoked when a public message is logged
	 * @param from the sending player
	 * @param message the message that was logged
	 */
	public void publicMessageLogged(Player from, String message);

	/**
	 * Invoked when a packet is received
	 * @param packet the packet that was received
	 */
	public void packetReceived(GhostPacket packet);
}
