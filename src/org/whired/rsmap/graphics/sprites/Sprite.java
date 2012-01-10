package org.whired.rsmap.graphics.sprites;

import java.awt.Point;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import org.whired.rsmap.graphics.RSCanvas;

/**
 * Represents an image to be drawn
 * 
 * @author Whired
 */
public abstract class Sprite {

	public final int width;
	public final int height;
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
	 * 
	 * @param listener the listener to add
	 */
	public void addMouseListener(MouseListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a mouse listener from this sprite
	 * 
	 * @param listener the listener to add
	 */
	public void removeMouseListener(MouseListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Draws this sprite
	 * 
	 * @param x the x coordinate to start drawing at
	 * @param y the y coordinate to start drawing at
	 * @param area the area to draw on
	 */
	public abstract void drawSprite(int x, int y, RSCanvas area);

	public int method40() {
		int someInt = spritePixels[8] - 1;
		return someInt;
	}

	/**
	 * Gets the width of this sprite
	 * 
	 * @return the width
	 */
	public abstract int getWidth();

	/**
	 * Gets the height of this sprite
	 * 
	 * @return the height
	 */
	public abstract int getHeight();
}
