package org.whired.rsmap.impl;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.HashMap;
import org.whired.ghost.net.model.player.Player;
import org.whired.rsmap.graphics.sprites.TextSprite;
import org.whired.rsmap.ui.RSMap;

/**
 *
 * @author Whired
 */
public class PlayerRSMap extends RSMap {

	private HashMap<String, Player> players = new HashMap<String, Player>();
	
	public void addPlayer(Player player) {
		players.put(player.getName(), player);
		repaint();
	}
	
	public void removePlayer(Player player) {
		players.remove(player.getName());
		repaint();
	}
	
	public Player getPlayer(String name) {
		return players.get(name);
	}
	
	public void playerMoved() {
		repaint();
	}
	
	private final Font arial = new Font("Arial", Font.PLAIN, 9);

	@Override
	public void renderMap(int[] pix, Dimension size, int x1, int y1, int x2, int y2) {
		super.renderMap(pix, size, x1, y1, x2, y2);
		for (Player p : players.values()) {
			Point loc = p.getLocation();
			int px = (loc.x - mapStartX);
			int py = ((mapStartY + mapHeight) - loc.y);
			py -= (currentZoomLevel < 8D ? 4 : 2);
			int j6;
			int l6;
			j6 = px;
			l6 = py;
			int adjustedX = ((getWidth()) * (j6 - x1)) / (x2 - x1);
			int adjustedY = ((getHeight()) * (l6 - y1)) / (y2 - y1);
			new TextSprite(p.getName(), arial, 0xC0CFEB, false, true, this).drawSprite(adjustedX, adjustedY, this);
		}
	}
}
