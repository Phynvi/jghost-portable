package org.whired.ghostclient.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

public class RoundedBorder implements Border {

	private final int radius;
	private Insets insets = new Insets(2, 3, 2, 3);
	private final Color color;

	public RoundedBorder(final int radius) {
		this(radius, Color.BLACK);
	}

	public RoundedBorder(final int radius, final Color color) {
		this.radius = radius;
		this.color = color;
	}

	public RoundedBorder(final Color color) {
		this(5, color);
	}

	@Override
	public Insets getBorderInsets(final Component c) {
		return insets;
	}

	public void setBorderInsets(Insets insets) {
		this.insets = insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
		g.setColor(color);
		((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
	}
}
