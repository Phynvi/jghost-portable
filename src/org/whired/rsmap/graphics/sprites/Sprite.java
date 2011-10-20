package org.whired.rsmap.graphics.sprites;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import org.whired.rsmap.graphics.RSCanvas;

/**
 * Represents an image to be drawn
 *
 * @author whired
 */
public abstract class Sprite {

	private final int width;
	private final int height;
	public boolean isRelativeToMap = true;
	public Point location;
	private ArrayList<MouseListener> listeners = new ArrayList<MouseListener>();
	/**
	 * The values of the pixels for this sprite
	 */
	public int spritePixels[];

	public Sprite(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Adds a mouse listener to this sprite
	 * @param listener the listener to add
	 */
	public void addMouseListener(MouseListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a mouse listener from this sprite
	 * @param listener the listener to add
	 */
	public void removeMouseListener(MouseListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notifies all listeners of a mouse event
	 * @param eventType the type of event to fire
	 * @param event the event to fire
	 */
	private void fireMouseEvent(int eventType, MouseEvent event) {
		for (MouseListener ml : listeners)
			switch (eventType) {
				case MouseEvent.MOUSE_CLICKED:
					ml.mouseClicked(event);
					break;
				case MouseEvent.MOUSE_ENTERED:
					ml.mouseEntered(event);
					break;
				case MouseEvent.MOUSE_EXITED:
					ml.mouseExited(event);
					break;
				case MouseEvent.MOUSE_PRESSED:
					ml.mousePressed(event);
					break;
				case MouseEvent.MOUSE_RELEASED:
					ml.mouseReleased(event);
					break;
			}
	}

	/**
	 * Draws this sprite
	 * @param x the x coordinate to start drawing at
	 * @param y the y coordinate to start drawing at
	 * @param area the area to draw on
	 */
	public abstract void drawSprite(int x, int y, RSCanvas area);

	public int method40() {
		int someInt = spritePixels[8] - 1;
		//System.out.println("SomeInt: "+ someInt);
		return someInt;
	}

	public void drawLetter(int ai[], int abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
		try {
			int l1 = -(l >> 2);
			l = -(l & 3);
			for (int i2 = -i1; i2 < 0; i2++) {
				for (int j2 = l1; j2 < 0; j2++) {
					if (abyte0[j++] != 0)
						ai[k++] = i;
					else
						k++;
					if (abyte0[j++] != 0)
						ai[k++] = i;
					else
						k++;
					if (abyte0[j++] != 0)
						ai[k++] = i;
					else
						k++;
					if (abyte0[j++] != 0)
						ai[k++] = i;
					else
						k++;
				}

				for (int k2 = l; k2 < 0; k2++)
					if (abyte0[j++] != 0)
						ai[k++] = i;
					else
						k++;

				k += j1;
				j += k1;
			}

		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

//	public static int anIntArray201[];
//
//	static
//	{
//		anIntArray201 = new int[256];
//		for (int i = 0; i < 256; i++)
//		{
//			int j = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ".indexOf(i);
//			if (j == -1)
//			{
//				j = 74;
//			}
//			anIntArray201[i] = j * 9;
//		}
//
//	}
	/**
	 * Gets the width of this sprite
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of this sprite
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
}
