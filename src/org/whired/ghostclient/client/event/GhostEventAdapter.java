package org.whired.ghostclient.client.event;

import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;

/**
 * Contains empty methods for receiving ghost events
 * @author Whired
 */
public abstract class GhostEventAdapter implements GhostEventListener {

	@Override
	public void playerAdded(Player player) {
	}

	@Override
	public void playerRemoved(Player player) {
	}

	@Override
	public void privateMessageLogged(Player from, Player to, String message) {
	}

	@Override
	public void publicMessageLogged(Player from, String message) {
	}

	@Override
	public void packetReceived(GhostPacket packet) {
	}
}
