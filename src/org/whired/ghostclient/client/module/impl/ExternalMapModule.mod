package org.whired.ghostclient.client.module.impl;

import java.awt.Component;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.Vars;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PlayerMovementPacket;
import org.whired.ghostclient.client.ClientGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;
import org.whired.rsmap.impl.PlayerRSMap;

/**
 * A module that displays players in the world on a graphical map
 * @author Whired
 */
public class ExternalMapModule extends PlayerRSMap implements Module {
	private ClientGhostFrame frame;

	private final GhostEventAdapter adapter = new GhostEventAdapter(){
		
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
			if(packet.getId() == PacketType.PLAYER_MOVEMENT) {
				PlayerMovementPacket pmp = (PlayerMovementPacket) packet;
				Player p = getPlayer(pmp.playerName);
				if(p != null) {
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
	public void setFrame(ClientGhostFrame frame) {
		this.frame = frame;
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return adapter;
	}

	@Override
	public void load(String resourceDir) {
		Vars.getLogger().log(Level.INFO, "Loading map from {0}", resourceDir+System.getProperty("file.separator")+"worldmap.dat");
		//loadMap(resourceDir+System.getProperty("file.separator")+"worldmap.dat");
		loadMap();
		final ArrayList<Player> lplayers = new ArrayList<Player>();
		for(int i = 0; i < 5; i++)
			lplayers.add(addPlayer(new Player("Player-"+i, 3, 2460, 3090)));
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					for(Player player : lplayers) {
					int randX = (int)(Math.random()*4);
					int randY = (int)(Math.random()*4);
					int neg = (int)(Math.random() * 2);
					if(neg == 0) {
						randX *= -1;
						randY *= -1;
					}
					player.setLocation(player.getLocation().x+randX, player.getLocation().y+randY);
					playerMoved(player);
					
					}
					repaint();
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException ex) {
						Logger.getLogger(ExternalMapModule.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}).start();
	}
}