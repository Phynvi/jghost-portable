package org.whired.graph;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Represents a graph
 * @author Whired
 */
public abstract class Graph extends Container implements Plottable {

	/**
	 * The labels to show
	 */
	private int labels = Label.NONE;
	/**
	 * The color of the x-axis gridlines
	 */
	private Color gridLineXColor = new Color(255, 255, 255, 30);
	/**
	 * The color of the y-axis gridlines
	 */
	private Color gridLineYColor = new Color(255, 255, 255, 30);
	/**
	 * Whether or not this graph automatically scrolls when data is added
	 */
	private boolean autoScroll = true;
	/**
	 * The legend for this graph
	 */
	private Legend legend = new Legend();

	/**
	 * Creates a new graph with the specified width and height
	 * @param width the width of this graph
	 * @param height the height of this graph
	 */
	public Graph(final int width, final int height) {
		this();
		final Dimension d = new Dimension(width, height);
		this.setPreferredSize(d);
	}

	public Graph() {
		this.add(legend);
	}

	/**
	 * Sets the legend for this graph
	 * @param legend the legend to set
	 */
	public void setLegend(final Legend legend) {
		this.legend = legend;
	}

	/**
	 * Gets the legend for this graph
	 * @return the legend
	 */
	public Legend getLegend() {
		return this.legend;
	}

	/**
	 * Sets the labels for this graph. Accepts bitwise operations.
	 * @param labels the labels to set
	 */
	public void setLabels(final int labels) {
		this.labels = labels;
	}

	/**
	 * Sets whether or not to show labels for the x axis
	 * @param visible
	 */
	public void setShowAxisXLabel(final boolean visible) {
		this.labels = visible ? this.getLabels() | Label.AXIS_X : this.getLabels() ^ Label.AXIS_X;
	}

	/**
	 * Sets whether or not to show labels for the y axis
	 * @param visible
	 */
	public void setShowAxisYLabel(final boolean visible) {
		this.labels = visible ? this.getLabels() | Label.AXIS_Y : this.getLabels() ^ Label.AXIS_Y;
	}

	/**
	 * Sets whether or not to display gridlines for the x axis
	 * @param visible
	 */
	public void setShowGridLineX(final boolean visible) {
		this.labels = visible ? this.getLabels() | Label.GRIDLINE_X : this.getLabels() ^ Label.GRIDLINE_X;
	}

	/**
	 * Sets whether or not to display gridlines for the x axis
	 * @param visible
	 */
	public void setShowGridlineY(final boolean visible) {
		this.labels = visible ? this.getLabels() | Label.GRIDLINE_Y : this.getLabels() ^ Label.GRIDLINE_Y;
	}

	/**
	 * Sets whether or not to show labels for points' x-coordinates
	 * @param visible
	 */
	public void setShowPointXLabel(final boolean visible) {
		this.labels = visible ? this.getLabels() | Label.POINT_X : this.getLabels() ^ Label.POINT_X;
	}

	/**
	 * Sets whether or not to show labels for points' y-coordinates
	 * @param visible
	 */
	public void setShowPointYLabel(final boolean visible) {
		this.labels = visible ? this.getLabels() | Label.POINT_Y : this.getLabels() ^ Label.POINT_Y;
	}

	/**
	 * Whether or not to show labels for the x axis
	 * @return {@code true} if they are shown, otherwise {@code false}
	 */
	public boolean showAxisXLabel() {
		return (this.getLabels() & Label.AXIS_X) == Label.AXIS_X;
	}

	/**
	 * Whether or not to show labels for the y axis
	 * @return {@code true} if they are shown, otherwise {@code false}
	 */
	public boolean showAxisYLabel() {
		return (this.getLabels() & Label.AXIS_Y) == Label.AXIS_Y;
	}

	/**
	 * Whether or not to display gridlines for the x axis
	 * @return {@code true} if they are shown, otherwise {@code false}
	 */
	public boolean showGridLineX() {
		return (this.getLabels() & Label.GRIDLINE_X) == Label.GRIDLINE_X;
	}

	/**
	 * Whether or not to display gridlines for the y axis
	 * @return {@code true} if they are shown, otherwise {@code false}
	 */
	public boolean showGridLineY() {
		return (this.getLabels() & Label.GRIDLINE_Y) == Label.GRIDLINE_Y;
	}

	/**
	 * Whether or not to show labels for points' x-coordinates
	 * @return {@code true} if they are shown, otherwise {@code false}
	 */
	public boolean showPointXLabel() {
		return (this.getLabels() & Label.POINT_X) == Label.POINT_X;
	}

	/**
	 * Whether or not to show labels for points' y-coordinates
	 * @return {@code true} if they are shown, otherwise {@code false}
	 */
	public boolean showPointYLabel() {
		return (this.getLabels() & Label.POINT_Y) == Label.POINT_Y;
	}

	/**
	 * @return the gridLineXColor
	 */
	public Color getGridLineXColor() {
		return gridLineXColor;
	}

	/**
	 * @param gridLineXColor the gridLineXColor to set
	 */
	public void setGridLineXColor(final Color gridLineXColor) {
		this.gridLineXColor = gridLineXColor;
	}

	/**
	 * @return the gridLineYColor
	 */
	public Color getGridLineYColor() {
		return gridLineYColor;
	}

	/**
	 * @param gridLineYColor the gridLineYColor to set
	 */
	public void setGridLineYColor(final Color gridLineYColor) {
		this.gridLineYColor = gridLineYColor;
	}

	/**
	 * @return the labels
	 */
	protected int getLabels() {
		return labels;
	}

	/**
	 * @return whether or not this graph autoscrolls when data is added
	 */
	public boolean isAutoScroll() {
		return autoScroll;
	}

	/**
	 * @param autoscrolls sets whether or not this graph autoscrolls when data is added
	 */
	public void setAutoScrolls(final boolean autoscrolls) {
		this.autoScroll = autoscrolls;
	}

	/**
	 * The labels that can be shown on this graph
	 */
	protected class Label {

		/**
		 * No labels
		 */
		public static final int NONE = 0x0;
		/**
		 * Labels for a point's x-coordinate
		 */
		public static final int POINT_X = 0x1;
		/**
		 * Labels for a point's y-coordinate
		 */
		public static final int POINT_Y = 0x2;
		/**
		 * Labels for the x axis
		 */
		public static final int AXIS_X = 0x4;
		/**
		 * Labels for the y axis
		 */
		public static final int AXIS_Y = 0x8;
		/**
		 * Gridlines for the x axis
		 */
		public static final int GRIDLINE_X = 0x16;
		/**
		 * Gridlines for the y axis
		 */
		public static final int GRIDLINE_Y = 0x32;
		/**
		 * All labels
		 */
		public static final int ALL = POINT_X | POINT_Y | AXIS_X | AXIS_Y | GRIDLINE_X | GRIDLINE_Y;
	}

	@Override
	public final void paint(final Graphics g) {
		plot(g);
		super.paint(g);
	}
}
