package org.whired.graph;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedHashSet;

/**
 * Represents a line to be added on a line graph
 *
 * @author Whired
 */
public class Line {

	/**
	 * The points on this line
	 */
	private LinkedHashSet<Point> points = new LinkedHashSet<Point>();
	/**
	 * The color of this line
	 */
	private Color color = Color.BLACK;
	/**
	 * The maximum X-coordinate of this line
	 */
	private int maxX = -1;
	/**
	 * The maximum Y-coordinate of this line
	 */
	private int maxY;
	/**
	 * The minimum Y-coordinate of this line
	 */
	private int minY = Integer.MAX_VALUE;

	/**
	 * Creates a new empty line with a default color of black
	 */
	public Line() {
		this(Color.BLACK);
	}

	/**
	 * Creates a new empty line with the specified color
	 *
	 * @param color the color of this line
	 */
	public Line(Color color) {
		this.color = color;
	}

	/**
	 * Creates a new line with the specified color and points
	 *
	 * @param color the color of this line
	 * @param points the points that make up this line
	 */
	public Line(Color color, Point[] points) {
		this.color = color;
		for (Point p : points) {
			addPoint(p);
		}
	}

	/**
	 * Creates a new line with the specified points
	 *
	 * @param points the points that will make up this line
	 */
	public Line(Point[] points) {
		for (Point p : points) {
			addPoint(p);
		}
	}

	/**
	 * Gets the count of points that make up this line
	 *
	 * @return the count
	 */
	public int getPointCount() {
		return points.size();
	}

	/**
	 * Gets the points that make up this line
	 * @return the points
	 */
	public synchronized Point[] getPoints() {
		return points.toArray(new Point[points.size()]);
	}

	/**
	 * Adds a point to this line TODO sort
	 *
	 * @param point the point to add
	 */
	public synchronized void addPoint(Point point) {
		points.add(point);
		if (point.x > maxX) {
			maxX = point.x;
		}
		if (point.y > maxY) {
			maxY = point.y;
		}
		if (point.y < minY) {
			minY = point.y;
		}
		requestRedraw();
	}

	/**
	 * Adds a point at the next maximum x-coordinate with the given y-coordinate
	 * @param y the y-coordinate of the new point
	 * @return the point that was plotted
	 */
	public Point addNextY(int y) {
		Point p = new Point(++maxX, y);
		addPoint(p);
		return p;
	}

	/**
	 * Removes a point from this line TODO sort
	 *
	 * @param point the point to remove
	 */
	public void removePoint(Point point) {
		points.remove(point);

		// If this was a max point, find the new max
		if (point.x == maxX || point.y == maxY) {
			maxX = -1;
			maxY = 0;
			for (Point p : points) {
				if (p.x > maxX) {
					maxX = p.x;
				}
				if (p.y > maxY) {
					maxY = p.y;
				}
			}
		}
		if (point.y == minY) {
			minY = Integer.MAX_VALUE;
			for (Point p : points) {
				if (p.y < minY) {
					minY = p.y;
				}
			}
		}
		requestRedraw();
	}

	/**
	 * Sets the color of this line
	 *
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		requestRedraw();
	}

	/**
	 * Gets the color of this line
	 *
	 * @return the color of this line
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Gets the maximum X-coordinate of this line
	 *
	 * @return the maximum X-coordinate
	 */
	public int getMaxX() {
		return this.maxX;
	}

	public int getMinY() {
		return this.minY;
	}

	/**
	 * Gets the maximum Y-coordinate of this line
	 *
	 * @return the maximum Y-coordinate
	 */
	public int getMaxY() {
		return this.maxY;
	}
	/**
	 * Used for redrawing when visual changes are made
	 */
	private Graph owner;

	/**
	 * Sets the owner of this line
	 * @param c the graph that owns this line
	 */
	protected void setOwner(Graph c) {
		this.owner = c;
	}

	/**
	 * Requests that the owner of this line, if any, is redrawn
	 */
	private void requestRedraw() {
		if (this.owner != null) {
			this.owner.repaint();
		}
	}
}
