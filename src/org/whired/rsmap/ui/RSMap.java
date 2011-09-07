package org.whired.rsmap.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import org.whired.rsmap.io.FileOperations;
import org.whired.rsmap.graphics.PaintObserver;
import org.whired.rsmap.graphics.TextRenderer;
import org.whired.rsmap.graphics.OverviewArea;
import org.whired.rsmap.io.CacheLoader;
import org.whired.rsmap.io.ByteBuffer;
import java.awt.Font;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.rsmap.graphics.sprites.Sprite;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.whired.rsmap.graphics.RSCanvas;
import org.whired.rsmap.graphics.sprites.StaticSprite;
import org.whired.rsmap.graphics.sprites.TextSprite;

public class RSMap extends RSCanvas implements PaintObserver
{

	public static void main(String args[])
	{

		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				JFrame jf = new JFrame("Map test");
				jf.setSize(400, 400);
				jf.setVisible(true);
				jf.setResizable(false);
				RSMap map = new RSMap((int) jf.getContentPane().getBounds().getWidth(), (int) jf.getContentPane().getBounds().getHeight());
				System.out.println("Map size: " + map.getWidth() + ", " + map.getHeight());
				jf.getContentPane().add(map);
				jf.pack();
			}
		});


		//map.load();
		//new Thread(map).start();
	}
	public Sprite textSprite;
	private ArrayList<Sprite> mapSprites = new ArrayList<Sprite>();

	public synchronized void addSprite(Sprite s)
	{
		mapSprites.add(s);
	}
//	public void init()
//	{
//		initializeFrame(super.myWidth, super.myHeight);
//	}
	public Sprite uberSprite;

	public final void load()
	{
		CacheLoader cacheLoader;
		try
		{
			cacheLoader = loadWorldMapData();
		}
		catch (FileNotFoundException e)
		{
			//drawLoadingText("Error: Map file not found");
			return;
		}
		catch (IOException e)
		{
			//drawLoadingText("Error: Unable to read map");
			return;
		}
		catch (ClassNotFoundException e)
		{
			//drawLoadingText("Error: Map file not found");
			System.out.println(e.toString());
			return;
		}
		System.out.println("Rendering..");
		//drawLoadingText("Rendering..");

		ByteBuffer byteBuffer = new ByteBuffer(cacheLoader.loadNode("size.dat"));
		mapStartX = byteBuffer.getShort();
		mapStartY = byteBuffer.getShort();
		mapWidth = byteBuffer.getShort();
		mapHeight = byteBuffer.getShort();

		overviewCenterX = 2460 - mapStartX;
		overviewCenterY = (mapStartY + mapHeight) - 3090;
		minimapHeight = 180;
		minimapWidth = (mapWidth * minimapHeight) / mapHeight;
		minimapX = super.getWidth() - minimapWidth - 5;
		minimapY = super.getHeight() - minimapHeight - 5;

		byteBuffer = new ByteBuffer(cacheLoader.loadNode("floorcol.dat"));
		int length = byteBuffer.getShort();
		anIntArray115 = new int[length + 1];
		anIntArray116 = new int[length + 1];
		for (int k = 0; k < length; k++)
		{
			anIntArray115[k + 1] = byteBuffer.getInt();
			anIntArray116[k + 1] = byteBuffer.getInt();
		}

		byte abyte0[] = cacheLoader.loadNode("underlay.dat");
		byte abyte1[][] = new byte[mapWidth][mapHeight];
		method14(abyte0, abyte1);

		byte abyte2[] = cacheLoader.loadNode("overlay.dat");
		anIntArrayArray118 = new int[mapWidth][mapHeight];
		aByteArrayArray119 = new byte[mapWidth][mapHeight];
		method15(abyte2, anIntArrayArray118, aByteArrayArray119);

		byte abyte3[] = cacheLoader.loadNode("loc.dat");
		mapObjects = new byte[mapWidth][mapHeight];
		aByteArrayArray121 = new byte[mapWidth][mapHeight];
		loadObjects(abyte3, mapObjects, new byte[mapWidth][mapHeight], aByteArrayArray121);

		textRenderer = new TextRenderer(this, cacheLoader, "b12_full");

		anIntArrayArray117 = new int[mapWidth][mapHeight];
		method16(abyte1, anIntArrayArray117);
		try
		{
			// TODO IMPLEMENT ELSEWHERE
			uberSprite = new StaticSprite(new URL("http://icons.iconarchive.com/icons/google/chrome/16/Google-Chrome-icon.png"));
			uberSprite.location = new Point(2460, 3090);
			uberSprite.isRelativeToMap = true;
			addSprite(uberSprite);

			textSprite = new TextSprite("Whired", new Font("Helvetica", Font.BOLD, 9), false, true, this);
			textSprite.location = new Point(2460, 3092);
			textSprite.isRelativeToMap = true;
			addSprite(textSprite);
		}
		catch (Exception ex)
		{
			Logger.getLogger(RSMap.class.getName()).log(Level.SEVERE, null, ex);
		}

		overviewArea = new OverviewArea(this, minimapWidth, minimapHeight);

		//renderMiniMap(overviewArea.area, new Dimension(mapWidth, mapHeight), x1, y1, x2, y2);
		renderMiniMap(overviewArea.area, new Dimension(minimapWidth, minimapHeight), 2, 2, mapWidth - 2, mapHeight - 2);
	}

	public void loadObjects(byte[] locations, byte[][] abyte1, byte[][] abyte2, byte[][] abyte3)
	{
		for (int i = 0; i < locations.length;)
		{
			int k = (locations[i++] & 0xff) * 64 - mapStartX;
			int l = (locations[i++] & 0xff) * 64 - mapStartY;
			if (k > 0 && l > 0 && k + 64 < mapWidth && l + 64 < mapHeight)
			{
				for (int i1 = 0; i1 < 64; i1++)
				{
					byte abyte4[] = abyte1[i1 + k];
					byte abyte5[] = abyte2[i1 + k];
					byte abyte6[] = abyte3[i1 + k];
					int l1 = mapHeight - l - 1;
					for (int i2 = -64; i2 < 0; i2++)
					{
						do
						{
							int j = locations[i++] & 0xff;
							if (j == 0)
							{
								break;
							}
							if (j < 29)
							{
								abyte4[l1] = (byte) j;
							}
							else if (j < 160)
							{
								abyte5[l1] = (byte) (j - 28);
							}
							else
							{
								abyte6[l1] = (byte) (j - 159);
							}
						}
						while (true);
						l1--;
					}

				}

			}
			else
			{
				for (int j1 = 0; j1 < 64; j1++)
				{
					for (int k1 = -64; k1 < 0; k1++)
					{
						byte byte0;
						do
						{
							byte0 = locations[i++];
						}
						while (byte0 != 0);
					}

				}

			}
		}
	}

	public void method14(byte abyte0[], byte abyte1[][])
	{
		for (int i = 0; i < abyte0.length;)
		{
			int j = (abyte0[i++] & 0xff) * 64 - mapStartX;
			int k = (abyte0[i++] & 0xff) * 64 - mapStartY;
			if (j > 0 && k > 0 && j + 64 < mapWidth && k + 64 < mapHeight)
			{
				for (int l = 0; l < 64; l++)
				{
					byte abyte2[] = abyte1[l + j];
					int i1 = mapHeight - k - 1;
					for (int j1 = -64; j1 < 0; j1++)
					{
						abyte2[i1--] = abyte0[i++];
					}

				}

			}
			else
			{
				i += 4096;
			}
		}

	}

	public void method15(byte abyte0[], int ai[][], byte abyte1[][])
	{
		for (int i = 0; i < abyte0.length;)
		{
			int j = (abyte0[i++] & 0xff) * 64 - mapStartX;
			int k = (abyte0[i++] & 0xff) * 64 - mapStartY;
			if (j > 0 && k > 0 && j + 64 < mapWidth && k + 64 < mapHeight)
			{
				for (int l = 0; l < 64; l++)
				{
					int ai1[] = ai[l + j];
					byte abyte2[] = abyte1[l + j];
					int j1 = mapHeight - k - 1;
					for (int k1 = -64; k1 < 0; k1++)
					{
						byte byte0 = abyte0[i++];
						if (byte0 != 0)
						{
							abyte2[j1] = abyte0[i++];
							int l1 = 0;
							if (byte0 > 0)
							{
								l1 = anIntArray116[byte0];
							}
							ai1[j1--] = l1;
						}
						else
						{
							ai1[j1--] = 0;
						}
					}

				}

			}
			else
			{
				for (int i1 = -4096; i1 < 0; i1++)
				{
					byte byte1 = abyte0[i++];
					if (byte1 != 0)
					{
						i++;
					}
				}

			}
		}

	}

	/**
	 * Has something to do with coloring tiles/pixels
	 * @param abyte0
	 * @param ai
	 */
	public void method16(byte abyte0[][], int ai[][])
	{
		int i = mapWidth;
		int j = mapHeight;
		int ai1[] = new int[j];
		for (int l = 5; l < i - 5; l++)
		{
			byte abyte1[] = abyte0[l + 5];
			byte abyte2[] = abyte0[l - 5];
			for (int i1 = 0; i1 < j; i1++)
			{
				ai1[i1] += anIntArray115[abyte1[i1] & 0xff] - anIntArray115[abyte2[i1] & 0xff];
			}

			if (l > 10 && l < i - 10)
			{
				int j1 = 0;
				int k1 = 0;
				int l1 = 0;
				int ai2[] = ai[l];
				for (int i2 = 5; i2 < j - 5; i2++)
				{
					int j2 = ai1[i2 - 5];
					int k2 = ai1[i2 + 5];
					j1 += (k2 >> 20) - (j2 >> 20);
					k1 += (k2 >> 10 & 0x3ff) - (j2 >> 10 & 0x3ff);
					l1 += (k2 & 0x3ff) - (j2 & 0x3ff);
					if (l1 > 0)
					{
						ai2[i2] = method17((double) j1 / 8533D, (double) k1 / 8533D, (double) l1 / 8533D);
					}
				}
			}
		}
	}

	/**
	 * Gets the color for an empty tile?
	 * @param d
	 * @param d1
	 * @param d2
	 * @return
	 */
	public int method17(double d, double d1, double d2)
	{
		double d3 = d2;
		double d4 = d2;
		double d5 = d2;
		if (d1 != 0.0D)
		{
			double d6;
			if (d2 < 0.5D)
			{
				d6 = d2 * (1.0D + d1);
			}
			else
			{
				d6 = (d2 + d1) - d2 * d1;
			}
			double d7 = 2D * d2 - d6;
			double d8 = d + 0.33333333333333331D;
			if (d8 > 1.0D)
			{
				d8--;
			}
			double d9 = d;
			double d10 = d - 0.33333333333333331D;
			if (d10 < 0.0D)
			{
				d10++;
			}
			if (6D * d8 < 1.0D)
			{
				d3 = d7 + (d6 - d7) * 6D * d8;
			}
			else if (2D * d8 < 1.0D)
			{
				d3 = d6;
			}
			else if (3D * d8 < 2D)
			{
				d3 = d7 + (d6 - d7) * (0.66666666666666663D - d8) * 6D;
			}
			else
			{
				d3 = d7;
			}
			if (6D * d9 < 1.0D)
			{
				d4 = d7 + (d6 - d7) * 6D * d9;
			}
			else if (2D * d9 < 1.0D)
			{
				d4 = d6;
			}
			else if (3D * d9 < 2D)
			{
				d4 = d7 + (d6 - d7) * (0.66666666666666663D - d9) * 6D;
			}
			else
			{
				d4 = d7;
			}
			if (6D * d10 < 1.0D)
			{
				d5 = d7 + (d6 - d7) * 6D * d10;
			}
			else if (2D * d10 < 1.0D)
			{
				d5 = d6;
			}
			else if (3D * d10 < 2D)
			{
				d5 = d7 + (d6 - d7) * (0.66666666666666663D - d10) * 6D;
			}
			else
			{
				d5 = d7;
			}
		}
		int red = (int) (d3 * 256D); //red?
		int green = (int) (d4 * 256D); // green?
		int blue = (int) (d5 * 256D); // blue?
		int hexValue = (red << 16) + (green << 8) + blue; // total?
		return hexValue;
	}

	public void disposeResources()
	{
		try
		{
			anIntArray115 = null;
			anIntArray116 = null;
			anIntArrayArray117 = null;
			anIntArrayArray118 = null;
			aByteArrayArray119 = null;
			mapObjects = null;
			aByteArrayArray121 = null;
			textRenderer = null;
			overviewArea = null;
			System.gc();
			return;
		}
		catch (Throwable _ex)
		{
			return;
		}
	}

	// TODO remove this shit. it's SO unnecessary.
	// Most of this method is a collection of shit-rigged events
	// that go against the official API and make 0 sense
	public void process()
	{
		if (super.keyArray[1] == 1)
		{
			overviewCenterX = (int) ((double) overviewCenterX - 16D / currentZoomLevel);
			requestRedraw();
		}
		if (super.keyArray[2] == 1)
		{
			overviewCenterX = (int) ((double) overviewCenterX + 16D / currentZoomLevel);
			requestRedraw();
		}
		if (super.keyArray[3] == 1)
		{
			overviewCenterY = (int) ((double) overviewCenterY - 16D / currentZoomLevel);
			requestRedraw();
		}
		if (super.keyArray[4] == 1)
		{
			overviewCenterY = (int) ((double) overviewCenterY + 16D / currentZoomLevel);
			requestRedraw();
		}

		for (int i = 1; i > 0;)
		{
			i = getKeyCode();

			//System.out.println("Key: "+i);
			if (i == 111 || i == 79)
			{
				showOverview = !showOverview;
				requestRedraw();
			}
		}

		if (super.clickMode == 2)
		{
			desiredZoomLevel = 8D;
		}

		// Left mouse down
		if (super.clickMode == 1)
		{
			firstClickX = super.clickX;
			firstClickY = super.clickY;

			// Something to do with current coordinates
			anInt160 = overviewCenterX;
			anInt161 = overviewCenterY;

			requestRedraw();
		}
		if ((super.clickMode2 == 1 || super.clickMode == 1) && showOverview)
		{
			int k = super.clickX;
			int k1 = super.clickY;
			if (super.clickMode2 == 1)
			{
				k = super.xDragged;
				k1 = super.yDragged;
			}
			if (k > minimapX && k1 > minimapY && k < minimapX + minimapWidth && k1 < minimapY + minimapHeight)
			{
				overviewCenterX = ((k - minimapX) * mapWidth) / minimapWidth;
				overviewCenterY = ((k1 - minimapY) * mapHeight) / minimapHeight;
				firstClickX = -1;
				requestRedraw();
			}
		}
		if (super.clickMode2 == 1 && firstClickX != -1)
		{
			//System.out.println("("+(anInt171+anInt111)+", "+((anInt112 + anInt114) - anInt172)+")");
			overviewCenterX = anInt160 + (int) (((double) (firstClickX - super.xDragged) * 2D) / desiredZoomLevel);
			overviewCenterY = anInt161 + (int) (((double) (firstClickY - super.yDragged) * 2D) / desiredZoomLevel);
			requestRedraw();
		}
		if (currentZoomLevel < desiredZoomLevel)
		{
			requestRedraw();
			currentZoomLevel += currentZoomLevel / 30D;
			if (currentZoomLevel > desiredZoomLevel)
			{
				currentZoomLevel = desiredZoomLevel;
			}
		}
		if (currentZoomLevel > desiredZoomLevel)
		{
			requestRedraw();
			currentZoomLevel -= currentZoomLevel / 30D;
			if (currentZoomLevel < desiredZoomLevel)
			{
				currentZoomLevel = desiredZoomLevel;
			}
		}
		if (anInt145 < anInt146)
		{
			requestRedraw();
			anInt145++;
		}
		if (anInt145 > anInt146)
		{
			requestRedraw();
			anInt145--;
		}
		if (anInt151 > 0)
		{
			requestRedraw();
			anInt151--;
		}
		int l = overviewCenterX - (int) ((double) super.getWidth() / currentZoomLevel);
		int l1 = overviewCenterY - (int) ((double) super.getHeight() / currentZoomLevel);
		int i2 = overviewCenterX + (int) ((double) super.getWidth() / currentZoomLevel);
		int k2 = overviewCenterY + (int) ((double) super.getHeight() / currentZoomLevel);
		if (l < 48)
		{
			overviewCenterX = 48 + (int) ((double) super.getWidth() / currentZoomLevel);
		}
		if (l1 < 48)
		{
			overviewCenterY = 48 + (int) ((double) super.getHeight() / currentZoomLevel);
		}
		if (i2 > mapWidth - 48)
		{
			overviewCenterX = mapWidth - 48 - (int) ((double) super.getWidth() / currentZoomLevel);
		}
		if (k2 > mapHeight - 48)
		{
			overviewCenterY = mapHeight - 48 - (int) ((double) super.getHeight() / currentZoomLevel);
		}
	}

	public void draw()
	{
		int i = overviewCenterX - (int) ((double) super.getWidth() / currentZoomLevel);
		int j = overviewCenterY - (int) ((double) super.getHeight() / currentZoomLevel);
		int k = overviewCenterX + (int) ((double) super.getWidth() / currentZoomLevel);
		int l = overviewCenterY + (int) ((double) super.getHeight() / currentZoomLevel);

		int m = overviewCenterX - (int) ((double) overviewArea.areaWidth / currentZoomLevel);
		int n = overviewCenterY - (int) ((double) overviewArea.areaHeight / currentZoomLevel);
		int o = overviewCenterX + (int) ((double) overviewArea.areaWidth / currentZoomLevel);
		int p = overviewCenterY + (int) ((double) overviewArea.areaHeight / currentZoomLevel);

		renderMap(pixels, new Dimension(super.getWidth(), super.getHeight()), i, j, k, l);

		if (showOverview)
		{
			// This won't work with both.
			//renderMap(overviewArea.area, new Dimension(overviewArea.areaWidth, overviewArea.areaHeight), m, n, o, p);
			overviewArea.validateAndDrawArea(minimapX, minimapY);

			fillRect(minimapX + (minimapWidth * i) / mapWidth, minimapY + (minimapHeight * j) / mapHeight, ((k - i) * minimapWidth) / mapWidth, ((l - j) * minimapHeight) / mapHeight, 0xACA9FC, 100);
			drawRect(minimapX + (minimapWidth * i) / mapWidth, minimapY + (minimapHeight * j) / mapHeight, ((k - i) * minimapWidth) / mapWidth, ((l - j) * minimapHeight) / mapHeight, 0xACA9FC);
		}

		synchronized (RSMap.this)
		{
			for (MapButton button : buttons)
			{
				button.draw();
			}
		}
		// Should be button.draw or something
		//drawButton(5, super.getHeight() - 18 - 5, 100, 18, 0xBEC7E8, 0xA3ACD1, "Minimap", textRenderer);
	}

	/**
	 * Renders the minimap
	 */
	public void renderMiniMap(int[] pix, Dimension size, int x1, int y1, int x2, int y2)
	{
		int localMapWidth = x2 - x1;
		int localMapHeight = y2 - y1;

		int k2 = (size.width << 16) / localMapWidth;
		int l2 = (size.height << 16) / localMapHeight;

		// Draws textures
		for (int i3 = 0; i3 < localMapWidth; i3++)
		{
			int j3 = k2 * i3 >> 16;
			int l3 = k2 * (i3 + 1) >> 16;
			int j4 = l3 - j3;
			if (j4 > 0)
			{
				int ai[] = anIntArrayArray117[i3 + x1];
				int ai1[] = anIntArrayArray118[i3 + x1];
				byte abyte0[] = aByteArrayArray119[i3 + x1];
				for (int j7 = 0; j7 < localMapHeight; j7++)
				{
					int i8 = l2 * j7 >> 16;
					int l8 = l2 * (j7 + 1) >> 16;
					int l9 = l8 - i8;
					if (l9 > 0)
					{
						int l10 = ai1[j7 + y1];
						if (l10 == 0)
						{
							fillColor(pix, size, j3, i8, l3 - j3, l8 - i8, ai[j7 + y1]);
						}
						else
						{
							byte byte0 = abyte0[j7 + y1];
							int l11 = byte0 & 0xfc;
							if (l11 == 0 || j4 <= 1 || l9 <= 1)
							{
								fillColor(pix, size, j3, i8, j4, l9, l10);
							}
							else
							{
								method20(pix, size, i8 * size.width + j3, ai[j7 + y1], l10, j4, l9, l11 >> 2, byte0 & 3);
							}
						}
					}
				}
			}
		}
	}

	public void renderMap(int[] pix, Dimension size, int x1, int y1, int x2, int y2)
	{
		int localMapWidth = x2 - x1;
		int localMapHeight = y2 - y1;


		int k2 = (size.width << 16) / localMapWidth;
		int l2 = (size.height << 16) / localMapHeight;

		// Draws textures
		for (int i3 = 0; i3 < localMapWidth; i3++)
		{
			int j3 = k2 * i3 >> 16;
			int l3 = k2 * (i3 + 1) >> 16;
			int j4 = l3 - j3;
			if (j4 > 0)
			{
				int ai[] = anIntArrayArray117[i3 + x1];
				int ai1[] = anIntArrayArray118[i3 + x1];
				byte abyte0[] = aByteArrayArray119[i3 + x1];
				for (int j7 = 0; j7 < localMapHeight; j7++)
				{
					int i8 = l2 * j7 >> 16;
					int l8 = l2 * (j7 + 1) >> 16;
					int l9 = l8 - i8;
					if (l9 > 0)
					{
						int l10 = ai1[j7 + y1];
						if (l10 == 0)
						{
							fillColor(pix, size, j3, i8, l3 - j3, l8 - i8, ai[j7 + y1]);
						}
						else
						{
							byte byte0 = abyte0[j7 + y1];
							int l11 = byte0 & 0xfc;
							if (l11 == 0 || j4 <= 1 || l9 <= 1)
							{
								fillColor(pix, size, j3, i8, j4, l9, l10);
							}
							else
							{
								method20(pix, size, i8 * size.width + j3, ai[j7 + y1], l10, j4, l9, l11 >> 2, byte0 & 3);
							}
						}
					}
				}

			}
		}

		if (x2 - x1 > size.width)
		{
			return;
		}

		// Draws walls, doors, fences, etc.
		for (int i4 = 0; i4 < localMapWidth; i4++)
		{
			int k4 = k2 * i4 >> 16;
			int i5 = k2 * (i4 + 1) >> 16;
			int i6 = i5 - k4;
			if (i6 > 0)
			{
				byte abyte1[] = mapObjects[i4 + x1];
				for (int i9 = 0; i9 < localMapHeight; i9++)
				{
					int i10 = l2 * i9 >> 16;
					int i11 = l2 * (i9 + 1) >> 16;
					int k11 = i11 - i10;
					if (k11 > 0)
					{
						int i12 = abyte1[i9 + y1] & 0xff;
						if (i12 != 0)
						{
							int k12;
							if (i6 == 1)
							{
								k12 = k4;
							}
							else
							{
								k12 = i5 - 1;
							}
							int j13;
							if (k11 == 1)
							{
								j13 = i10;
							}
							else
							{
								j13 = i11 - 1;
							}
							int i14 = 0xcccccc;
							if (i12 >= 5 && i12 <= 8 || i12 >= 13 && i12 <= 16 || i12 >= 21 && i12 <= 24 || i12 == 27 || i12 == 28)
							{
								i14 = 0xcc0000;
								i12 -= 4;
							}
							if (i12 == 1)
							{
								drawVerticalLine(k4, i10, k11, i14);
							}
							else if (i12 == 2)
							{
								drawHorizontalLine(k4, i10, i6, i14);
							}
							else if (i12 == 3)
							{
								drawVerticalLine(k12, i10, k11, i14);
							}
							else if (i12 == 4)
							{
								drawHorizontalLine(k4, j13, i6, i14);
							}
							else if (i12 == 9)
							{
								drawVerticalLine(k4, i10, k11, 0xffffff);
								drawHorizontalLine(k4, i10, i6, i14);
							}
							else if (i12 == 10)
							{
								drawVerticalLine(k12, i10, k11, 0xffffff);
								drawHorizontalLine(k4, i10, i6, i14);
							}
							else if (i12 == 11)
							{
								drawVerticalLine(k12, i10, k11, 0xffffff);
								drawHorizontalLine(k4, j13, i6, i14);
							}
							else if (i12 == 12)
							{
								drawVerticalLine(k4, i10, k11, 0xffffff);
								drawHorizontalLine(k4, j13, i6, i14);
							}
							else if (i12 == 17)
							{
								drawHorizontalLine(k4, i10, 1, i14);
							}
							else if (i12 == 18)
							{
								drawHorizontalLine(k12, i10, 1, i14);
							}
							else if (i12 == 19)
							{
								drawHorizontalLine(k12, j13, 1, i14);
							}
							else if (i12 == 20)
							{
								drawHorizontalLine(k4, j13, 1, i14);
							}
							else if (i12 == 25)
							{
								for (int j14 = 0; j14 < k11; j14++)
								{
									drawHorizontalLine(k4 + j14, j13 - j14, 1, i14);
								}

							}
							else if (i12 == 26)
							{
								for (int k14 = 0; k14 < k11; k14++)
								{
									drawHorizontalLine(k4 + k14, i10 + k14, 1, i14);
								}
							}
						}
					}
				}
			}
		}

		// TODO fortify synchronicity
		synchronized (RSCanvas.class)
		{
			for (Sprite s : mapSprites)
			{
				if (s.isRelativeToMap)
				{
					Point loc = s.location;
					int px = (loc.x - mapStartX);
					int py = ((mapStartY + mapHeight) - loc.y);
					py -= (desiredZoomLevel < 8D ? 4 : 2);
					int j6;
					int l6;
					j6 = px;
					l6 = py;
					int adjustedX = ((getWidth()) * (j6 - x1)) / (x2 - x1);
					int adjustedY = ((getHeight()) * (l6 - y1)) / (y2 - y1);
					s.drawSprite(adjustedX - s.getWidth() / 2, adjustedY, this);
				}
				else
				{
					s.drawSprite(s.location.x, s.location.y, this);
				}
			}
		}

//		 TODO IMPLEMENT ELSEWHERE -- OR USE DEPENDENCY INJECTION?
//		synchronized(DrawingArea.class)
//		{
//			for (MapPlayer p : players.values())
//			{
//				Point loc = p.getLocation();
//				int px = (loc.x - m.mapStartX);
//				int py = ((m.mapStartY + m.mapHeight) - loc.y);
//				py -= (m.desiredZoomLevel < 8D ? 4 : 2);
//				int j6;
//				int l6;
//				j6 = px;
//				l6 = py;
//				int adjustedX = ((k1) * (j6 - x1)) / (x2 - x1);
//				int adjustedY = ((l1) * (l6 - y1)) / (y2 - y1);
//
//				if (m.textSprite != null)
//				{
//					m.textSprite.parseAndRenderString(p.getName(), adjustedX + m.uberSprite.myWidth/2 + 1, adjustedY + 2 + m.uberSprite.myHeight/2, 0xC0CFEB, false, true);
//				}
//
//				if(curStep == 0) curStep = adjustedY;
//				if(destY == 0) destY = adjustedY + 20;
//				if(curStep < destY && System.currentTimeMillis()-lastStep > STEP_INCREMENT_MS)
//				{
//					curStep++;
//					lastStep = System.currentTimeMillis();
//					m.requestRedraw();
//					final RSMap mm = m;
//					new Thread(new Runnable()
//					{
//						public void run()
//						{
//							try
//							{
//								Thread.sleep(STEP_INCREMENT_MS + 10);
//								System.out.println("end sleep");
//								mm.requestRedraw();
//							}
//							catch (InterruptedException ex)
//							{
//								Logger.getLogger(DrawingArea.class.getName()).log(Level.SEVERE, null, ex);
//							}
//						}
//
//					}).start();
//				}
//				m.uberSprite.drawSprite(adjustedX - m.uberSprite.myWidth/2, curStep);
//				if(curStep == destY) curStep = 0;
//			}
//		}
	}

	public CacheLoader loadWorldMapData() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		byte abyte0[] = null;
		System.out.print("RES: ");
		System.out.println(this.getClass().getResource("/org/whired/rsmap/io/worldmap.dat").getPath());
		abyte0 = FileOperations.ReadFile(this.getClass().getResource("/org/whired/rsmap/io/worldmap.dat").getPath());
		return new CacheLoader(abyte0);
	}

	public RSMap(int width, int height)
	{
		super(width, height);
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				synchronized (RSMap.this)
				{
					buttons.add(new MapButton("Minimap", 5, RSMap.super.getHeight() - 18 - 5, 100, 18, 0xBEC7E8, 0xFF00FF)//0xBEC7E8, 0xA3ACD1)
					{

						@Override
						public void draw()
						{
							drawButton(this);
						}

						@Override
						public void clicked()
						{
							showOverview = !showOverview;
							requestRedraw();
						}
					});
					buttons.add(new MapButton("Test", 110, RSMap.super.getHeight() - 20 - 5, 100, 20, Color.MAGENTA, Color.PINK)
					{
						@Override
						public void draw()
						{
							drawButton(this);
						}

						@Override
						public void clicked()
						{
							System.out.println("Test clicked");
						}
					});
				}

				//drawButton(5, super.getHeight() - 18 - 5, 100, 18, , "Minimap", textRenderer);
				showOverview = true;

				currentZoomLevel = 3D;
				desiredZoomLevel = 3D;
				load();
				new Thread(RSMap.this).start();
			}
		});

	}

	@Override
	public void clicked(Point p)
	{
		System.out.println("Click received at "+p);
		synchronized(RSMap.this)
		{
			for(MapButton b : buttons)
			{
				if(b.contains(p))
				{
					b.clicked();
				}
			}
		}
	}

	private ArrayList<MapButton> buttons = new ArrayList<MapButton>();
	public int mapStartX;
	public int mapStartY;
	public int mapWidth;
	public int mapHeight;
	public int anIntArray115[];
	public int anIntArray116[];
	public int anIntArrayArray117[][];
	public int anIntArrayArray118[][];
	public byte aByteArrayArray119[][];
	/** Contains locations of fences, doors, walls, etc. */
	public byte mapObjects[][];
	public byte aByteArrayArray121[][];
	public int anInt141;
	public int anInt142;
	public int anInt143;
	public int anInt144;
	public int anInt145;
	public int anInt146;
	public boolean aBoolean147;
	public int anInt148;
	public int anInt149;
	public int anInt150;
	public int anInt151;
	public int minimapHeight;
	public int minimapWidth;
	public int minimapX;
	public int minimapY;
	public boolean showOverview;
	public OverviewArea overviewArea;
	public int firstClickX;
	public int firstClickY;
	public int anInt160;
	public int anInt161;
	public int anInt163;
	public double currentZoomLevel;
	public double desiredZoomLevel;
	public int overviewCenterX;
	public int overviewCenterY;

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		System.out.println(this.desiredZoomLevel);
		if (e.getWheelRotation() > 0)
		{
			if (this.desiredZoomLevel - 0.1D > 1)
			{
				this.desiredZoomLevel -= 0.1D;
			}
			else
			{
				this.desiredZoomLevel = 1;
			}
		}
		else
		{
			this.desiredZoomLevel += 0.1D;
		}
	}
}
