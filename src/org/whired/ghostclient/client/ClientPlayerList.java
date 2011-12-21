package org.whired.ghostclient.client;

import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.model.player.PlayerList;

/**
 *
 * @author Whired
 */
public abstract class ClientPlayerList extends PlayerList {

	public ClientPlayerList(GhostFrame frame) {
		super(frame);
	}
	
	
}
