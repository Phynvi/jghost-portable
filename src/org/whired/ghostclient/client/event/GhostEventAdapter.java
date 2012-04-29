package org.whired.ghostclient.client.event;

import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.player.Player;

/**
 * Contains empty methods for receiving ghost events
 * @author Whired
 */
public abstract class GhostEventAdapter implements GhostEventListener {

	@Override
	public void playerAdded(final Player player) {
	}

	@Override
	public void playerRemoved(final Player player) {
	}

	@Override
	public void privateMessageLogged(final Player from, final Player to, final String message) {
	}

	@Override
	public void publicMessageLogged(final Player from, final String message) {
	}

	@Override
	public void packetReceived(final GhostPacket packet) {
	}

	public void playerSelected(final Player player) {
	}
}
