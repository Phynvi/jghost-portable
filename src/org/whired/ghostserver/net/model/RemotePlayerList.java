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
	
	private final Accessor playerList = Accessor.getClass("org.whired.ghostclient.Main").getField("instance").getMethod("getFrame").getMethod("getPlayerList");
	
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
	 * @param accessor the accessor to invoke
	 */
	private void invoke(Accessor accessor) {
		getFrame().getSessionManager().getConnection().sendPacket(PacketType.INVOKE_ACCESSOR, accessor);
	}
}
