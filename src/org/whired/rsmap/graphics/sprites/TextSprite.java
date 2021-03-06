package org.whired.rsmap.graphics.sprites;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;

import org.whired.rsmap.graphics.RSCanvas;

/**
 * Represents a string of text that can be drawn on the map
 * @author Whired
 */
public class TextSprite extends Sprite {

	/** This sprite's text */
	private String text;
	/** Acceptable chars to display */
	private static final String ACCEPTABLE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	/** The values for acceptable chars */
	private final int[] charValues;
	/** The color of this sprite's text */
	public int hexRGB = 0x000000;
	/** Whether or not this sprite's text should be horizontally centered */
	public boolean isCentered;
	/** Whether or not this sprite's text should display a drop-shadow */
	public boolean hasDropShadow;
	public int yOffset;
	private int textWidth;

	public TextSprite(final String text, final Font font, final int hexRGB, final boolean isCentered, final boolean hasDropShadow, final Component component) {
		this(text, font, isCentered, hasDropShadow, component);
		this.hexRGB = hexRGB;
	}

	public TextSprite(final String text, final Font font, final boolean isCentered, final boolean hasDropShadow, final Component component) {
		super(0, 0);
		this.charValues = new int[256];
		for (int i = 0; i < 256; i++) {
			int j = ACCEPTABLE_CHARS.indexOf(i);
			if (j == -1) {
				j = 74;
			}
			charValues[i] = j * 9;
		}
		this.text = text;
		this.isCentered = isCentered;
		this.hasDropShadow = hasDropShadow;
		spritePixels = new int[0x186a0]; // 100,000
		yOffset = 855;

		for (int j = 0; j < 95; j++) {
			copyCharPixels(font, ACCEPTABLE_CHARS.charAt(j), j, false, component);
		}

		// This bit shrinks down spritePixels
		final int abyte0[] = new int[yOffset];
		for (int i1 = 0; i1 < yOffset; i1++) {
			abyte0[i1] = spritePixels[i1];
		}
		spritePixels = abyte0;
	}

	@Override
	public void drawSprite(final int x, final int y, final RSCanvas canvas) {
		parseAndRenderString(x, y, canvas);
	}

	private void drawSprite(final int i, final int x, final int y, final int hexRGB, final RSCanvas canvas) {
		int adjustedX = x + spritePixels[i + 5];
		int adjustedY = y - spritePixels[i + 6];
		int k1 = spritePixels[i + 3];
		int l1 = spritePixels[i + 4];
		int i2 = spritePixels[i] * 16384 + spritePixels[i + 1] * 128 + spritePixels[i + 2];
		int scanOffset = adjustedX + adjustedY * canvas.getWidth();
		int k2 = canvas.getWidth() - k1;
		int l2 = 0;
		if (adjustedY < canvas.startY) {
			final int i3 = canvas.startY - adjustedY;
			l1 -= i3;
			adjustedY = canvas.startY;
			i2 += i3 * k1;
			scanOffset += i3 * canvas.getWidth();
		}
		if (adjustedY + l1 >= canvas.endY) {
			l1 -= adjustedY + l1 - canvas.endY + 1;
		}
		if (adjustedX < canvas.startX) {
			final int j3 = canvas.startX - adjustedX;
			k1 -= j3;
			adjustedX = canvas.startX;
			i2 += j3;
			scanOffset += j3;
			l2 += j3;
			k2 += j3;
		}
		if (adjustedX + k1 >= canvas.endX) {
			final int k3 = adjustedX + k1 - canvas.endX + 1;
			k1 -= k3;
			l2 += k3;
			k2 += k3;
		}
		if (k1 > 0 && l1 > 0) {
			drawLetter(canvas.pixels, spritePixels, hexRGB, i2, scanOffset, k1, l1, k2, l2);
		}
	}

	private void parseAndRenderString(final int x, int y, final RSCanvas canvas) {
		int lineCount = 1;
		String string = getText();
		for (int i13 = 0; i13 < string.length(); i13++) {
			if (string.charAt(i13) == '/') {
				lineCount++;
			}
		}
		y -= method40() * (lineCount - 1) / 2;
		y += getStringHeight() / 2;
		do {
			final int newLineIndex = string.indexOf("/");
			// No new lines
			if (newLineIndex == -1) {
				attemptRenderString(string, x, y, canvas);
				break;
			}
			final String s1 = string.substring(0, newLineIndex);
			attemptRenderString(s1, x, y, canvas);
			y += method40();
			string = string.substring(newLineIndex + 1);
		}
		while (true);
	}

	private void attemptRenderString(final String string, int x, final int y, final RSCanvas canvas) {
		final int stringHalfWidth = isCentered ? getStringWidth(string) / 2 : 0;
		final int stringHeight = getStringHeight();
		// Only render if string is in bounds
		if (x - stringHalfWidth <= canvas.endX && x + stringHalfWidth >= canvas.startX && y - stringHeight <= canvas.endY && y > 0) {
			x -= stringHalfWidth;
			try {
				for (int l = 0; l < string.length(); l++) {
					final int i1 = charValues[string.charAt(l)];
					if (hasDropShadow) {
						drawSprite(i1, x + 1, y, 0, canvas);
						drawSprite(i1, x, y + 1, 0, canvas);
						drawSprite(i1, x - 1, y, 0, canvas);
						drawSprite(i1, x, y - 1, 0, canvas);
					}
					drawSprite(i1, x, y, hexRGB, canvas);
					x += spritePixels[i1 + 7];
				}
			}
			catch (final Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	@Override
	public int getWidth() {
		return textWidth;
	}

	@Override
	public int getHeight() {
		return getStringHeight();
	}

	private int getStringWidth(final String s) {
		int i = 0;
		for (int j = 0; j < s.length(); j++) {
			if (s.charAt(j) == '@' && j + 4 < s.length() && s.charAt(j + 4) == '@') {
				j += 4;
			}
			else if (s.charAt(j) == '~' && j + 4 < s.length() && s.charAt(j + 4) == '~') {
				j += 4;
			}
			else {
				i += spritePixels[charValues[s.charAt(j)] + 7];
			}
		}
		return i;
	}

	private int getStringHeight() {
		return spritePixels[6];
	}

	private void copyCharPixels(final Font font, final char c, final int i, boolean bool, final Component component) {
		final FontMetrics metrics = component.getFontMetrics(font);
		int i_0_ = metrics.charWidth(c);
		final int i_1_ = i_0_;
		if (bool) {
			try {
				if (c == '/') {
					bool = false;
				}
				if (c == 'f' || c == 't' || c == 'w' || c == 'v' || c == 'k' || c == 'x' || c == 'y' || c == 'A' || c == 'V' || c == 'W') {
					i_0_++;
				}
			}
			catch (final Exception exception) {
				exception.printStackTrace();
			}
		}
		final int i_2_ = metrics.getMaxAscent();
		final int i_3_ = metrics.getMaxAscent() + metrics.getMaxDescent();
		final int i_4_ = metrics.getHeight();
		final Image image = component.createImage(i_0_, i_3_);
		final Graphics graphics = image.getGraphics();
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, i_0_, i_3_);
		graphics.setColor(Color.white);
		graphics.setFont(font);
		graphics.drawString(c + "", 0, i_2_);
		if (bool) {
			graphics.drawString(c + "", 1, i_2_);
		}
		final int[] is = new int[i_0_ * i_3_];
		final PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, i_0_, i_3_, is, 0, i_0_);
		try {
			pixelgrabber.grabPixels();
		}
		catch (final Exception exception) {
			/* empty */
		}
		image.flush();
		int i_5_ = 0;
		int i_6_ = 0;
		int i_7_ = i_0_;
		int i_8_ = i_3_;
		while_0_: for (int i_9_ = 0; i_9_ < i_3_; i_9_++) {
			for (int i_10_ = 0; i_10_ < i_0_; i_10_++) {
				final int i_11_ = is[i_10_ + i_9_ * i_0_];
				if ((i_11_ & 16777215) != 0) {
					i_6_ = i_9_;
					break while_0_;
				}
			}
		}
		while_1_: for (int i_12_ = 0; i_12_ < i_0_; i_12_++) {
			for (int i_13_ = 0; i_13_ < i_3_; i_13_++) {
				final int i_14_ = is[i_12_ + i_13_ * i_0_];
				if ((i_14_ & 16777215) != 0) {
					i_5_ = i_12_;
					break while_1_;
				}
			}
		}
		while_2_: for (int i_15_ = i_3_ - 1; i_15_ >= 0; i_15_--) {
			for (int i_16_ = 0; i_16_ < i_0_; i_16_++) {
				final int i_17_ = is[i_16_ + i_15_ * i_0_];
				if ((i_17_ & 16777215) != 0) {
					i_8_ = i_15_ + 1;
					break while_2_;
				}
			}
		}
		while_3_: for (int i_18_ = i_0_ - 1; i_18_ >= 0; i_18_--) {
			for (int i_19_ = 0; i_19_ < i_3_; i_19_++) {
				final int i_20_ = is[i_18_ + i_19_ * i_0_];
				if ((i_20_ & 16777215) != 0) {
					i_7_ = i_18_ + 1;
					break while_3_;
				}
			}
		}
		spritePixels[i * 9] = (byte) (yOffset / 16384);
		spritePixels[i * 9 + 1] = (byte) (yOffset / 128 & 127);
		spritePixels[i * 9 + 2] = (byte) (yOffset & 127);
		spritePixels[i * 9 + 3] = (byte) (i_7_ - i_5_);
		spritePixels[i * 9 + 4] = (byte) (i_8_ - i_6_);
		spritePixels[i * 9 + 5] = (byte) i_5_;
		spritePixels[i * 9 + 6] = (byte) (i_2_ - i_6_);
		spritePixels[i * 9 + 7] = (byte) i_1_;
		spritePixels[i * 9 + 8] = (byte) i_4_;
		for (int i_21_ = i_6_; i_21_ < i_8_; i_21_++) {
			for (int i_22_ = i_5_; i_22_ < i_7_; i_22_++) {
				final int i_23_ = is[i_22_ + i_21_ * i_0_] & 255;
				spritePixels[yOffset++] = (byte) i_23_;
			}
		}
	}

	private void drawLetter(final int ai[], final int abyte0[], final int i, int j, int k, int l, final int i1, final int j1, final int k1) {
		try {
			final int l1 = -(l >> 2);
			l = -(l & 3);
			for (int i2 = -i1; i2 < 0; i2++) {
				for (int j2 = l1; j2 < 0; j2++) {
					if (abyte0[j++] != 0) {
						ai[k++] = i;
					}
					else {
						k++;
					}
					if (abyte0[j++] != 0) {
						ai[k++] = i;
					}
					else {
						k++;
					}
					if (abyte0[j++] != 0) {
						ai[k++] = i;
					}
					else {
						k++;
					}
					if (abyte0[j++] != 0) {
						ai[k++] = i;
					}
					else {
						k++;
					}
				}

				for (int k2 = l; k2 < 0; k2++) {
					if (abyte0[j++] != 0) {
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
		catch (final Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Gets the text displayed on this sprite
	 * @return this sprite's text
	 */
	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
		textWidth = getStringWidth(this.text);
	}
}
