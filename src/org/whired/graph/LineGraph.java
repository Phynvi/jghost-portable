package org.whired.graph;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.font.TextAttribute;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.RepaintManager;

/**
 * Represents a line graph
 * @author Whired
 */
public class LineGraph extends Graph implements Scrollable {

	/**
	 * The lines on this graph
	 */
	private final HashSet<Line> lines = new HashSet<Line>();
	private final MouseAdapter panListener = new MouseAdapter() {

		@Override
		public void mouseWheelMoved(final MouseWheelEvent e) {
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
	public LineGraph(final int width, final int height) {
		super(width, height);
		this.addMouseWheelListener(panListener);
	}

	public LineGraph() {
		this.addMouseWheelListener(panListener);
	}

	/**
	 * Adds the specified line to this graph
	 * @param line the line to add
	 */
	public void addLine(final Line line) {
		lines.add(line);
		line.setOwner(this);

		final Legend leg = getLegend();
		final String lbl = line.getLabel();
		final JLabel l = new JLabel(lbl == null ? "line" : lbl);
		l.setForeground(line.getColor());
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final boolean visible = line.isVisible();
				final Map attributes = l.getFont().getAttributes();
				if (visible) {
					line.setVisible(false);
					attributes.put(TextAttribute.STRIKETHROUGH, Boolean.TRUE);

				}
				else {
					line.setVisible(true);
					attributes.put(TextAttribute.STRIKETHROUGH, Boolean.FALSE);
				}
				l.setFont(l.getFont().deriveFont(attributes));

				// Don't repaint the label yet
				RepaintManager.currentManager(l).markCompletelyClean(l);
				// If we don't repaint everything, the transparency compounds
				LineGraph.this.repaint(l.getX() - 2, l.getY() - 2, l.getWidth() + 2, l.getHeight() + 2);
			}
		});
		leg.addLabel(l);
	}

	@Override
	public void scroll() {
		final float unitsPerView = (float) (this.getWidth() / getWidthScale());
		if (isAutoScroll() && unitsPerView - getMaxVisibleX() <= 0 && !isPanning) {
			xOffset++;
		}
	}

	private boolean isPanning = false;

	@Override
	public void plot(final Graphics graphics) {

		final double widthScale = getWidthScale();
		final float unitsPerView = (float) (this.getWidth() / widthScale);
		final Graphics2D g = (Graphics2D) graphics;
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setStroke(new BasicStroke(1.2F));
		final int maxY = getMaxVisibleY();
		g.setFont(this.getFont());

		final double heightScale = getHeightScale();

		final int minY = getMinVisibleY();

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
					final int approx = (maxY - minY) / 10;
					curY -= heightScale * (approx < 1 ? 1 : approx);
				}
			}
		}

		for (final Line l : lines) {
			if (l.isVisible()) {
				g.setColor(l.getColor());
				Point lastPoint = null;

				for (int i = xOffset; i < l.getPoints().length; i++) {
					// Stop plotting if these points are out of range
					if (i - xOffset > unitsPerView) {
						break;
					}

					final Point realPt = new Point(i - xOffset, l.getPoints()[i].y);
					final Point scaledPt = new Point(realPt.x, realPt.y - minY);

					// Draw points
					if (showPointXLabel() || showPointYLabel()) {
						g.drawString(showPointXLabel() && showPointYLabel() ? "(" + realPt.x + ", " + realPt.y + ")" : showPointXLabel() ? Integer.toString(realPt.x) : showPointYLabel() ? Integer.toString(realPt.y) : "", (int) (scaledPt.x * widthScale + 4), getHeight() - (int) (scaledPt.y * heightScale) - 4);
					}
					final Point pt = new Point((int) (scaledPt.x * widthScale), getHeight() - (int) (scaledPt.y * heightScale));

					if (lastPoint != null) {
						g.drawLine(lastPoint.x, lastPoint.y - 2, pt.x, pt.y - 2);
					}
					lastPoint = pt;
				}
			}
		}
	}

	private int xOffset = 0;

	/**
	 * Gets the height scale of this graph
	 * @return
	 */
	private double getHeightScale() {
		final int canY = getHeight() - (int) (getHeight() * .1);
		final int maxY = getMaxVisibleY();
		final int minY = getMinVisibleY();
		return maxY - minY > 0 ? (double) canY / (double) (maxY - minY) : 1;
	}

	/**
	 * Gets the minimum y-value on this graph
	 * @return
	 */
	public int getMinY() {
		int minY = Integer.MAX_VALUE;
		for (final Line l : lines) {
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
		final int curMax = xOffset + (int) (getWidth() / getWidthScale());
		for (final Line l : lines) {
			if (l.isVisible()) {
				for (int i = xOffset; i < l.getPointCount(); i++) {
					if (i > curMax) {
						break;
					}
					final int y = l.getPoints()[i].y;
					if (y < minY) {
						minY = y;
					}
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
		for (final Line l : lines) {
			if (l.getMaxY() > maxY) {
				maxY = l.getMaxY();
			}
		}
		return maxY;
	}

	/**
	 * Gets the highest x-value on this graph
	 * @return the highest x-value
	 */
	public int getMaxX() {
		int maxX = 0;
		for (final Line l : lines) {
			final int x = l.getPointCount();
			if (x > maxX) {
				maxX = x;
			}
		}
		return maxX;
	}

	/**
	 * Gets the highest visible x-value on this graph
	 * @return the highest visible x-value
	 */
	public int getMaxVisibleX() {
		int maxX = 0;
		for (final Line l : lines) {
			if (l.isVisible()) {
				final int x = l.getPointCount();
				if (x > maxX) {
					maxX = x;
				}
			}
		}
		return maxX - xOffset;
	}

	/**
	 * Gets the highest visible y-value on this graph
	 * @return
	 */
	public int getMaxVisibleY() {
		int maxY = Integer.MIN_VALUE;
		final int curMax = xOffset + (int) (getWidth() / getWidthScale());
		for (final Line l : lines) {
			if (l.isVisible()) {
				for (int i = xOffset; i < l.getPointCount(); i++) {
					if (i > curMax) {
						break;
					}
					final int y = l.getPoints()[i].y;
					if (y > maxY) {
						maxY = y;
					}
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

	public Point scaleUp(final Point p) {
		return new Point((int) (p.x * getWidthScale()), getHeight() - (int) (p.y * getHeightScale()));
	}

	/**
	 * Shifts this graph's view by the specified amount of pixels, or the maximum amount possible if {@code amount} exceeds it.
	 * @param amt the amount, in points, to shift this graph's view
	 */
	public void shift(final int amt) {
		final float unitsPerView = (float) (this.getWidth() / getWidthScale());

		if (amt > 0) {
			if (getMaxVisibleX() > unitsPerView + 1) {
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
		scroll();
		repaint();
	}

	@Override
	public void update(final Graphics g) {
		paint(g);
	}
}
