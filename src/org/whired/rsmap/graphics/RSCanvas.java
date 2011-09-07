package org.whired.rsmap.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import org.whired.rsmap.ui.MapButton;

public abstract class RSCanvas extends Component implements Runnable, MouseWheelListener, ImageProducer, ImageObserver
{

	public int anInt1;
	public int delayTime;
	public int minDelay;
	public long aLongArray4[];
	public int framesPerSecond;
	public boolean shouldDebug;
	public boolean shouldClearScreen;
	public boolean awtFocus;
	public int idleTime;
	public int clickMode2;
	public int xDragged;
	public int yDragged;
	public int clickMode1;
	public int xPressed;
	public int yPressed;
	public long clickTime;
	public int clickMode;
	public int clickX;
	public int clickY;
	public long aLong26;
	public int keyArray[];
	public int charQueue[];
	public int readIndex;
	public int writeIndex;
	public boolean aBoolean31;
	private int cyclesUntilPaint = 0;
	private boolean redrawRequired = false;
	public ColorModel colorModel;
	public ImageConsumer imageConsumer;
	public Image image;
	public TextRenderer textRenderer;

	public RSCanvas(final int width, final int height)
	{

		setPreferredSize(new Dimension(width, height));
		setSize(width, height);
		setFocusable(true);
		System.out.println("Initial size: " + width + ", " + height + " - FINAL SIZE--" + getSize());
		pixels = new int[getWidth() * getHeight()];
		colorModel = new DirectColorModel(32, 0xff0000, 0x00ff00, 0x0000ff);
		image = createImage(RSCanvas.this);
		prepareImage(image, RSCanvas.this);

		if (imageConsumer != null)
		{
			imageConsumer.setPixels(0, 0, getWidth(), getHeight(), colorModel, pixels, 0, getWidth());
			imageConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}

		setDrawingArea(0, 0, getWidth(), getHeight());


		anInt1 = 0;
		delayTime = 20;
		minDelay = 1;
		aLongArray4 = new long[10];
		framesPerSecond = 0;
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
		idleTime = 0;
		clickMode2 = 0;
		xDragged = 0;
		yDragged = 0;
		clickMode1 = 0;
		xPressed = 0;
		yPressed = 0;
		clickTime = 0L;
		clickMode = 0;
		clickX = 0;
		clickY = 0;
		aLong26 = 0L;
		keyArray = new int[128];
		charQueue = new int[128];
		readIndex = 0;
		writeIndex = 0;
		graphics = getGraphics();

		addMouseListener(new MouseAdapter()
		{

			public void mousePressed(MouseEvent mouseevent)
			{
				requestFocusInWindow();
				int x = mouseevent.getX();
				int y = mouseevent.getY();

				idleTime = 0;
				xPressed = x;
				yPressed = y;
				clickTime = System.currentTimeMillis();
				if (mouseevent.isMetaDown())
				{
					clickMode1 = 2;
					clickMode2 = 2;
				}
				else
				{
					clickMode1 = 1;
					clickMode2 = 1;
				}
			}

			public void mouseReleased(MouseEvent mouseevent)
			{
				clicked(mouseevent.getPoint());
				idleTime = 0;
				clickMode2 = 0;
			}

			public void mouseExited(MouseEvent mouseevent)
			{
				idleTime = 0;
				xDragged = -1;
				yDragged = -1;
			}
		});


		addMouseWheelListener(RSCanvas.this);

		addMouseMotionListener(new MouseAdapter()
		{

			public void mouseMoved(MouseEvent mouseevent)
			{
				int i = mouseevent.getX();
				int j = mouseevent.getY();
				idleTime = 0;
				xDragged = i;
				yDragged = j;
			}

			public void mouseDragged(MouseEvent mouseevent)
			{
				int x = mouseevent.getX();
				int y = mouseevent.getY();
				idleTime = 0;
				xDragged = x;
				yDragged = y;
			}
		});

		addKeyListener(new KeyAdapter()
		{

			public void keyPressed(KeyEvent keyevent)
			{
				System.out.println("KeyPress");
				idleTime = 0;
				int i = keyevent.getKeyCode();
				int j = keyevent.getKeyChar();
				if (j < 30)
				{
					j = 0;
				}
				if (i == 37)
				{
					j = 1;
				}
				if (i == 39)
				{
					j = 2;
				}
				if (i == 38)
				{
					j = 3;
				}
				if (i == 40)
				{
					j = 4;
				}
				if (i == 17)
				{
					j = 5;
				}
				if (i == 8)
				{
					j = 8;
				}
				if (i == 127)
				{
					j = 8;
				}
				if (i == 9)
				{
					j = 9;
				}
				if (i == 10)
				{
					j = 10;
				}
				if (i >= 112 && i <= 123)
				{
					j = (1008 + i) - 112;
				}
				if (i == 36)
				{
					j = 1000;
				}
				if (i == 35)
				{
					j = 1001;
				}
				if (i == 33)
				{
					j = 1002;
				}
				if (i == 34)
				{
					j = 1003;
				}
				if (j > 0 && j < 128)
				{
					keyArray[j] = 1;
				}
				if (j > 4)
				{
					charQueue[writeIndex] = j;
					writeIndex = writeIndex + 1 & 0x7f;
				}
			}

			public void keyReleased(KeyEvent keyevent)
			{
				idleTime = 0;
				int i = keyevent.getKeyCode();
				char c = keyevent.getKeyChar();
				if (c < '\036')
				{
					c = '\0';
				}
				if (i == 37)
				{
					c = '\001';
				}
				if (i == 39)
				{
					c = '\002';
				}
				if (i == 38)
				{
					c = '\003';
				}
				if (i == 40)
				{
					c = '\004';
				}
				if (i == 17)
				{
					c = '\005';
				}
				if (i == 8)
				{
					c = '\b';
				}
				if (i == 127)
				{
					c = '\b';
				}
				if (i == 9)
				{
					c = '\t';
				}
				if (i == 10)
				{
					c = '\n';
				}
				if (c > 0 && c < '\200')
				{
					keyArray[c] = 0;
				}
			}
		});

		addFocusListener(new FocusAdapter()
		{

			public void focusLost(FocusEvent focusevent)
			{
				awtFocus = false;
				for (int i = 0; i < 128; i++)
				{
					keyArray[i] = 0;
				}

			}

			public void focusGained(FocusEvent focusevent)
			{
				awtFocus = true;
				shouldClearScreen = true;
				cyclesUntilPaint = 0;
			}
		});
		System.out.println("RSCanvas constructed.");
	}

	/**
	 * Invoked when this canvas has been clicked
	 * @param p the point at which the canvas was clicked
	 */
	public abstract void clicked(Point p);

	public int getKeyCode()
	{
		int i = -1;
		if (writeIndex != readIndex)
		{
			i = charQueue[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return i;
	}

	public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
	{
		return true;
	}

	public void requestTopDownLeftRightResend(ImageConsumer imageconsumer)
	{
		System.out.println("TDLR");
	}

	public synchronized boolean isConsumer(ImageConsumer imageconsumer)
	{
		return imageConsumer == imageconsumer;
	}

	public synchronized void removeConsumer(ImageConsumer imageconsumer)
	{
		if (imageConsumer == imageconsumer)
		{
			imageConsumer = null;
		}
	}

	public void drawGraphics(Graphics g, int i, int j)
	{
		if (imageConsumer != null)
		{
			imageConsumer.setPixels(0, 0, getWidth(), getHeight(), colorModel, pixels, 0, getWidth());
			imageConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
		}
		g.drawImage(image, i, j, this);
	}

	public void startProduction(ImageConsumer imageconsumer)
	{
		addConsumer(imageconsumer);
	}

	public synchronized void addConsumer(ImageConsumer imageconsumer)
	{
		imageConsumer = imageconsumer;
		imageconsumer.setDimensions(getWidth(), getHeight());
		imageconsumer.setProperties(null);
		imageconsumer.setColorModel(colorModel);
		imageconsumer.setHints(14);
	}

	/**
	 * Draws a horizontal line
	 * @param lineX the starting x-coordinate of the line
	 * @param lineY the starting y-coordinate of the line
	 * @param lineLength the length of the line
	 * @param hexRGB the color of the line
	 */
	public void drawHorizontalLine(int lineX, int lineY, int lineLength, int hexRGB)
	{
		if (lineY < topY || lineY >= endY)
		{
			return;
		}
		if (lineX < startX)
		{
			lineLength -= startX - lineX;
			lineX = startX;
		}
		if (lineX + lineLength > endX)
		{
			lineLength = endX - lineX;
		}
		int i1 = lineX + lineY * getWidth();
		for (int j1 = 0; j1 < lineLength; j1++)
		{
			pixels[i1 + j1] = hexRGB;
		}

	}

	/**
	 * Draws a vertical line
	 * @param lineX the starting x-coordinate of the line
	 * @param lineY the starting y-coordinate of the line
	 * @param lineLength the length of the line
	 * @param hexRGB the color of the line
	 */
	public void drawVerticalLine(int lineX, int lineY, int lineWidth, int hexRGB)
	{
		if (lineX < startX || lineX >= endX)
		{
			return;
		}
		if (lineY < topY)
		{
			lineWidth -= topY - lineY;
			lineY = topY;
		}
		if (lineY + lineWidth > endY)
		{
			lineWidth = endY - lineY;
		}
		int i1 = lineX + lineY * getWidth();
		for (int j1 = 0; j1 < lineWidth; j1++)
		{
			pixels[i1 + j1 * getWidth()] = hexRGB;
		}
	}

	private final void setDrawingArea(int i, int j, int k, int l)
	{
		if (i < 0)
		{
			i = 0;
		}
		if (j < 0)
		{
			j = 0;
		}
		if (k > getWidth())
		{
			k = getWidth();
		}
		if (l > getHeight())
		{
			l = getHeight();
		}
		startX = i;
		topY = j;
		endX = k;
		endY = l;
		centerX = endX - 1;
		centerY = endX / 2;
		anInt184 = endY / 2;
	}

	// TODO make signature drawButton(MapButton button)
	/**
	 * Draws a button
	 * @param buttonX the x-coordinate of the button
	 * @param buttonY the y-coordinate of the button
	 * @param buttonWidth the width of the button
	 * @param buttonHeight the height of the button
	 * @param buttonHexRGB the color of the button
	 * @param borderHexRGB the color of the border
	 * @param text the text to display
	 * @param textRenderer the renderer to use
	 */
	public void drawButton(int buttonX, int buttonY, int buttonWidth, int buttonHeight, int buttonHexRGB, int borderHexRGB, String text, TextRenderer textRenderer)
	{
		int i1 = borderHexRGB;
		drawRect(buttonX, buttonY, buttonWidth, buttonHeight, 0);
		buttonX++;
		buttonY++;
		buttonWidth -= 2;
		buttonHeight -= 2;
		fillColor(pixels, new Dimension(getWidth(), getHeight()), buttonX, buttonY, buttonWidth, buttonHeight, buttonHexRGB);
		drawHorizontalLine(buttonX, buttonY, buttonWidth, i1);
		drawVerticalLine(buttonX, buttonY, buttonHeight, i1);
		drawHorizontalLine(buttonX, (buttonY + buttonHeight) - 1, buttonWidth, borderHexRGB);
		drawVerticalLine((buttonX + buttonWidth) - 1, buttonY, buttonHeight, borderHexRGB);
		textRenderer.renderText(text, buttonX + buttonWidth / 2 + 1, buttonY + buttonHeight / 2 + 1 + 4, 0);
		textRenderer.renderText(text, buttonX + buttonWidth / 2, buttonY + buttonHeight / 2 + 4, 0xffffff);
	}

	// Time to improve!
	// TODO give Button a foreground (text) color
	public void drawButton(MapButton button)
	{
		fillColor(pixels, new Dimension(getWidth(), getHeight()), button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getBackgroundColor());
		drawRect(button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getBorderColor());
		drawRect(button.getX()+1, button.getY()+1, button.getWidth()-2, button.getHeight()-2, button.getBorderColor());
		textRenderer.renderText(button.getText(), button.getX() + button.getWidth() / 2 + 1, button.getY() + button.getHeight() / 2 + 1 + 4, 0);
		textRenderer.renderText(button.getText(), button.getX() + button.getWidth() / 2, button.getY() + button.getHeight() / 2 + 4, 0xffffff);
	}

	/**
	 * Invoked when the component is painted
	 */
	public abstract void draw();

	/**
	 * Invoked after a set amount of time
	 */
	public abstract void process();
	/** Used for explicit drawing */
	private Graphics graphics;

	public void update(Graphics g)
	{
		if (graphics == null)
		{
			graphics = g;
		}
		try
		{
			draw();
		}
		catch (NullPointerException e)
		{
			System.out.println("Draw error: " + e.toString());
		}
		shouldClearScreen = true;
		cyclesUntilPaint = 0;
	}

	/**
	 * Draws the component
	 * @deprecated do not call explicitly--use #draw() instead
	 * @param g
	 */
	public final void paint(Graphics g)
	{
		if (graphics == null)
		{
			graphics = g;
		}
		try
		{
			draw();
		}
		catch (NullPointerException e)
		{
			System.out.println("Draw error: " + e.toString());
		}
		shouldClearScreen = true;
		cyclesUntilPaint = 0;
	}

	public final void run()
	{
//		if (rsFrame != null)
//		{
//			rsFrame.addWindowListener(this);
//		}
		drawLoadingText("Initializing..");
		int i = 0;
		int j = 256;
		int k = 1;
		int i1 = 0;
		int j1 = 0;
		for (int k1 = 0; k1 < 10; k1++)
		{
			aLongArray4[k1] = System.currentTimeMillis();
		}

		while (anInt1 >= 0)
		{
			if (anInt1 > 0)
			{
				anInt1--;
				if (anInt1 == 0)
				{
					return;
				}
			}
			int i2 = j;
			int j2 = k;
			j = 300;
			k = 1;
			long l1 = System.currentTimeMillis();
			if (aLongArray4[i] == 0L)
			{
				j = i2;
				k = j2;
			}
			else if (l1 > aLongArray4[i])
			{
				j = (int) ((long) (2560 * delayTime) / (l1 - aLongArray4[i]));
			}
			if (j < 25)
			{
				j = 25;
			}
			if (j > 256)
			{
				j = 256;
				k = (int) ((long) delayTime - (l1 - aLongArray4[i]) / 10L);
			}
			if (k > delayTime)
			{
				k = delayTime;
			}
			aLongArray4[i] = l1;
			i = (i + 1) % 10;
			if (k > 1)
			{
				for (int k2 = 0; k2 < 10; k2++)
				{
					if (aLongArray4[k2] != 0L)
					{
						aLongArray4[k2] += k;
					}
				}

			}
			if (k < minDelay)
			{
				k = minDelay;
			}
			try
			{
				Thread.sleep(k);
			}
			catch (InterruptedException interruptedexception)
			{
				j1++;
			}
			for (; i1 < 256; i1 += j)
			{
				clickMode = clickMode1;
				clickX = xPressed;
				clickY = yPressed;
				aLong26 = clickTime;
				clickMode1 = 0;
				process();
				readIndex = writeIndex;
			}

			i1 &= 0xff;
			if (delayTime > 0)
			{
				framesPerSecond = (1000 * j) / (delayTime * 256);
			}
			if (redrawRequired)
			{
				setAllPixelsToZero(); // TODO possibly bad here
				try
				{
					draw();
				}
				catch (NullPointerException e)
				{
					System.out.println("Draw error: " + e.toString());
				}
				cyclesUntilPaint = 0;
				redrawRequired = false;
			}
			if (cyclesUntilPaint <= 0)
			{
				drawGraphics(graphics, 0, 0);
				cyclesUntilPaint = 50;
			}
			if (shouldDebug)
			{
				System.out.println("ntime:" + l1);
				for (int l2 = 0; l2 < 10; l2++)
				{
					int i3 = ((i - l2 - 1) + 20) % 10;
					System.out.println("otim" + i3 + ":" + aLongArray4[i3]);
				}

				System.out.println("fps:" + framesPerSecond + " ratio:" + j + " count:" + i1);
				System.out.println("del:" + k + " deltime:" + delayTime + " mindel:" + minDelay);
				System.out.println("intex:" + j1 + " opos:" + i);
				shouldDebug = false;
				j1 = 0;
			}
		}
		if (anInt1 == -1)
		{
//			exit();
		}
	}

	public final void requestRedraw()
	{
		redrawRequired = true;
	}
	private int curStep = 0;
	private int destY = 0;
	private long lastStep = 0;
	private final int STEP_INCREMENT_MS = 33;

	public synchronized void drawAllSprites()
	{
	}

	// TODO IMPLEMENT ELSEWHERE
//	private static HashMap<String, MapPlayer> players = new HashMap<String, MapPlayer>();
//	public static synchronized void putPlayer(MapPlayer mp)
//	{
//		players.put(mp.getName(), mp);
//	}
//	public static synchronized MapPlayer getPlayer(String name)
//	{
//		return players.get(name);
//	}

	/**
	 * Something to do with antialiasing..?
	 * @param ai
	 * @param i
	 * @param j
	 * @param x2
	 * @param y2
	 * @param i1
	 * @param j1
	 * @param k1
	 */
	public void method20(int ai[], Dimension size, int i, int j, int k, int l, int i1, int j1, int k1)
	{
		int l1 = size.width - l;
		if (j1 == 9)
		{
			j1 = 1;
			k1 = k1 + 1 & 3;
		}
		if (j1 == 10)
		{
			j1 = 1;
			k1 = k1 + 3 & 3;
		}
		if (j1 == 11)
		{
			j1 = 8;
			k1 = k1 + 3 & 3;
		}
		if (j1 == 1)
		{
			if (k1 == 0)
			{
				for (int i2 = 0; i2 < i1; i2++)
				{
					for (int i10 = 0; i10 < l; i10++)
					{
						if (i10 <= i2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j2 = i1 - 1; j2 >= 0; j2--)
				{
					for (int j10 = 0; j10 < l; j10++)
					{
						if (j10 <= j2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k2 = 0; k2 < i1; k2++)
				{
					for (int k10 = 0; k10 < l; k10++)
					{
						if (k10 >= k2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l2 = i1 - 1; l2 >= 0; l2--)
				{
					for (int l10 = 0; l10 < l; l10++)
					{
						if (l10 >= l2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else
			{
				return;
			}
		}
		if (j1 == 2)
		{
			if (k1 == 0)
			{
				for (int i3 = i1 - 1; i3 >= 0; i3--)
				{
					for (int i11 = 0; i11 < l; i11++)
					{
						if (i11 <= i3 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j3 = 0; j3 < i1; j3++)
				{
					for (int j11 = 0; j11 < l; j11++)
					{
						if (j11 >= j3 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k3 = 0; k3 < i1; k3++)
				{
					for (int k11 = l - 1; k11 >= 0; k11--)
					{
						if (k11 <= k3 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l3 = i1 - 1; l3 >= 0; l3--)
				{
					for (int l11 = l - 1; l11 >= 0; l11--)
					{
						if (l11 >= l3 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else
			{
				return;
			}
		}
		if (j1 == 3)
		{
			if (k1 == 0)
			{
				for (int i4 = i1 - 1; i4 >= 0; i4--)
				{
					for (int i12 = l - 1; i12 >= 0; i12--)
					{
						if (i12 <= i4 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j4 = i1 - 1; j4 >= 0; j4--)
				{
					for (int j12 = 0; j12 < l; j12++)
					{
						if (j12 >= j4 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k4 = 0; k4 < i1; k4++)
				{
					for (int k12 = 0; k12 < l; k12++)
					{
						if (k12 <= k4 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l4 = 0; l4 < i1; l4++)
				{
					for (int l12 = l - 1; l12 >= 0; l12--)
					{
						if (l12 >= l4 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else
			{
				return;
			}
		}
		if (j1 == 4)
		{
			if (k1 == 0)
			{
				for (int i5 = i1 - 1; i5 >= 0; i5--)
				{
					for (int i13 = 0; i13 < l; i13++)
					{
						if (i13 >= i5 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j5 = 0; j5 < i1; j5++)
				{
					for (int j13 = 0; j13 < l; j13++)
					{
						if (j13 <= j5 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k5 = 0; k5 < i1; k5++)
				{
					for (int k13 = l - 1; k13 >= 0; k13--)
					{
						if (k13 >= k5 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l5 = i1 - 1; l5 >= 0; l5--)
				{
					for (int l13 = l - 1; l13 >= 0; l13--)
					{
						if (l13 <= l5 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else
			{
				return;
			}
		}
		if (j1 == 5)
		{
			if (k1 == 0)
			{
				for (int i6 = i1 - 1; i6 >= 0; i6--)
				{
					for (int i14 = l - 1; i14 >= 0; i14--)
					{
						if (i14 >= i6 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j6 = i1 - 1; j6 >= 0; j6--)
				{
					for (int j14 = 0; j14 < l; j14++)
					{
						if (j14 <= j6 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k6 = 0; k6 < i1; k6++)
				{
					for (int k14 = 0; k14 < l; k14++)
					{
						if (k14 >= k6 >> 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l6 = 0; l6 < i1; l6++)
				{
					for (int l14 = l - 1; l14 >= 0; l14--)
					{
						if (l14 <= l6 << 1)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			else
			{
				return;
			}
		}
		if (j1 == 6)
		{
			if (k1 == 0)
			{
				for (int i7 = 0; i7 < i1; i7++)
				{
					for (int i15 = 0; i15 < l; i15++)
					{
						if (i15 <= l / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j7 = 0; j7 < i1; j7++)
				{
					for (int j15 = 0; j15 < l; j15++)
					{
						if (j7 <= i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k7 = 0; k7 < i1; k7++)
				{
					for (int k15 = 0; k15 < l; k15++)
					{
						if (k15 >= l / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l7 = 0; l7 < i1; l7++)
				{
					for (int l15 = 0; l15 < l; l15++)
					{
						if (l7 >= i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
		}
		if (j1 == 7)
		{
			if (k1 == 0)
			{
				for (int i8 = 0; i8 < i1; i8++)
				{
					for (int i16 = 0; i16 < l; i16++)
					{
						if (i16 <= i8 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j8 = i1 - 1; j8 >= 0; j8--)
				{
					for (int j16 = 0; j16 < l; j16++)
					{
						if (j16 <= j8 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k8 = i1 - 1; k8 >= 0; k8--)
				{
					for (int k16 = l - 1; k16 >= 0; k16--)
					{
						if (k16 <= k8 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l8 = 0; l8 < i1; l8++)
				{
					for (int l16 = l - 1; l16 >= 0; l16--)
					{
						if (l16 <= l8 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
		}
		if (j1 == 8)
		{
			if (k1 == 0)
			{
				for (int i9 = 0; i9 < i1; i9++)
				{
					for (int i17 = 0; i17 < l; i17++)
					{
						if (i17 >= i9 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 1)
			{
				for (int j9 = i1 - 1; j9 >= 0; j9--)
				{
					for (int j17 = 0; j17 < l; j17++)
					{
						if (j17 >= j9 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 2)
			{
				for (int k9 = i1 - 1; k9 >= 0; k9--)
				{
					for (int k17 = l - 1; k17 >= 0; k17--)
					{
						if (k17 >= k9 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

				return;
			}
			if (k1 == 3)
			{
				for (int l9 = 0; l9 < i1; l9++)
				{
					for (int l17 = l - 1; l17 >= 0; l17--)
					{
						if (l17 >= l9 - i1 / 2)
						{
							ai[i++] = k;
						}
						else
						{
							ai[i++] = j;
						}
					}

					i += l1;
				}

			}
		}
	}

	public void drawLoadingText(String text)
	{
		while (graphics == null)
		{
			System.out.println("Graphics are null..");
			graphics = getGraphics();
			System.out.println("getSize: "+getWidth()+", "+getHeight());
			try
			{
				repaint();
			}
			catch (Exception exception)
			{
			}
			try
			{
				Thread.sleep(1000L);
			}
			catch (Exception exception1)
			{
			}
		}
		Font font = new Font("Lucida Sans", Font.PLAIN, 10);
		FontMetrics fontmetrics = getFontMetrics(font);
		Graphics2D g2 = (Graphics2D) graphics;

		// Clear whole screen
		if (shouldClearScreen)
		{
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			//g2.fillRect(0, 0, myWidth, myHeight);

			g2.setColor(new Color(0, 0, 250, 20));
			g2.fillRect(0, 0, getWidth(), getHeight());
			shouldClearScreen = false;
		}

		int xPaint = getWidth() / 2 - 150;
		int yPaint = getHeight() / 2 - 18;
		int loadAreaWidth = 300;
		int loadAreaHeight = 30;

		// Clear loading area
		g2.setColor(Color.WHITE);
		g2.fillRect(xPaint, yPaint, loadAreaWidth, loadAreaHeight);
		g2.setColor(new Color(0, 0, 250, 20));
		g2.fillRect(xPaint, yPaint, loadAreaWidth, loadAreaHeight);

		// Draw grad
		GradientPaint grad = new GradientPaint(xPaint, yPaint + loadAreaHeight, new Color(0, 0, 250, 20), xPaint, yPaint, new Color(0, 0, 250, 5));
		g2.setPaint(grad);
		g2.fill(new RoundRectangle2D.Double(xPaint, yPaint, loadAreaWidth, loadAreaHeight, 10, 10));

		// Draw text
		g2.setFont(font);
		g2.setColor(Color.BLACK);
		g2.drawString(text, (getWidth() - fontmetrics.stringWidth(text)) / 2, yPaint + (loadAreaHeight / 2) + 5);
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
	public void fillRect(int rectX, int rectY, int rectWidth, int rectHeight, int hexRGB, int alpha)
	{
		// Validate area and resize as necessary
		if (rectX < startX)
		{
			rectWidth -= startX - rectX;
			rectX = startX;
		}
		if (rectY < topY)
		{
			rectHeight -= topY - rectY;
			rectY = topY;
		}
		if (rectX + rectWidth > endX)
		{
			rectWidth = endX - rectX;
		}
		if (rectY + rectHeight > endY)
		{
			rectHeight = endY - rectY;
		}

		// Calculate transparency
		int k1 = 256 - alpha;
		int l1 = (hexRGB >> 16 & 0xff) * alpha;
		int i2 = (hexRGB >> 8 & 0xff) * alpha;
		int j2 = (hexRGB & 0xff) * alpha;
		int j3 = getWidth() - rectWidth;
		int k3 = rectX + rectY * getWidth();

		// Set the values
		for (int l3 = 0; l3 < rectHeight; l3++)
		{
			for (int i4 = -rectWidth; i4 < 0; i4++)
			{
				int k2 = (pixels[k3] >> 16 & 0xff) * k1;
				int l2 = (pixels[k3] >> 8 & 0xff) * k1;
				int i3 = (pixels[k3] & 0xff) * k1;
				int j4 = ((l1 + k2 >> 8) << 16) + ((i2 + l2 >> 8) << 8) + (j2 + i3 >> 8);
				pixels[k3++] = j4;
			}
			k3 += j3;
		}
	}

	// This actually draws a rectangle..
	public void drawRect(int rectX, int rectY, int rectWidth, int rectHeight, int hexRGB)
	{
		drawHorizontalLine(rectX, rectY, rectWidth, hexRGB);
		drawHorizontalLine(rectX, (rectY + rectHeight) - 1, rectWidth, hexRGB);
		drawVerticalLine(rectX, rectY, rectHeight, hexRGB);
		drawVerticalLine((rectX + rectWidth) - 1, rectY, rectHeight, hexRGB);
	}

	private void setAllPixelsToZero()
	{
		int i = getWidth() * getHeight();
		for (int j = 0; j < i; j++)
		{
			pixels[j] = 0;
		}

	}
	public int pixels[];
	public int topY = 0;
	public int endY = 0;
	public int startX = 0;
	public int endX = 0;
	public int centerX;
	public int centerY;
	public int anInt184;
	public int anInt185;

	/**
	 * Fills a specified area with the specified color
	 *
	 * @param pix the pixels to manipulate
	 * @param size the size of the area that the pixels cover
	 * @param x the x-coordinate of the area to fill
	 * @param y the y-coordinate of the area to fill
	 * @param width the width of the area to fill
	 * @param height the height of the area to fill
	 * @param hexColor the color to fill
	 */
	public void fillColor(int[] pix, Dimension size, int x, int y, int width, int height, int hexColor)
	{
		if (x < startX)
		{
			width -= startX - x;
			x = startX;
		}
		if (y < topY)
		{
			height -= topY - y;
			y = topY;
		}
		if (x + width > endX)
		{
			width = endX - x;
		}
		if (y + height > endY)
		{
			height = endY - y;
		}
		int j1 = size.width - width;
		int k1 = x + y * size.width;
		for (int l1 = -height; l1 < 0; l1++)
		{
			for (int i2 = -width; i2 < 0; i2++)
			{
				pix[k1++] = hexColor;
			}
			k1 += j1;
		}
	}
}
