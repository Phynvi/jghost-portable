package org.whired.rsmap.graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import org.whired.ghost.Constants;
import org.whired.rsmap.graphics.sprites.TextSprite;
import org.whired.rsmap.ui.MapButton;

public abstract class RSCanvas extends Component implements MouseWheelListener, ImageProducer, ImageObserver {

	private int xDragged, yDragged, xPressed, yPressed;
	public int pixels[];
	public int startY, endY, startX, endX;
	private final ColorModel colorModel = new DirectColorModel(32, 0xff0000, 0x00ff00, 0x0000ff);
	private ImageConsumer imageConsumer;
	private Image image;
	public TextRenderer textRenderer;
	private boolean ignoreDrag = false;

	public RSCanvas() {
		setFocusable(true);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(final MouseEvent mouseevent) {
				requestFocusInWindow();
				if (!clicked(mouseevent.getPoint())) {
					xPressed = mouseevent.getX();
					yPressed = mouseevent.getY();
					RSCanvas.this.mouseDown(xPressed, yPressed);
				}
				else {
					ignoreDrag = true;
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (xDragged == xPressed && yDragged == yPressed && !ignoreDrag) {
					RSCanvas.this.mouseUp(xPressed, yPressed);
				}
				ignoreDrag = false;
			}

			@Override
			public void mouseExited(final MouseEvent mouseevent) {
				xDragged = -1;
				yDragged = -1;
			}
		});

		addMouseWheelListener(RSCanvas.this);

		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(final MouseEvent mouseevent) {
				if (!ignoreDrag) {
					xDragged = mouseevent.getX();
					yDragged = mouseevent.getY();
				}
			}

			@Override
			public void mouseDragged(final MouseEvent mouseevent) {
				if (!ignoreDrag) {
					xDragged = mouseevent.getX();
					yDragged = mouseevent.getY();
					RSCanvas.this.mouseDragged(xPressed, yPressed, xDragged, yDragged);
					repaint();
				}
			}
		});

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent keyevent) {
				RSCanvas.this.keyPressed(keyevent.getKeyCode());
			}

			@Override
			public void keyReleased(final KeyEvent keyevent) {
			}
		});
	}

	public abstract void mouseDragged(int oldX, int oldY, int newX, int newY);

	public abstract void keyPressed(int keyCode);

	public void loadMap() {
		Constants.getLogger().info("Initial map size: (" + getWidth() + ", " + getHeight() + ")");
		pixels = new int[getWidth() * getHeight()];
		image = createImage(RSCanvas.this);
		prepareImage(image, RSCanvas.this);

		if (imageConsumer != null) {
			imageConsumer.setPixels(0, 0, getWidth(), getHeight(), colorModel, pixels, 0, getWidth());
			imageConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}
		setDrawingArea(0, 0, getWidth(), getHeight());
	}

	@Override
	public boolean imageUpdate(final Image image, final int i, final int j, final int k, final int l, final int i1) {
		return true;
	}

	@Override
	public void requestTopDownLeftRightResend(final ImageConsumer imageconsumer) {
	}

	@Override
	public synchronized boolean isConsumer(final ImageConsumer imageconsumer) {
		return imageConsumer == imageconsumer;
	}

	@Override
	public synchronized void removeConsumer(final ImageConsumer imageconsumer) {
		if (imageConsumer == imageconsumer) {
			imageConsumer = null;
		}
	}

	@Override
	public void startProduction(final ImageConsumer imageconsumer) {
		addConsumer(imageconsumer);
	}

	@Override
	public synchronized void addConsumer(final ImageConsumer imageconsumer) {
		imageConsumer = imageconsumer;
		imageconsumer.setDimensions(getWidth(), getHeight());
		imageconsumer.setProperties(null);
		imageconsumer.setColorModel(colorModel);
		imageconsumer.setHints(14);
	}

	private void drawGraphics(final Graphics g, final int i, final int j) {
		if (imageConsumer != null) {
			imageConsumer.setPixels(0, 0, getWidth(), getHeight(), colorModel, pixels, 0, getWidth());
			imageConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}
		g.drawImage(image, i, j, this);
	}

	/**
	 * Renders a line on this canvas
	 * @param x1 the starting x-coordinate
	 * @param y1 the starting y-coordinate
	 * @param x2 the ending x-coordinate
	 * @param y2 the ending y-coordinate
	 * @param hexRGB the color of the line
	 */
	public void renderLine(int x1, int y1, final int x2, final int y2, final int hexRGB) {
		final int w = x2 - x1;
		final int h = y2 - y1;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0) {
			dx1 = -1;
		}
		else if (w > 0) {
			dx1 = 1;
		}
		if (h < 0) {
			dy1 = -1;
		}
		else if (h > 0) {
			dy1 = 1;
		}
		if (w < 0) {
			dx2 = -1;
		}
		else if (w > 0) {
			dx2 = 1;
		}
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0) {
				dy2 = -1;
			}
			else if (h > 0) {
				dy2 = 1;
			}
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			renderPoint(x1, y1, hexRGB);
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x1 += dx1;
				y1 += dy1;
			}
			else {
				x1 += dx2;
				y1 += dy2;
			}
		}
	}

	/**
	 * Renders a point on this canvas
	 * @param x the x-coordinate of the point
	 * @param y the y-coordinate of the point
	 * @param hexRGB the color of the point
	 */
	public void renderPoint(final int x, final int y, final int hexRGB) {
		if (x > startX && y > startY && x < endX && y < endY) {
			pixels[x + y * getWidth()] = hexRGB;
		}
	}

	/**
	 * Renders a horizontal line on this canvas
	 * @param lineX the starting x-coordinate of the line
	 * @param lineY the starting y-coordinate of the line
	 * @param lineLength the length of the line
	 * @param hexRGB the color of the line
	 */
	public void renderHorizontalLine(int lineX, final int lineY, int lineLength, final int hexRGB) {
		if (lineY < startY || lineY >= endY) {
			return;
		}
		if (lineX < startX) {
			lineLength -= startX - lineX;
			lineX = startX;
		}
		if (lineX + lineLength > endX) {
			lineLength = endX - lineX;
		}
		final int i1 = lineX + lineY * getWidth();
		for (int j1 = 0; j1 < lineLength; j1++) {
			pixels[i1 + j1] = hexRGB;
		}

	}

	/**
	 * Renders a vertical line on this canvas
	 * @param lineX the starting x-coordinate of the line
	 * @param lineY the starting y-coordinate of the line
	 * @param lineLength the length of the line
	 * @param hexRGB the color of the line
	 */
	public void renderVerticalLine(final int lineX, int lineY, int lineLength, final int hexRGB) {
		if (lineX < startX || lineX >= endX) {
			return;
		}
		if (lineY < startY) {
			lineLength -= startY - lineY;
			lineY = startY;
		}
		if (lineY + lineLength > endY) {
			lineLength = endY - lineY;
		}
		final int i1 = lineX + lineY * getWidth();
		for (int j1 = 0; j1 < lineLength; j1++) {
			pixels[i1 + j1 * getWidth()] = hexRGB;
		}
	}

	private final void setDrawingArea(int i, int j, int k, int l) {
		if (i < 0) {
			i = 0;
		}
		if (j < 0) {
			j = 0;
		}
		if (k > getWidth()) {
			k = getWidth();
		}
		if (l > getHeight()) {
			l = getHeight();
		}
		startX = i;
		startY = j;
		endX = k;
		endY = l;
	}

	/**
	 * Draws the given button
	 * @param button the button to draw
	 */
	public void drawButton(final MapButton button) {
		fillColor(pixels, new Dimension(getWidth(), getHeight()), button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getBackgroundColor());
		drawRect(button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getBorderColor());
		final TextSprite ts = button.getTextSprite();
		if (ts != null) {
			ts.setText(button.getText());
			ts.drawSprite(button.getX() + button.getWidth() / 2 - ts.getWidth() / 2, button.getY() + button.getHeight() / 2, this);
		}
		else {
			textRenderer.renderText(button.getText(), button.getX() + button.getWidth() / 2 + 1, button.getY() + button.getHeight() / 2 + 1 + 4, 0);
			textRenderer.renderText(button.getText(), button.getX() + button.getWidth() / 2, button.getY() + button.getHeight() / 2 + 4, 0xffffff);
		}
	}

	/**
	 * Invoked when the component is painted
	 */
	public abstract void draw();

	@Override
	public final void paint(final Graphics g) {
		try {
			draw();
		}
		catch (final NullPointerException e) {
		}
		try {
			if (g != null) {
				drawGraphics(g, 0, 0);
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Blends corners
	 * @param pixel
	 * @param size
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @param i1
	 * @param j1
	 * @param k1
	 */
	public void blendCorners(final int pixel[], final Dimension size, int i, final int j, final int k, final int l, final int i1, int j1, int k1) {
		final int l1 = size.width - l;
		if (j1 == 9) {
			j1 = 1;
			k1 = k1 + 1 & 3;
		}
		if (j1 == 10) {
			j1 = 1;
			k1 = k1 + 3 & 3;
		}
		if (j1 == 11) {
			j1 = 8;
			k1 = k1 + 3 & 3;
		}
		if (j1 == 1) {
			if (k1 == 0) {
				for (int i2 = 0; i2 < i1; i2++) {
					for (int i10 = 0; i10 < l; i10++) {
						if (i10 <= i2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j2 = i1 - 1; j2 >= 0; j2--) {
					for (int j10 = 0; j10 < l; j10++) {
						if (j10 <= j2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k2 = 0; k2 < i1; k2++) {
					for (int k10 = 0; k10 < l; k10++) {
						if (k10 >= k2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l2 = i1 - 1; l2 >= 0; l2--) {
					for (int l10 = 0; l10 < l; l10++) {
						if (l10 >= l2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else {
				return;
			}
		}
		if (j1 == 2) {
			if (k1 == 0) {
				for (int i3 = i1 - 1; i3 >= 0; i3--) {
					for (int i11 = 0; i11 < l; i11++) {
						if (i11 <= i3 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j3 = 0; j3 < i1; j3++) {
					for (int j11 = 0; j11 < l; j11++) {
						if (j11 >= j3 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k3 = 0; k3 < i1; k3++) {
					for (int k11 = l - 1; k11 >= 0; k11--) {
						if (k11 <= k3 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l3 = i1 - 1; l3 >= 0; l3--) {
					for (int l11 = l - 1; l11 >= 0; l11--) {
						if (l11 >= l3 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else {
				return;
			}
		}
		if (j1 == 3) {
			if (k1 == 0) {
				for (int i4 = i1 - 1; i4 >= 0; i4--) {
					for (int i12 = l - 1; i12 >= 0; i12--) {
						if (i12 <= i4 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j4 = i1 - 1; j4 >= 0; j4--) {
					for (int j12 = 0; j12 < l; j12++) {
						if (j12 >= j4 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k4 = 0; k4 < i1; k4++) {
					for (int k12 = 0; k12 < l; k12++) {
						if (k12 <= k4 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l4 = 0; l4 < i1; l4++) {
					for (int l12 = l - 1; l12 >= 0; l12--) {
						if (l12 >= l4 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else {
				return;
			}
		}
		if (j1 == 4) {
			if (k1 == 0) {
				for (int i5 = i1 - 1; i5 >= 0; i5--) {
					for (int i13 = 0; i13 < l; i13++) {
						if (i13 >= i5 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j5 = 0; j5 < i1; j5++) {
					for (int j13 = 0; j13 < l; j13++) {
						if (j13 <= j5 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k5 = 0; k5 < i1; k5++) {
					for (int k13 = l - 1; k13 >= 0; k13--) {
						if (k13 >= k5 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l5 = i1 - 1; l5 >= 0; l5--) {
					for (int l13 = l - 1; l13 >= 0; l13--) {
						if (l13 <= l5 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else {
				return;
			}
		}
		if (j1 == 5) {
			if (k1 == 0) {
				for (int i6 = i1 - 1; i6 >= 0; i6--) {
					for (int i14 = l - 1; i14 >= 0; i14--) {
						if (i14 >= i6 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j6 = i1 - 1; j6 >= 0; j6--) {
					for (int j14 = 0; j14 < l; j14++) {
						if (j14 <= j6 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k6 = 0; k6 < i1; k6++) {
					for (int k14 = 0; k14 < l; k14++) {
						if (k14 >= k6 >> 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l6 = 0; l6 < i1; l6++) {
					for (int l14 = l - 1; l14 >= 0; l14--) {
						if (l14 <= l6 << 1) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else {
				return;
			}
		}
		if (j1 == 6) {
			if (k1 == 0) {
				for (int i7 = 0; i7 < i1; i7++) {
					for (int i15 = 0; i15 < l; i15++) {
						if (i15 <= l / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j7 = 0; j7 < i1; j7++) {
					for (int j15 = 0; j15 < l; j15++) {
						if (j7 <= i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k7 = 0; k7 < i1; k7++) {
					for (int k15 = 0; k15 < l; k15++) {
						if (k15 >= l / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l7 = 0; l7 < i1; l7++) {
					for (int l15 = 0; l15 < l; l15++) {
						if (l7 >= i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
		}
		if (j1 == 7) {
			if (k1 == 0) {
				for (int i8 = 0; i8 < i1; i8++) {
					for (int i16 = 0; i16 < l; i16++) {
						if (i16 <= i8 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j8 = i1 - 1; j8 >= 0; j8--) {
					for (int j16 = 0; j16 < l; j16++) {
						if (j16 <= j8 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k8 = i1 - 1; k8 >= 0; k8--) {
					for (int k16 = l - 1; k16 >= 0; k16--) {
						if (k16 <= k8 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l8 = 0; l8 < i1; l8++) {
					for (int l16 = l - 1; l16 >= 0; l16--) {
						if (l16 <= l8 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
		}
		if (j1 == 8) {
			if (k1 == 0) {
				for (int i9 = 0; i9 < i1; i9++) {
					for (int i17 = 0; i17 < l; i17++) {
						if (i17 >= i9 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1) {
				for (int j9 = i1 - 1; j9 >= 0; j9--) {
					for (int j17 = 0; j17 < l; j17++) {
						if (j17 >= j9 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2) {
				for (int k9 = i1 - 1; k9 >= 0; k9--) {
					for (int k17 = l - 1; k17 >= 0; k17--) {
						if (k17 >= k9 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3) {
				for (int l9 = 0; l9 < i1; l9++) {
					for (int l17 = l - 1; l17 >= 0; l17--) {
						if (l17 >= l9 - i1 / 2) {
							pixel[i++] = k;
						}
						else {
							pixel[i++] = j;
						}
					}

					i += l1;
				}
			}
		}
	}

	/**
	 * Paints a rectangle at the specified coordinates
	 * @param rectX the x-coordinate (absolute to this canvas)
	 * @param rectY the y-coordinate (absolute to this canvas)
	 * @param rectWidth the width of the rectangle
	 * @param rectHeight the height of the rectangle
	 * @param hexRGB the color of the rectangle
	 * @param alpha the transparency of the rectangle (0-255)
	 */
	public void fillRect(int rectX, int rectY, int rectWidth, int rectHeight, final int hexRGB, final int alpha) {
		// Validate area and resize as necessary
		if (rectX < startX) {
			rectWidth -= startX - rectX;
			rectX = startX;
		}
		if (rectY < startY) {
			rectHeight -= startY - rectY;
			rectY = startY;
		}
		if (rectX + rectWidth > endX) {
			rectWidth = endX - rectX;
		}
		if (rectY + rectHeight > endY) {
			rectHeight = endY - rectY;
		}

		// Calculate transparency
		final int k1 = 256 - alpha;
		final int l1 = (hexRGB >> 16 & 0xff) * alpha;
		final int i2 = (hexRGB >> 8 & 0xff) * alpha;
		final int j2 = (hexRGB & 0xff) * alpha;
		final int j3 = getWidth() - rectWidth;
		int k3 = rectX + rectY * getWidth();

		// Set the values
		for (int l3 = 0; l3 < rectHeight; l3++) {
			for (int i4 = -rectWidth; i4 < 0; i4++) {
				final int k2 = (pixels[k3] >> 16 & 0xff) * k1;
				final int l2 = (pixels[k3] >> 8 & 0xff) * k1;
				final int i3 = (pixels[k3] & 0xff) * k1;
				final int j4 = (l1 + k2 >> 8 << 16) + (i2 + l2 >> 8 << 8) + (j2 + i3 >> 8);
				pixels[k3++] = j4;
			}
			k3 += j3;
		}
	}

	// This actually draws a rectangle..
	public void drawRect(final int rectX, final int rectY, final int rectWidth, final int rectHeight, final int hexRGB) {
		renderHorizontalLine(rectX, rectY, rectWidth, hexRGB);
		renderHorizontalLine(rectX, rectY + rectHeight - 1, rectWidth, hexRGB);
		renderVerticalLine(rectX, rectY, rectHeight, hexRGB);
		renderVerticalLine(rectX + rectWidth - 1, rectY, rectHeight, hexRGB);
	}

	/**
	 * Fills a specified area with the specified color
	 * @param pix the pixels to manipulate
	 * @param size the size of the area that the pixels cover
	 * @param x the x-coordinate of the area to fill
	 * @param y the y-coordinate of the area to fill
	 * @param width the width of the area to fill
	 * @param height the height of the area to fill
	 * @param hexColor the color to fill
	 */
	public void fillColor(final int[] pix, final Dimension size, int x, int y, int width, int height, final int hexColor) {
		if (x < startX) {
			width -= startX - x;
			x = startX;
		}
		if (y < startY) {
			height -= startY - y;
			y = startY;
		}
		if (x + width > endX) {
			width = endX - x;
		}
		if (y + height > endY) {
			height = endY - y;
		}
		final int j1 = size.width - width;
		int k1 = x + y * size.width;
		for (int l1 = -height; l1 < 0; l1++) {
			for (int i2 = -width; i2 < 0; i2++) {
				pix[k1++] = hexColor;
			}
			k1 += j1;
		}
	}

	/**
	 * Invoked when the left mouse button is pressed down
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public abstract void mouseDown(int x, int y);

	/**
	 * Invoked when this canvas has been clicked
	 * @param p the point at which the canvas was clicked
	 */
	public abstract boolean clicked(Point p);

	/**
	 * Invoked when the left mouse button is released
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public abstract void mouseUp(int x, int y);
}
