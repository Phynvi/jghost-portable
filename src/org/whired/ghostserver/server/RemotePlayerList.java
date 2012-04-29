package org.whired.ghostserver.server;

import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.packet.PlayerConnectionPacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.PlayerList;

/**
 * A player list that provides access to the remote frame
 * @author Whired
 */
public class RemotePlayerList extends PlayerList {

	public RemotePlayerList(final GhostFrame frame) {
		super(frame);
	}

	@Override
	public void playerAdded(final Player player) {
		new PlayerConnectionPacket(player, PlayerConnectionPacket.CONNECTING).send(getFrame().getSessionManager().getConnection());
	}

	@Override
	public void playerRemoved(final Player player) {
		new PlayerConnectionPacket(player, PlayerConnectionPacket.DISCONNECTING).send(getFrame().getSessionManager().getConnection());
	}

	@Override
	public Player[] getPlayers() {
		throw new UnsupportedOperationException("Remote not supported yet.");// TODO
	}
}
