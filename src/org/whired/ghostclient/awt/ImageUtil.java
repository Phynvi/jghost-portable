package org.whired.ghostclient.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * Image utlities
 * @author Whired
 */
public class ImageUtil {
	/**
	 * Gets the average color from the specified icon
	 * @param icon the icon to get the color from
	 * @return the average color of the icon
	 */
	public static Color colorFromIcon(final Icon icon) {
		final BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		final Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		final int w = bi.getWidth();
		final int h = bi.getHeight();
		final int[] rgbs = new int[w * h];
		bi.getRGB(0, 0, w, h, rgbs, 0, w);
		int re = 0, gr = 0, bl = 0;
		int cts = rgbs.length;
		for (final int i : rgbs) {
			final int tr = (i & 0xff0000) >> 16;
			final int tg = (i & 0x00ff00) >> 8;
			final int tb = i & 0x0000ff;
			if (tr > 120 || tg > 120 || tb > 120) {
				re += tr;
				gr += tg;
				bl += tb;
			}
			else {
				cts--;
			}
		}
		if (cts == 0) {
			return Color.WHITE;
		}
		re /= cts;
		gr /= cts;
		bl /= cts;
		return new Color(re, gr, bl);
	}
}
