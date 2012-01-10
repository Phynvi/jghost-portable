package org.whired.ghostclient.client.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A rounded version of a JButton. Intended modernize the look and feel of the
 * original JButton.
 * 
 * @author Whired
 */
public class JRoundedButton extends JButton {
	/**
	 * Creates a rounded button with text and size
	 * preferredWidth*preferredHeight.
	 * 
	 * @param text the text to display on the rounded button
	 * @param preferredWidth the preferred width of the rounded button
	 * @param preferredHeight the preferred height of the rounded button
	 */
	public JRoundedButton(String text, int preferredWidth, int preferredHeight) {
		super(text);
		setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		setBorderPainted(false);
	}

	/**
	 * Creates a rounded button with text.
	 * 
	 * @param text the text to display on the rounded button
	 */
	public JRoundedButton(String text) {
		super(text);
		setBorderPainted(false);
	}

	/**
	 * Creates a rounded button with initial text and an icon.
	 * 
	 * @param text the text of the rounded button
	 * @param the image to display on the rounded button
	 */
	public JRoundedButton(String text, Icon icon) {
		super(text, icon);
		setBorderPainted(false);
	}

	/**
	 * Creates a rounded button where properties are taken from the Action
	 * supplied.
	 * 
	 * @param a the Action used to specify the new rounded button
	 */
	public JRoundedButton(Action a) {
		super(a);
		setBorderPainted(false);
	}

	/**
	 * Creates a rounded button with an icon.
	 * 
	 * @param icon the image to display on the rounded button
	 */
	public JRoundedButton(Icon icon) {
		super(icon);
		setBorderPainted(false);
	}

	/**
	 * Creates a rounded button with no set text or icon.
	 */
	public JRoundedButton() {
		setBorderPainted(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		Shape firstClip = g.getClip();
		RoundRectangle2D rect = new RoundRectangle2D.Double();
		double arc = Math.ceil(getSize().getHeight() / 3);
		rect.setRoundRect(0, 0, Math.ceil(getSize().getWidth()), Math.ceil(getSize().getHeight()), arc, arc);
		Area secondClip = new Area(getBounds());
		secondClip.subtract(new Area(rect));
		Area finalClip = new Area(firstClip);
		finalClip.subtract(secondClip);
		g2.setClip(finalClip);
		super.paintComponent(g2);
		Color[] gradients;
		if (getModel().isRollover()) {
			gradients = new Color[] { new Color(184, 207, 229), new Color(122, 138, 153), new Color(184, 207, 229) };
		}
		else {
			gradients = new Color[] { new Color(122, 138, 153) };
		}
		for (int i = 0; i < gradients.length; i++) {
			arc -= 2;
			g2.setColor(gradients[i]);
			g2.drawRoundRect(i + 1, i + 1, (int) Math.ceil(getSize().getWidth() - 2) - i * 2, (int) Math.ceil(getSize().getHeight() - 2) - i * 2, (int) arc, (int) arc);
		}
	}
}
