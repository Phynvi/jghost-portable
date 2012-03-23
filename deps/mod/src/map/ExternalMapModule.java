import java.awt.Component;

import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PlayerMovementPacket;
import org.whired.ghost.player.Player;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;
import org.whired.rsmap.impl.PlayerRSMap;

/**
 * A module that displays players in the world on a graphical map
 * 
 * @author Whired
 */
public class ExternalMapModule extends PlayerRSMap implements Module {
	private String resourcePath;

	private final GhostEventAdapter adapter = new GhostEventAdapter() {

		@Override
		public void playerAdded(Player player) {
			addPlayer(player);
		}

		@Override
		public void playerRemoved(Player player) {
			removePlayer(player);
		}

		@Override
		public void packetReceived(GhostPacket packet) {
			if (packet.getId() == PacketType.PLAYER_MOVEMENT) {
				PlayerMovementPacket pmp = (PlayerMovementPacket) packet;
				Player p = getPlayer(pmp.playerName);
				if (p != null) {
					p.setLocation(pmp.newAbsX, pmp.newAbsY);
					playerMoved(p);
				}
			}
		}
	};

	@Override
	public String getModuleName() {
		return "Map";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void setFrame(GhostClientFrame frame) {
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return adapter;
	}

	@Override
	public void load() {
		loadMap(resourcePath + System.getProperty("file.separator") + "worldmap.dat");
	}

	@Override
	public void setResourcePath(String path) {
		resourcePath = path;
	}
}