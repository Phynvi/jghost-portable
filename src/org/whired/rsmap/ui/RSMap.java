package org.whired.rsmap.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.rsmap.graphics.OverviewArea;
import org.whired.rsmap.graphics.RSCanvas;
import org.whired.rsmap.graphics.TextRenderer;
import org.whired.rsmap.graphics.sprites.Sprite;
import org.whired.rsmap.graphics.sprites.TextSprite;
import org.whired.rsmap.io.ByteBuffer;
import org.whired.rsmap.io.CacheLoader;
import org.whired.rsmap.io.FileOperations;

public abstract class RSMap extends RSCanvas {

	private int mapStartX;
	private int mapStartY;
	private int mapWidth;
	private int mapHeight;
	private int anIntArray115[];
	private int anIntArray116[];
	private int anIntArrayArray117[][];
	private int anIntArrayArray118[][];
	private byte aByteArrayArray119[][];
	private byte mapObjects[][];
	private int minimapHeight;
	private int minimapWidth;
	private int minimapX;
	private int minimapY;
	private boolean showOverview = true;
	private OverviewArea overviewArea;
	private int dragStartX;
	private int dragStartY;
	private double currentZoomLevel = 3D;
	private int overviewCenterX;
	private int overviewCenterY;
	private final ArrayList<Sprite> mapSprites = new ArrayList<Sprite>();
	private final Font defaultFont = loadPackagedFont("ubuntu");
	public TextSprite defaultTextSprite;

	public synchronized void addSprite(final Sprite s) {
		mapSprites.add(s);
	}

	/**
	 * Loads a custom map located at the given path
	 * @param cachePath the path of the map to load
	 */
	public void loadMap(final String cachePath) {
		super.loadMap();
		defaultTextSprite = new TextSprite("ts", defaultFont, 0xFFFFFF, false, true, this);
		final MapButton mb1 = new MapButton("Minimap", 2, RSMap.super.getHeight() - 14 - 2, 40, 14, 0xBEC7E8, 0x6382BF) {

			@Override
			public void draw() {
				drawButton(this);
			}

			@Override
			public void clicked() {
				showOverview = !showOverview;
				repaint();
			}
		};
		mb1.setTextSprite(defaultTextSprite);
		addButton(mb1);

		Constants.getLogger().info("Loading map from disk..");
		CacheLoader cacheLoader;
		try {
			cacheLoader = getMapLoader(new FileInputStream(cachePath));
		}
		catch (final Throwable t) {
			try {
				Constants.getLogger().log(Level.WARNING, "External cache load fail, falling back to default: ", t);
				cacheLoader = getMapLoader(this.getClass().getResourceAsStream("/org/whired/rsmap/resources/worldmap.dat"));
			}
			catch (final Throwable t2) {
				Constants.getLogger().log(Level.SEVERE, "Internal cache load fail, map not loaded", t2);
				return;
			}
		}

		Constants.getLogger().info("Map loaded. Rendering..");
		ByteBuffer byteBuffer = new ByteBuffer(cacheLoader.loadNode("size.dat"));
		mapStartX = byteBuffer.getShort();
		mapStartY = byteBuffer.getShort();
		mapWidth = byteBuffer.getShort();
		mapHeight = byteBuffer.getShort();

		overviewCenterX = 2460 - mapStartX;
		overviewCenterY = mapStartY + mapHeight - 3090;
		minimapHeight = 180;
		minimapWidth = mapWidth * minimapHeight / mapHeight;
		minimapX = super.getWidth() - minimapWidth - 5;
		minimapY = super.getHeight() - minimapHeight - 5;

		byteBuffer = new ByteBuffer(cacheLoader.loadNode("floorcol.dat"));
		final int length = byteBuffer.getShort();
		anIntArray115 = new int[length + 1];
		anIntArray116 = new int[length + 1];
		for (int k = 0; k < length; k++) {
			anIntArray115[k + 1] = byteBuffer.getInt();
			anIntArray116[k + 1] = byteBuffer.getInt();
		}

		final byte abyte1[][] = new byte[mapWidth][mapHeight];
		loadUnderlay(cacheLoader.loadNode("underlay.dat"), abyte1);

		anIntArrayArray118 = new int[mapWidth][mapHeight];
		aByteArrayArray119 = new byte[mapWidth][mapHeight];
		loadOverlay(cacheLoader.loadNode("overlay.dat"));

		mapObjects = new byte[mapWidth][mapHeight];
		loadObjects(cacheLoader.loadNode("loc.dat"));

		textRenderer = new TextRenderer(this, cacheLoader, "b12_full");

		anIntArrayArray117 = new int[mapWidth][mapHeight];
		method16(abyte1);
		overviewArea = new OverviewArea(this, minimapWidth, minimapHeight);

		renderMiniMap(overviewArea.area, new Dimension(minimapWidth, minimapHeight), 2, 2, mapWidth - 2, mapHeight - 2);
	}

	public void loadObjects(final byte[] locations) {
		for (int i = 0; i < locations.length;) {
			final int k = (locations[i++] & 0xff) * 64 - mapStartX;
			final int l = (locations[i++] & 0xff) * 64 - mapStartY;
			if (k > 0 && l > 0 && k + 64 < mapWidth && l + 64 < mapHeight) {
				for (int i1 = 0; i1 < 64; i1++) {
					final byte abyte4[] = mapObjects[i1 + k];
					int l1 = mapHeight - l - 1;
					for (int i2 = -64; i2 < 0; i2++) {
						do {
							final int j = locations[i++] & 0xff;
							if (j == 0) {
								break;
							}
							if (j < 29) {
								abyte4[l1] = (byte) j;
							}
						}
						while (true);
						l1--;
					}

				}
			}
			else {
				for (int j1 = 0; j1 < 64; j1++) {
					for (int k1 = -64; k1 < 0; k1++) {
						byte byte0;
						do {
							byte0 = locations[i++];
						}
						while (byte0 != 0);
					}
				}
			}
		}
	}

	public void loadUnderlay(final byte abyte0[], final byte abyte1[][]) {
		for (int i = 0; i < abyte0.length;) {
			final int j = (abyte0[i++] & 0xff) * 64 - mapStartX;
			final int k = (abyte0[i++] & 0xff) * 64 - mapStartY;
			if (j > 0 && k > 0 && j + 64 < mapWidth && k + 64 < mapHeight) {
				for (int l = 0; l < 64; l++) {
					final byte abyte2[] = abyte1[l + j];
					int i1 = mapHeight - k - 1;
					for (int j1 = -64; j1 < 0; j1++) {
						abyte2[i1--] = abyte0[i++];
					}

				}
			}
			else {
				i += 4096;
			}
		}

	}

	public void loadOverlay(final byte abyte0[]) {
		for (int i = 0; i < abyte0.length;) {
			final int j = (abyte0[i++] & 0xff) * 64 - mapStartX;
			final int k = (abyte0[i++] & 0xff) * 64 - mapStartY;
			if (j > 0 && k > 0 && j + 64 < mapWidth && k + 64 < mapHeight) {
				for (int l = 0; l < 64; l++) {
					final int ai1[] = anIntArrayArray118[l + j];
					final byte abyte2[] = aByteArrayArray119[l + j];
					int j1 = mapHeight - k - 1;
					for (int k1 = -64; k1 < 0; k1++) {
						final byte byte0 = abyte0[i++];
						if (byte0 != 0) {
							abyte2[j1] = abyte0[i++];
							int l1 = 0;
							if (byte0 > 0) {
								l1 = anIntArray116[byte0];
							}
							ai1[j1--] = l1;
						}
						else {
							ai1[j1--] = 0;
						}
					}

				}
			}
			else {
				for (int i1 = -4096; i1 < 0; i1++) {
					final byte byte1 = abyte0[i++];
					if (byte1 != 0) {
						i++;
					}
				}
			}
		}

	}

	/**
	 * Has something to do tile color
	 * @param abyte0
	 * @param ai
	 */
	public void method16(final byte abyte0[][]) {
		final int ai1[] = new int[mapHeight];
		for (int l = 5; l < mapWidth - 5; l++) {
			final byte abyte1[] = abyte0[l + 5];
			final byte abyte2[] = abyte0[l - 5];
			for (int i1 = 0; i1 < mapHeight; i1++) {
				ai1[i1] += anIntArray115[abyte1[i1] & 0xff] - anIntArray115[abyte2[i1] & 0xff];
			}

			if (l > 10 && l < mapWidth - 10) {
				int j1 = 0;
				int k1 = 0;
				int l1 = 0;
				final int ai2[] = anIntArrayArray117[l];
				for (int i2 = 5; i2 < mapHeight - 5; i2++) {
					final int j2 = ai1[i2 - 5];
					final int k2 = ai1[i2 + 5];
					j1 += (k2 >> 20) - (j2 >> 20);
					k1 += (k2 >> 10 & 0x3ff) - (j2 >> 10 & 0x3ff);
					l1 += (k2 & 0x3ff) - (j2 & 0x3ff);
					if (l1 > 0) {
						ai2[i2] = method17(j1 / 8533D, k1 / 8533D, l1 / 8533D);
					}
				}
			}
		}
	}

	/**
	 * Gets hex value for color
	 * @param d
	 * @param d1
	 * @param d2
	 * @return
	 */
	public int method17(final double d, final double d1, final double d2) {
		double d3 = d2;
		double d4 = d2;
		double d5 = d2;
		if (d1 != 0.0D) {
			double d6;
			if (d2 < 0.5D) {
				d6 = d2 * (1.0D + d1);
			}
			else {
				d6 = d2 + d1 - d2 * d1;
			}
			final double d7 = 2D * d2 - d6;
			double d8 = d + 0.33333333333333331D;
			if (d8 > 1.0D) {
				d8--;
			}
			final double d9 = d;
			double d10 = d - 0.33333333333333331D;
			if (d10 < 0.0D) {
				d10++;
			}
			if (6D * d8 < 1.0D) {
				d3 = d7 + (d6 - d7) * 6D * d8;
			}
			else if (2D * d8 < 1.0D) {
				d3 = d6;
			}
			else if (3D * d8 < 2D) {
				d3 = d7 + (d6 - d7) * (0.66666666666666663D - d8) * 6D;
			}
			else {
				d3 = d7;
			}
			if (6D * d9 < 1.0D) {
				d4 = d7 + (d6 - d7) * 6D * d9;
			}
			else if (2D * d9 < 1.0D) {
				d4 = d6;
			}
			else if (3D * d9 < 2D) {
				d4 = d7 + (d6 - d7) * (0.66666666666666663D - d9) * 6D;
			}
			else {
				d4 = d7;
			}
			if (6D * d10 < 1.0D) {
				d5 = d7 + (d6 - d7) * 6D * d10;
			}
			else if (2D * d10 < 1.0D) {
				d5 = d6;
			}
			else if (3D * d10 < 2D) {
				d5 = d7 + (d6 - d7) * (0.66666666666666663D - d10) * 6D;
			}
			else {
				d5 = d7;
			}
		}
		final int red = (int) (d3 * 256D);
		final int green = (int) (d4 * 256D);
		final int blue = (int) (d5 * 256D);
		final int hexValue = (red << 16) + (green << 8) + blue;
		return hexValue;
	}

	@Override
	public void draw() {

		final int i = overviewCenterX - (int) (super.getWidth() / currentZoomLevel);
		final int j = overviewCenterY - (int) (super.getHeight() / currentZoomLevel);
		final int k = overviewCenterX + (int) (super.getWidth() / currentZoomLevel);
		final int l = overviewCenterY + (int) (super.getHeight() / currentZoomLevel);

		renderMap(pixels, new Dimension(super.getWidth(), super.getHeight()), i, j, k, l);

		if (showOverview) {
			overviewArea.validateAndDrawArea(minimapX, minimapY);
			fillRect(minimapX + minimapWidth * i / mapWidth, minimapY + minimapHeight * j / mapHeight, (k - i) * minimapWidth / mapWidth, (l - j) * minimapHeight / mapHeight, 0xACA9FC, 100);
			drawRect(minimapX + minimapWidth * i / mapWidth, minimapY + minimapHeight * j / mapHeight, (k - i) * minimapWidth / mapWidth, (l - j) * minimapHeight / mapHeight, 0xACA9FC);
		}

		synchronized (RSMap.this) {
			for (final MapButton button : buttons) {
				button.draw();
			}
		}
	}

	/**
	 * Renders the minimap
	 */
	public void renderMiniMap(final int[] pix, final Dimension size, final int x1, final int y1, final int x2, final int y2) {
		final int localMapWidth = x2 - x1;
		final int localMapHeight = y2 - y1;

		final int k2 = (size.width << 16) / localMapWidth;
		final int l2 = (size.height << 16) / localMapHeight;

		// Draws textures
		for (int i3 = 0; i3 < localMapWidth; i3++) {
			final int j3 = k2 * i3 >> 16;
			final int l3 = k2 * (i3 + 1) >> 16;
			final int j4 = l3 - j3;
			if (j4 > 0) {
				final int ai[] = anIntArrayArray117[i3 + x1];
				final int ai1[] = anIntArrayArray118[i3 + x1];
				final byte abyte0[] = aByteArrayArray119[i3 + x1];
				for (int j7 = 0; j7 < localMapHeight; j7++) {
					final int i8 = l2 * j7 >> 16;
					final int l8 = l2 * (j7 + 1) >> 16;
					final int l9 = l8 - i8;
					if (l9 > 0) {
						final int l10 = ai1[j7 + y1];
						if (l10 == 0) {
							fillColor(pix, size, j3, i8, l3 - j3, l8 - i8, ai[j7 + y1]);
						}
						else {
							final byte byte0 = abyte0[j7 + y1];
							final int l11 = byte0 & 0xfc;
							if (l11 == 0 || j4 <= 1 || l9 <= 1) {
								fillColor(pix, size, j3, i8, j4, l9, l10);
							}
							else {
								blendCorners(pix, size, i8 * size.width + j3, ai[j7 + y1], l10, j4, l9, l11 >> 2, byte0 & 3);
							}
						}
					}
				}
			}
		}
	}

	public void renderMap(final int[] pix, final Dimension size, final int x1, final int y1, final int x2, final int y2) {
		final int localMapWidth = x2 - x1;
		final int localMapHeight = y2 - y1;

		final int k2 = (size.width << 16) / localMapWidth;
		final int l2 = (size.height << 16) / localMapHeight;

		// Draws textures
		for (int i3 = 0; i3 < localMapWidth; i3++) {
			final int j3 = k2 * i3 >> 16;
			final int l3 = k2 * (i3 + 1) >> 16;
			final int j4 = l3 - j3;
			if (j4 > 0) {
				final int ai[] = anIntArrayArray117[i3 + x1];
				final int ai1[] = anIntArrayArray118[i3 + x1];
				final byte abyte0[] = aByteArrayArray119[i3 + x1];
				for (int j7 = 0; j7 < localMapHeight; j7++) {
					final int i8 = l2 * j7 >> 16;
					final int l8 = l2 * (j7 + 1) >> 16;
					final int l9 = l8 - i8;
					if (l9 > 0) {
						final int l10 = ai1[j7 + y1];
						if (l10 == 0) {
							fillColor(pix, size, j3, i8, l3 - j3, l8 - i8, ai[j7 + y1]);
						}
						else {
							final byte byte0 = abyte0[j7 + y1];
							final int l11 = byte0 & 0xfc;
							if (l11 == 0 || j4 <= 1 || l9 <= 1) {
								fillColor(pix, size, j3, i8, j4, l9, l10);
							}
							else {
								blendCorners(pix, size, i8 * size.width + j3, ai[j7 + y1], l10, j4, l9, l11 >> 2, byte0 & 3);
							}
						}
					}
				}

			}
		}

		if (x2 - x1 > size.width) {
			return;
		}

		// Draws walls, doors, fences, etc.
		for (int i4 = 0; i4 < localMapWidth; i4++) {
			final int k4 = k2 * i4 >> 16;
			final int i5 = k2 * (i4 + 1) >> 16;
			final int i6 = i5 - k4;
			if (i6 > 0) {
				final byte abyte1[] = mapObjects[i4 + x1];
				for (int i9 = 0; i9 < localMapHeight; i9++) {
					final int i10 = l2 * i9 >> 16;
					final int i11 = l2 * (i9 + 1) >> 16;
					final int k11 = i11 - i10;
					if (k11 > 0) {
						int i12 = abyte1[i9 + y1] & 0xff;
						if (i12 != 0) {
							int k12;
							if (i6 == 1) {
								k12 = k4;
							}
							else {
								k12 = i5 - 1;
							}
							int j13;
							if (k11 == 1) {
								j13 = i10;
							}
							else {
								j13 = i11 - 1;
							}
							int i14 = 0xcccccc;
							if (i12 >= 5 && i12 <= 8 || i12 >= 13 && i12 <= 16 || i12 >= 21 && i12 <= 24 || i12 == 27 || i12 == 28) {
								i14 = 0xcc0000;
								i12 -= 4;
							}
							if (i12 == 1) {
								renderVerticalLine(k4, i10, k11, i14);
							}
							else if (i12 == 2) {
								renderHorizontalLine(k4, i10, i6, i14);
							}
							else if (i12 == 3) {
								renderVerticalLine(k12, i10, k11, i14);
							}
							else if (i12 == 4) {
								renderHorizontalLine(k4, j13, i6, i14);
							}
							else if (i12 == 9) {
								renderVerticalLine(k4, i10, k11, 0xffffff);
								renderHorizontalLine(k4, i10, i6, i14);
							}
							else if (i12 == 10) {
								renderVerticalLine(k12, i10, k11, 0xffffff);
								renderHorizontalLine(k4, i10, i6, i14);
							}
							else if (i12 == 11) {
								renderVerticalLine(k12, i10, k11, 0xffffff);
								renderHorizontalLine(k4, j13, i6, i14);
							}
							else if (i12 == 12) {
								renderVerticalLine(k4, i10, k11, 0xffffff);
								renderHorizontalLine(k4, j13, i6, i14);
							}
							else if (i12 == 17) {
								renderHorizontalLine(k4, i10, 1, i14);
							}
							else if (i12 == 18) {
								renderHorizontalLine(k12, i10, 1, i14);
							}
							else if (i12 == 19) {
								renderHorizontalLine(k12, j13, 1, i14);
							}
							else if (i12 == 20) {
								renderHorizontalLine(k4, j13, 1, i14);
							}
							else if (i12 == 25) {
								for (int j14 = 0; j14 < k11; j14++) {
									renderHorizontalLine(k4 + j14, j13 - j14, 1, i14);
								}
							}
							else if (i12 == 26) {
								for (int k14 = 0; k14 < k11; k14++) {
									renderHorizontalLine(k4 + k14, i10 + k14, 1, i14);
								}
							}
						}
					}
				}
			}
		}

		synchronized (RSCanvas.class) {
			for (final Sprite s : mapSprites) {
				if (s.isRelativeToMap) {
					final Point loc = s.location;
					final int px = loc.x - mapStartX;
					int py = mapStartY + mapHeight - loc.y;
					py -= currentZoomLevel < 8D ? 4 : 2;
					int j6;
					int l6;
					j6 = px;
					l6 = py;
					final int adjustedX = getWidth() * (j6 - x1) / (x2 - x1);
					final int adjustedY = getHeight() * (l6 - y1) / (y2 - y1);
					s.drawSprite(adjustedX - s.getWidth() / 2, adjustedY, this);
				}
				else {
					s.drawSprite(s.location.x, s.location.y, this);
				}
			}
		}
	}

	/**
	 * Translates a map coordinate into a pixel coordinate
	 * @param mapCoord the point to translate
	 * @return the pixel coordinate
	 */
	public Point mapToPixel(final Point mapCoord) {
		final int i = overviewCenterX - (int) (super.getWidth() / currentZoomLevel);
		final int j = overviewCenterY - (int) (super.getHeight() / currentZoomLevel);
		final int k = overviewCenterX + (int) (super.getWidth() / currentZoomLevel);
		final int l = overviewCenterY + (int) (super.getHeight() / currentZoomLevel);
		final int px = mapCoord.x - mapStartX;
		int py = mapStartY + mapHeight - mapCoord.y;
		py -= currentZoomLevel < 8D ? 4 : 2;
		int j6;
		int l6;
		j6 = px;
		l6 = py;
		final int adjustedX = getWidth() * (j6 - i) / (k - i);
		final int adjustedY = getHeight() * (l6 - j) / (l - j);
		return new Point(adjustedX, adjustedY);

	}

	/**
	 * Translates a component coordinate into a map coordinate
	 * @param componentCoord the point to translate
	 * @return the map coordinate
	 */
	public Point componentToMap(final Point componentCoord) {
		final int localX = (int) (componentCoord.x * 2D / currentZoomLevel) - (int) (getWidth() / currentZoomLevel);
		final int localY = (int) (componentCoord.y * 2D / currentZoomLevel) - (int) (getHeight() / currentZoomLevel);
		final int mapCoordX = mapStartX + overviewCenterX + localX;
		final int mapCoordY = mapStartY + mapHeight - localY - overviewCenterY;
		return new Point(mapCoordX, mapCoordY);
	}

	/**
	 * Gets a cache loader for the cache file at the specified path
	 * @param cachePath the path of the cache to get a loader for
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public CacheLoader getMapLoader(final InputStream is) throws FileNotFoundException, IOException {
		byte abyte0[] = null;
		abyte0 = FileOperations.ReadFile(is);
		return new CacheLoader(abyte0);
	}

	@Override
	public boolean clicked(final Point p) {
		synchronized (RSMap.this) {
			for (final MapButton b : buttons) {
				if (b.contains(p)) {
					b.clicked();
					return true;
				}
			}
			return false;
		}
	}

	private final HashSet<MapButton> buttons = new HashSet<MapButton>();

	/**
	 * Adds a button to this map
	 * @param button the button to add
	 */
	public synchronized void addButton(final MapButton button) {
		buttons.add(button);
		repaint();
	}

	/**
	 * Removes a button from this map
	 * @param button the button to remove
	 */
	public synchronized void removeButton(final MapButton button) {
		buttons.remove(button);
		repaint();
	}

	@Override
	public void mouseDragged(final int oldX, final int oldY, final int newX, final int newY) {
		if (newX > minimapX && newY > minimapY && newX < minimapX + minimapWidth && newY < minimapY + minimapHeight && showOverview) {
			overviewCenterX = (newX - minimapX) * mapWidth / minimapWidth;
			overviewCenterY = (newY - minimapY) * mapHeight / minimapHeight;
		}
		else {
			overviewCenterX = dragStartX + (int) ((oldX - newX) * 2D / currentZoomLevel);
			overviewCenterY = dragStartY + (int) ((oldY - newY) * 2D / currentZoomLevel);
		}
		adjustOverview();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (e.getWheelRotation() > 0) {
			if (currentZoomLevel - 0.5D > 1) {
				currentZoomLevel -= 0.5D;
			}
			else {
				currentZoomLevel = 1;
			}
		}
		else {
			currentZoomLevel += 0.5D;
		}
		adjustOverview();
		repaint();
	}

	private void adjustOverview() {
		final int l = overviewCenterX - (int) (super.getWidth() / currentZoomLevel);
		final int l1 = overviewCenterY - (int) (super.getHeight() / currentZoomLevel);
		final int i2 = overviewCenterX + (int) (super.getWidth() / currentZoomLevel);
		final int k2 = overviewCenterY + (int) (super.getHeight() / currentZoomLevel);
		if (l < 48) {
			overviewCenterX = 48 + (int) (super.getWidth() / currentZoomLevel);
		}
		if (l1 < 48) {
			overviewCenterY = 48 + (int) (super.getHeight() / currentZoomLevel);
		}
		if (i2 > mapWidth - 48) {
			overviewCenterX = mapWidth - 48 - (int) (super.getWidth() / currentZoomLevel);
		}
		if (k2 > mapHeight - 48) {
			overviewCenterY = mapHeight - 48 - (int) (super.getHeight() / currentZoomLevel);
		}
	}

	@Override
	public void mouseDown(final int x, final int y) {
		dragStartX = overviewCenterX;
		dragStartY = overviewCenterY;
	}

	@Override
	public void keyPressed(final int keyCode) {
		switch (keyCode) {
			case 38: // UP
				overviewCenterY = (int) (overviewCenterY - 16D / currentZoomLevel);
			break;
			case 40: // DOWN
				overviewCenterY = (int) (overviewCenterY + 16D / currentZoomLevel);
			break;
			case 37: // LEFT
				overviewCenterX = (int) (overviewCenterX - 16D / currentZoomLevel);
			break;
			case 39: // RIGHT
				overviewCenterX = (int) (overviewCenterX + 16D / currentZoomLevel);
			break;
			case 77: // "M"
				showOverview = !showOverview;
			break;
		}
		repaint();
	}

	private Font loadPackagedFont(final String name) {
		try {
			final Font f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/org/whired/rsmap/resources/" + name + ".ttf")).deriveFont(9F);
			Constants.getLogger().log(Level.INFO, "Loaded font: {0}", name);
			return f;
		}
		catch (final Throwable t) {
			final String safeFontName = "SansSerif";
			Constants.getLogger().log(Level.WARNING, "Error loading font: " + name, t);
			final Font f = new Font(safeFontName, Font.PLAIN, 10);
			Constants.getLogger().log(Level.INFO, "Loaded safe font: {0}", safeFontName);
			return f;
		}
	}
}