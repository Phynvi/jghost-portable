package org.whired.graph;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;

/**
 * Represents a line graph
 *
 * @author Whired
 */
public class LineGraph extends Graph {

	/**
	 * The lines on this graph
	 */
	private HashSet<Line> lines = new HashSet<Line>();
	private final MouseAdapter panListener = new MouseAdapter() {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() > 0) {
				shift(-1);
			}
			else {
				shift(1);
			}
			repaint();
		}
	};

	/**
	 * Creates a new line graph with the specified width and height
	 * @param width the width of the graph
	 * @param height the height of the graph
	 */
	public LineGraph(int width, int height) {
		super(width, height);
		this.addMouseWheelListener(panListener);
	}

	public LineGraph() {
		this.addMouseWheelListener(panListener);
	}
	
	/**
	 * Adds the specified line to this graph
	 *
	 * @param line the line to add
	 */
	public void addLine(Line line) {
		lines.add(line);
		line.setOwner(this);
	}
	private boolean isPanning = false;

	@Override
	public void plot(Graphics graphics) {

		double widthScale = getWidthScale();
		float unitsPerView = (float) (this.getWidth() / widthScale);
		System.out.println("autoscroll: " + (unitsPerView - getVisibleX()));
		if (isAutoScroll() && unitsPerView - getVisibleX() <= 0 && !isPanning) {
			xOffset++;
		}
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setStroke(new BasicStroke(1.2F));
		int maxY = getMaxVisibleY();
		g.setFont(this.getFont());

		double heightScale = getHeightScale();

		int minY = getMinVisibleY();



		// Draw gridlines
		if (showGridLineX() || showGridLineY()) {
			if (showGridLineX()) {
				g.setColor(getGridLineXColor());
				double curX = 0;

				while (curX < getWidth()) {
					g.drawLine((int) curX, getHeight() - 1, (int) curX, 0);
					curX += widthScale;
				}
			}

			if (showGridLineY()) {
				g.setColor(getGridLineYColor());
				double curY = getHeight();

				while (curY > 0) {
					g.drawLine(0, (int) curY, getWidth(), (int) curY);
					int approx = (maxY - minY) / 10;
					curY -= heightScale * (approx < 1 ? 1 : approx);
				}
			}
		}

		for (Line l : lines) {
			g.setColor(l.getColor());
			Point lastPoint = null;

			for (int i = xOffset; i < l.getPoints().length; i++) {
				// Stop plotting if these points are out of range
				if (i - xOffset > unitsPerView) {
					System.out.println("Points exceed view. Abort.");
					break;
				}

				Point realPt = new Point(i - xOffset, l.getPoints()[i].y);
				Point scaledPt = new Point(realPt.x, realPt.y - minY);

				// Draw points
				if (showPointXLabel() || showPointYLabel()) {
					g.drawString((showPointXLabel() && showPointYLabel() ? "(" + realPt.x + ", " + realPt.y + ")"
						   : showPointXLabel() ? Integer.toString(realPt.x)
						   : showPointYLabel() ? Integer.toString(realPt.y)
						   : ""), (int) (scaledPt.x * widthScale + 4), getHeight() - (int) (scaledPt.y * heightScale) - 4);
				}
				Point pt = new Point((int) (scaledPt.x * widthScale), getHeight() - (int) (scaledPt.y * heightScale));

				if (lastPoint != null) {
					g.drawLine(lastPoint.x, lastPoint.y - 2, pt.x, pt.y - 2);
				}
				lastPoint = pt;
			}
		}
	}
	private int xOffset = 0;

	/**
	 * Gets the height scale of this graph
	 * @return 
	 */
	private double getHeightScale() {
		int canY = getHeight() - (int) (getHeight() * .1);
		int maxY = getMaxVisibleY();
		int minY = getMinVisibleY();
		return maxY - minY > 0 ? ((double) canY / (double) (maxY - minY)) : 1;
	}

	/**
	 * Gets the minimum y-value on this graph
	 * @return 
	 */
	public int getMinY() {
		int minY = Integer.MAX_VALUE;
		for (Line l : lines) {
			if (l.getMinY() < minY) {
				minY = l.getMinY();
			}
		}
		return minY;
	}

	/**
	 * Gets the minimum visible y-value on this graph
	 * @return 
	 */
	public int getMinVisibleY() {
		int minY = Integer.MAX_VALUE;
		int curMax = xOffset + (int) (getWidth() / getWidthScale());
		for (Line l : lines) {
			for (int i = xOffset; i < l.getPointCount(); i++) {
				if (i > curMax) {
					break;
				}
				int y = l.getPoints()[i].y;
				if (y < minY) {
					minY = y;
				}
			}
		}
		return minY;
	}

	/**
	 * Gets the highest y-value on this graph
	 * @return 
	 */
	public int getMaxY() {
		int maxY = 0;
		for (Line l : lines) {
			if (l.getMaxY() > maxY) {
				maxY = l.getMaxY();
			}
		}
		return maxY;
	}

	/**
	 * Gets the highest x-value on this graph
	 * @return 
	 */
	public int getMaxX() {
		int maxX = 0;
		for (Line l : lines) {
			int x = l.getPointCount();
			if (x > maxX) {
				maxX = x;
			}
		}
		return maxX;
	}

	public int getVisibleX() {
		return getMaxX() - xOffset;
	}

	/**
	 * Gets the highest visible y-value on this graph
	 * @return 
	 */
	public int getMaxVisibleY() {
		int maxY = Integer.MIN_VALUE;
		int curMax = xOffset + (int) (getWidth() / getWidthScale());
		for (Line l : lines) {
			for (int i = xOffset; i < l.getPointCount(); i++) {
				if (i > curMax) {
					break;
				}
				int y = l.getPoints()[i].y;
				if (y > maxY) {
					maxY = y;
				}
			}
		}
		return maxY;
	}

	/**
	 * Gets the width scale for this graph
	 * @return 
	 */
	private double getWidthScale() {
		return getWidth() * .05D;
	}

	public Point scaleUp(Point p) {
		return new Point((int) (p.x * getWidthScale()), getHeight() - (int) (p.y * getHeightScale()));
	}

	/**
	 * Shifts this graph's view by the specified amount of pixels, or the maximum amount
	 * possible if {@code amount} exceeds it.
	 * @param amt the amount, in points, to shift this graph's view
	 */
	public void shift(int amt) {
		float unitsPerView = (float) (this.getWidth() / getWidthScale());

		if (amt > 0) {
			if (getVisibleX() > unitsPerView + 1) {
				xOffset += amt;
				isPanning = true;
			}
			else {
				isPanning = false;
			}
		}
		else {
			xOffset = xOffset + amt < 0 ? 0 : xOffset + amt;
			isPanning = true;
		}


		/*
		if(xOffset + ptAmount < 0) {
		xOffset = 0;
		isPanning = true;
		}*/
		System.out.println("new offset: " + xOffset + " upv/gvx: " + unitsPerView + "/" + getVisibleX());
		repaint();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}
}
