package org.whired.ghostserver.net.model;

import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.PlayerList;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.reflection.Accessor;

/**
 *
 * @author Whired
 */
public class RemotePlayerList extends PlayerList {

	public RemotePlayerList(GhostFrame frame) {
		super(frame);
	}
	
	@Override
	public void addPlayer(Player player) {
		getFrame().getConnection().sendPacket(PacketType.INVOKE_ACCESSOR, Accessor.getClass("org.whired.ghostclient.Main").getField("instance").getMethod("getFrame").getMethod("getPlayerList").getMethod("addPlayer", player));
	}
}
