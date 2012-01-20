package org.whired.ghostclient.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

public class RoundedBorder implements Border {

	private final int radius;
	private final Insets insets = new Insets(2, 3, 2, 3);
	private final Color color;

	public RoundedBorder(int radius) {
		this(radius, Color.BLACK);
	}

	public RoundedBorder(int radius, Color color) {
		this.radius = radius;
		this.color = color;
	}

	public RoundedBorder(Color color) {
		this(5, color);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(color);
		((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
	}
}
