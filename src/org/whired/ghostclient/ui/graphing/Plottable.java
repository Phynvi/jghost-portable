package org.whired.ghostclient.ui.graphing;

import java.awt.Graphics;

/**
 * Contains methods for plotting points on a graph
 *
 * @author Whired
 */
public interface Plottable {

	/**
	 * Invoked when a plottable is to be plotted
	 */
	public void plot(Graphics g);
}
