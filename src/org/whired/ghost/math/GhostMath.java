package org.whired.ghost.math;

import java.awt.Point;

/**
 *
 * @author Whired
 */
public class GhostMath {
	/**
	 * Gets the distance between two points
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the distance
	 */
	public static double getDistance(Point p1, Point p2) {
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
}
