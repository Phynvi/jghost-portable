package org.whired.ghostclient.client.event;

import org.whired.ghost.player.GhostPlayer;

/**
 * Contains empty methods for receiving ghost events
 * @author Whired
 */
public abstract class GhostEventAdapter implements GhostEventListener {

	@Override
	public void playerAdded(final GhostPlayer player) {
	}

	@Override
	public void playerRemoved(final GhostPlayer player) {
	}

	@Override
	public void privateMessageLogged(final GhostPlayer from, final GhostPlayer to, final String message) {
	}

	@Override
	public void publicMessageLogged(final GhostPlayer from, final String message) {
	}

	public void playerSelected(final GhostPlayer player) {
	}
}
