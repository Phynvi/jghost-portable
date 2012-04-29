package org.whired.rsmap.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;

import org.whired.ghost.math.GhostMath;
import org.whired.ghost.player.Player;
import org.whired.rsmap.ui.MapButton;
import org.whired.rsmap.ui.RSMap;

/**
 * @author Whired
 */
public class PlayerRSMap extends RSMap {

	private final HashMap<String, MapPlayer> players = new HashMap<String, MapPlayer>();
	private boolean showLocations = false;
	private boolean showNames = false;
	private MapPlayer selectedPlayer = null;

	public MapPlayer addPlayer(final Player player) {
		final MapPlayer wrapped = MapPlayer.fromPlayer(player, 200);
		players.put(player.getName(), wrapped);
		repaint();
		return wrapped;
	}

	public void removePlayer(final Player player) {
		players.remove(player.getName());
		repaint();
	}

	public Player getPlayer(final String name) {
		return players.get(name);
	}

	public void playerMoved(final Player player) {
		players.get(player.getName()).addLocation(player.getLocation());
	}

	private void addButtons() {
		final MapButton mb = new MapButton("Players", 44, PlayerRSMap.super.getHeight() - 14 - 2, 40, 14, 0xBEC7E8, 0x6382BF) {

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
				repaint();
			}
		};
		mb.setTextSprite(defaultTextSprite);
		addButton(mb);
	}

	@Override
	public void renderMap(final int[] pix, final Dimension size, final int x1, final int y1, final int x2, final int y2) {
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
			final Point c = mapToPixel(selectedPlayer.getLocation());
			renderLine(loc.x, loc.y, c.x, c.y, 0xff0000);
		}
		if (showLocations || showNames) {
			for (final MapPlayer p : players.values()) {
				Point loc = mapToPixel(p.getLocation());
				if (showNames) {
					defaultTextSprite.setText(p.getName());
					defaultTextSprite.drawSprite(loc.x, loc.y, this);
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
					final Point c = mapToPixel(p.getLocation());
					renderLine(loc.x, loc.y, c.x, c.y, 0xff0000);
				}
			}
		}
		final Point p = mapToPixel(new Point(2460, 3000));
		renderPoint(p.x, p.y, 0xff00ff);
	}

	public MapPlayer findPlayerNearest(final Point mapCoord) {
		MapPlayer nearest = null;
		for (final MapPlayer pl : players.values()) {
			if (nearest != null) {
				if (GhostMath.getDistance(pl.getLocation(), mapCoord) < GhostMath.getDistance(nearest.getLocation(), mapCoord)) {
					nearest = pl;
				}
			}
			else {
				nearest = pl;
			}
		}
		return nearest;
	}

	@Override
	public void mouseUp(final int x, final int y) {
		selectedPlayer = findPlayerNearest(componentToMap(new Point(x, y)));
	}

	@Override
	public void loadMap(final String cacheDir) {
		super.loadMap(cacheDir);
		addButtons();
	}
}
