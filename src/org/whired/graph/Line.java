package org.whired.graph;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedHashSet;

/**
 * Represents a line to be added on a line graph
 * @author Whired
 */
public class Line implements Scrollable {

	/**
	 * The points on this line
	 */
	private final LinkedHashSet<Point> points = new LinkedHashSet<Point>();
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
	 * The graph that owns this line
	 */
	private LineGraph owner;
	/**
	 * The label for this line
	 */
	private String label = null;
	/**
	 * Whether or not this line is visible
	 */
	private boolean visible = true;

	/**
	 * Creates a new empty line with a default color of black
	 */
	public Line() {
		this(Color.BLACK);
	}

	/**
	 * Creates a new empty line with the specified label and color
	 * @param label the label of this line
	 * @param color the color of this line
	 */
	public Line(final String label, final Color color) {
		this.label = label;
		this.color = color;
	}

	/**
	 * Creates a new empty line with the specified color
	 * @param color the color of this line
	 */
	public Line(final Color color) {
		this.color = color;
	}

	/**
	 * Creates a new line with the specified color and points
	 * @param color the color of this line
	 * @param points the points that make up this line
	 */
	public Line(final Color color, final Point[] points) {
		this.color = color;
		for (final Point p : points) {
			addPoint(p);
		}
	}

	/**
	 * Creates a new line with the specified points
	 * @param points the points that will make up this line
	 */
	public Line(final Point[] points) {
		for (final Point p : points) {
			addPoint(p);
		}
	}

	/**
	 * Gets the count of points that make up this line
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
	 * @param point the point to add
	 */
	public synchronized void addPoint(final Point point) {
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
		scroll();
		requestRedraw();
	}

	/**
	 * Adds a point at the next maximum x-coordinate with the given y-coordinate
	 * @param y the y-coordinate of the new point
	 * @return the point that was plotted
	 */
	public synchronized Point addNextY(final int y) {
		final Point p = new Point(++maxX, y); // TODO concur
		addPoint(p);
		return p;
	}

	/**
	 * Removes a point from this line TODO sort
	 * @param point the point to remove
	 */
	public void removePoint(final Point point) {
		points.remove(point);

		// If this was a max point, find the new max
		if (point.x == maxX || point.y == maxY) {
			maxX = -1;
			maxY = 0;
			for (final Point p : points) {
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
			for (final Point p : points) {
				if (p.y < minY) {
					minY = p.y;
				}
			}
		}
		scroll();
		requestRedraw();
	}

	/**
	 * Sets the color of this line
	 * @param color the color to set
	 */
	public void setColor(final Color color) {
		this.color = color;
		requestRedraw();
	}

	/**
	 * Gets the color of this line
	 * @return the color of this line
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Gets the maximum X-coordinate of this line
	 * @return the maximum X-coordinate
	 */
	public int getMaxX() {
		return this.maxX;
	}

	/**
	 * Gets the minimum Y-coordinate of this line
	 * @return the minimum Y-coordinate
	 */
	public int getMinY() {
		return this.minY;
	}

	/**
	 * Gets the maximum Y-coordinate of this line
	 * @return the maximum Y-coordinate
	 */
	public int getMaxY() {
		return this.maxY;
	}

	/**
	 * Sets the owner of this line
	 * @param c the graph that owns this line
	 */
	protected void setOwner(final LineGraph c) {
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

	@Override
	public void scroll() {
		if (this.owner != null) {
			this.owner.scroll();
		}
	}

	/**
	 * Sets this line's label
	 * @param label the label to set
	 */
	public void setLabel(final String label) {
		this.label = label;
		requestRedraw();
	}

	/**
	 * Gets this line's label
	 * @return the label, or {@code null} if no label has been set
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets this line's visibility
	 * @param visible the visibility to set
	 */
	public void setVisible(final boolean visible) {
		this.visible = visible;
		scroll();
		requestRedraw();
	}

	/**
	 * Gets this line's visibility
	 * @return {@code true} if this line is visible, otherwise {@code false}
	 */
	public boolean isVisible() {
		return visible;
	}
}
