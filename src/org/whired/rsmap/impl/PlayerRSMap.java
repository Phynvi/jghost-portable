package org.whired.rsmap.impl;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.HashMap;
import org.whired.ghost.net.model.player.Player;
import org.whired.rsmap.graphics.sprites.TextSprite;
import org.whired.rsmap.ui.MapButton;
import org.whired.rsmap.ui.RSMap;

/**
 *
 * @author Whired
 */
public class PlayerRSMap extends RSMap {

	private HashMap<String, MapPlayer> players = new HashMap<String, MapPlayer>();
	private boolean showLocations = false;
	private boolean showNames = false;
	private final Font arial = new Font("Arial", Font.PLAIN, 9);
	private TextSprite ts;
	private boolean isSelecting = false;
	private MapPlayer selectedPlayer = null;

	public MapPlayer addPlayer(Player player) {
		MapPlayer wrapped = MapPlayer.fromPlayer(player, 200);
		players.put(player.getName(), wrapped);
		repaint();
		return wrapped;
	}

	public void removePlayer(Player player) {
		players.remove(player.getName());
		repaint();
	}

	public Player getPlayer(String name) {
		return players.get(name);
	}

	public void playerMoved(Player player) {
		((MapPlayer) players.get(player.getName())).addLocation(player.getLocation());
	}

	private void addButtons() {
		MapButton mb1 = new MapButton("Select", 47, PlayerRSMap.this.getHeight() - 18 - 25, 40, 14, 0xBEC7E8, 0x6382BF) {

			@Override
			public void draw() {
				drawButton(this);
			}

			@Override
			public void clicked() {
				isSelecting = !isSelecting;
				if (isSelecting) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else {
					selectedPlayer = null;
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		};
		MapButton mb = new MapButton("Players", 5, PlayerRSMap.this.getHeight() - 18 - 25, 40, 14, 0xBEC7E8, 0x6382BF) {

			int mode = 0;

			@Override
			public void draw() {
				drawButton(this);
			}

			@Override
			public void clicked() {
				mode++;
				if (mode == 0) {
					showNames = false;
					showLocations = false;
				}
				else if (mode == 1) {
					showNames = true;
					showLocations = false;
				}
				else if (mode == 2) {
					showNames = false;
					showLocations = true;

				}
				else {
					showNames = true;
					showLocations = true;
					mode = -1;
				}
			}
		};
		mb.setTextSprite(ts);
		mb1.setTextSprite(ts);
		addButton(mb);
		addButton(mb1);
	}

	@Override
	public void renderMap(int[] pix, Dimension size, int x1, int y1, int x2, int y2) {
		super.renderMap(pix, size, x1, y1, x2, y2);
		if (selectedPlayer != null) {
			Point loc = null;
			for (Point lh : selectedPlayer.getLocationHistory()) {
				lh = mapToPixel(lh);
				if (loc != null) {
					renderLine(loc.x, loc.y, lh.x, lh.y, 0xffffff);
				}
				loc = lh;
			}
			Point c = mapToPixel(selectedPlayer.getLocation());
			renderLine(loc.x, loc.y, c.x, c.y, 0xff0000);
		}
		if (showLocations || showNames) {
			for (MapPlayer p : players.values()) {
				Point loc = mapToPixel(p.getLocation());
				if (showNames) {
					ts.setText(p.getName());
					ts.drawSprite(loc.x, loc.y, this);
				}
				loc = null;
				if (showLocations) {
					for (Point lh : p.getLocationHistory()) {
						lh = mapToPixel(lh);
						if (loc != null) {
							renderLine(loc.x, loc.y, lh.x, lh.y, 0xffffff);
						}
						loc = lh;
					}
					Point c = mapToPixel(p.getLocation());
					renderLine(loc.x, loc.y, c.x, c.y, 0xff0000);
				}
			}
		}
		Point p = mapToPixel(new Point(2460, 3000));
		renderPoint(p.x, p.y, 0xff00ff);
	}

	public MapPlayer findPlayerNearest(Point mapCoord) {
		MapPlayer nearest = null;
		for (MapPlayer pl : players.values()) {
			if (nearest != null) {
				int dist1 = (int) getDistance(pl.getLocation(), mapCoord);
				int dist2 = (int) getDistance(nearest.getLocation(), mapCoord);
				if (dist1 < dist2) {
					nearest = pl;
				}
			}
			else {
				nearest = pl;
			}
		}
		return nearest;
	}

	public boolean clicked(Point p) {
		boolean b = super.clicked(p);
		if (isSelecting && !b) {
			selectedPlayer = findPlayerNearest(componentToMap(p));
		}
		return b;
	}

	public void loadMap() {
		super.loadMap();
		ts = new TextSprite("ts", arial, 0xC0CFEB, false, true, PlayerRSMap.this);
		addButtons();
	}
}
