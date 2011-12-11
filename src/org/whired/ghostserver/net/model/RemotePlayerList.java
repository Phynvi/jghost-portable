package org.whired.ghostserver.net.model;

import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.PlayerList;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.reflection.Accessor;

/**
 * A player list that provides access to the remote frame
 * @author Whired
 */
public class RemotePlayerList extends PlayerList {

	// TODO impl
	
	public RemotePlayerList(GhostFrame frame) {
		super(frame);
	}

	@Override
	public void playerAdded(Player player) {
		getFrame().getSessionManager().getConnection().sendPacket(PacketType.INVOKE_ACCESSOR, Accessor.getClass("org.whired.ghostclient.Main").getField("instance").getMethod("getFrame").getMethod("getPlayerList").getMethod("addPlayer", player));
	}

	@Override
	public Player getSelectedPlayer() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void playerRemoved(Player player) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void playerSelectionChanged(Player oldPlayer, Player newPlayer) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
