package org.whired.graph;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Displays information about the graph
 * @author Whired
 */
public class Legend extends JPanel {

	/**
	 * Creates a new empty legend
	 */
	public Legend() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBounds(-1, -1, 2, 2);
	}

	/**
	 * Creates a new legend with the specified labels
	 * @param labels the labels to add
	 */
	public Legend(final JLabel[] labels) {
		this();
		for (final JLabel l : labels) {
			addLabel(l);
		}
	}

	/**
	 * Adds a label to this legend
	 * @param label the label to add
	 */
	public void addLabel(final JLabel label) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Dimension d = label.getPreferredSize();
				final int tw = getWidth();
				final Insets is = getBorder().getBorderInsets(Legend.this);
				setSize(d.width > tw ? d.width + is.left + is.right : tw, getHeight() + d.height);
				label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				add(label);
			}
		});
	}

	/**
	 * Adds a label for the specified line
	 * @param line the line to add a label for
	 */
	public void addLabel(final Line line, final MouseListener listener) {
		final String lbl = line.getLabel();
		final String text = lbl == null ? "line" : lbl;
		final JLabel l = new JLabel(text);
		l.setForeground(line.getColor());
		l.addMouseListener(listener);
		addLabel(l);
	}
}
