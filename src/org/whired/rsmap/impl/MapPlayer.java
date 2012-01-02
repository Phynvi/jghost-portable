package org.whired.rsmap.impl;

import java.awt.Point;
import java.util.LinkedList;
import org.whired.ghost.net.model.player.Player;

/**
 *
 * @author Whired
 */
public class MapPlayer extends Player {
	private int numMoves = 10;
	private final int maxMoves;
	private LinkedList<Point> locations = new LinkedList<Point>();

	public final void addLocation(Point location) {
		if (numMoves >= 10) {
			numMoves = 0;
			locations.addLast(location);
			if (locations.size() >= maxMoves) {
				locations.removeFirst();
			}
		}
		else {
			numMoves++;
		}
	}

	public Point[] getLocationHistory() {
		return locations.toArray(new Point[locations.size()]);
	}
	
	public Point getLastLocation() {
		return locations.getLast();
	}
	
	private MapPlayer(String name, int rights, int x, int y, int maxMoves) {
		super(name, rights, x, y);
		this.maxMoves = maxMoves;
		addLocation(getLocation());
	}

	public static MapPlayer fromPlayer(Player from, int maxMoves) {
		return new MapPlayer(from.getName(), from.getRights(), from.getLocation().x, from.getLocation().y, maxMoves);
	}
}
