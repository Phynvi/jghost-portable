package org.whired.rsmap.impl;

import java.awt.Point;
import java.util.LinkedList;

import org.whired.ghost.math.GhostMath;
import org.whired.ghost.player.GhostPlayer;

/**
 * @author Whired
 */
public class MapPlayer extends GhostPlayer {

	private int threshold = 20;
	private final LinkedList<Point> locations = new LinkedList<Point>();
	private int trackingPrecision = 20;

	/**
	 * Sets the tracking precision for this player. Any distance less than the specified number will be ignored
	 * @param precision the precision of tracking
	 */
	public void setTrackingPrecision(final int precision) {
		this.trackingPrecision = precision;
	}

	/**
	 * Sets the threshold for tracking this player. Any move quantity greater than the specified number will result in the first queued move being removed.
	 * @param threshold the tracking threshold
	 */
	public void setThreshold(final int threshold) {
		this.threshold = threshold;
	}

	public final void addLocation(final Point location) {
		final Point last = locations.size() > 0 ? locations.getLast() : null;
		if (last == null || GhostMath.getDistance(last, location) >= trackingPrecision) {
			locations.addLast(location);
			if (locations.size() >= threshold) {
				locations.removeFirst();
			}
		}
	}

	public Point[] getLocationHistory() {
		return locations.toArray(new Point[locations.size()]);
	}

	public Point getLastLocation() {
		return locations.getLast();
	}

	private MapPlayer(final String name, final int rights, final int x, final int y, final int maxMoves) {
		super(name, rights, x, y);
		this.threshold = maxMoves;
		addLocation(getLocation());
	}

	public static MapPlayer fromPlayer(final GhostPlayer from, final int maxMoves) {
		return new MapPlayer(from.getName(), from.getRights(), from.getLocation().x, from.getLocation().y, maxMoves);
	}

	public static MapPlayer fromPlayer(final GhostPlayer from) {
		return new MapPlayer(from.getName(), from.getRights(), from.getLocation().x, from.getLocation().y, 20);
	}
}
