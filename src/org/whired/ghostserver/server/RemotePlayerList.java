package org.whired.ghostserver.server;

import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.PlayerList;

/**
 * A player list that provides access to the remote frame
 * 
 * @author Whired
 */
public class RemotePlayerList extends PlayerList {

	private final Accessor playerList = Accessor.getClass("org.whired.ghostclient.Main").getField("client").getMethod("getModel").getMethod("getPlayerList");

	public RemotePlayerList(GhostFrame frame) {
		super(frame);
	}

	@Override
	public void playerAdded(Player player) {
		invoke(playerList.getMethod("addPlayer", player));
	}

	@Override
	public void playerRemoved(Player player) {
		invoke(playerList.getMethod("removePlayer", player));
	}

	/**
	 * Invokes an accessor
	 * 
	 * @param accessor the accessor to invoke
	 */
	private void invoke(Accessor accessor) {
		// getFrame().getSessionManager().getConnection().sendPacket(PacketType.INVOKE_ACCESSOR,
		// accessor);//TODO fix
	}

	@Override
	public Player[] getPlayers() {
		throw new UnsupportedOperationException("Remote not supported yet.");// TODO
	}
}
