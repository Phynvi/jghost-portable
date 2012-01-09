package org.whired.rsmap.graphics.sprites;

import java.awt.image.PixelGrabber;
import java.net.URL;
import javax.swing.ImageIcon;
import org.whired.rsmap.graphics.RSCanvas;

/**
 * 
 * @author Whired
 */
public class StaticSprite extends Sprite {
	public StaticSprite(ImageIcon image) throws InterruptedException {
		super(image.getIconWidth(), image.getIconHeight());
		spritePixels = new int[getWidth() * getHeight()];
		PixelGrabber pixelgrabber = new PixelGrabber(image.getImage(), 0, 0, getWidth(), getHeight(), spritePixels, 0, getWidth());
		pixelgrabber.grabPixels();
		image = null;
	}

	public StaticSprite(String filename) throws InterruptedException {
		this(new ImageIcon(filename));
	}

	public StaticSprite(URL url) throws InterruptedException {
		this(new ImageIcon(url));
	}

	public void drawSprite(int x, int y, RSCanvas area) {
		int l = x + y * area.getWidth();
		int i1 = 0;
		int j1 = getHeight();
		int k1 = getWidth();
		int l1 = area.getWidth() - k1;
		int i2 = 0;
		if (y < area.startY) {
			int j2 = area.startY - y;
			j1 -= j2;
			y = area.startY;
			i1 += j2 * k1;
			l += j2 * area.getWidth();
		}
		if (y + j1 > area.endY) {
			j1 -= (y + j1) - area.endY;
		}
		if (x < area.startX) {
			int k2 = area.startX - x;
			k1 -= k2;
			x = area.startX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (x + k1 > area.endX) {
			int l2 = (x + k1) - area.endX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {
			method349(area.pixels, spritePixels, i1, l, k1, j1, l1, i2);
		}
	}

	private void method349(int ai[], int ai1[], int j, int k, int l, int i1, int j1, int k1) {
		int i;// was parameter
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				}
				else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				}
				else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				}
				else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				}
				else {
					k++;
				}
			}

			for (int k2 = l; k2 < 0; k2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				}
				else {
					k++;
				}
			}

			k += j1;
			j += k1;
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
