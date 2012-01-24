package org.whired.ghostserver.server;

import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.packet.PlayerConnectionPacket;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.PlayerList;

/**
 * A player list that provides access to the remote frame
 * 
 * @author Whired
 */
public class RemotePlayerList extends PlayerList {

	public RemotePlayerList(GhostFrame frame) {
		super(frame);
	}

	@Override
	public void playerAdded(Player player) {
		new PlayerConnectionPacket(player, PlayerConnectionPacket.CONNECTING).send(getFrame().getSessionManager().getConnection());
	}

	@Override
	public void playerRemoved(Player player) {
		new PlayerConnectionPacket(player, PlayerConnectionPacket.DISCONNECTING).send(getFrame().getSessionManager().getConnection());
	}

	@Override
	public Player[] getPlayers() {
		throw new UnsupportedOperationException("Remote not supported yet.");// TODO
	}
}
