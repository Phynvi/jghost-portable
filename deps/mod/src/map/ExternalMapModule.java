import java.awt.Component;

import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketListener;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PlayerMovementPacket;
import org.whired.ghost.player.Player;
import org.whired.ghostclient.client.LocalGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;
import org.whired.rsmap.impl.PlayerRSMap;

/**
 * A module that displays players in the world on a graphical map
 * @author Whired
 */
public class ExternalMapModule extends PlayerRSMap implements Module {
	private String resourcePath;

	private final GhostEventAdapter adapter = new GhostEventAdapter() {

		@Override
		public void playerAdded(final Player player) {
			addPlayer(player);
		}

		@Override
		public void playerRemoved(final Player player) {
			removePlayer(player);
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
	public void setFrame(final LocalGhostFrame frame) {
		frame.getPacketHandler().addPacketListener(PacketType.PLAYER_MOVEMENT, new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				final PlayerMovementPacket pmp = (PlayerMovementPacket) packet;
				final Player p = getPlayer(pmp.playerName);
				if (p != null) {
					p.setLocation(pmp.newAbsX, pmp.newAbsY);
					playerMoved(p);
				}
			}
		});
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
	public void setResourcePath(final String path) {
		resourcePath = path;
	}
}