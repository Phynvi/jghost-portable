package org.whired.rsmap.ui;

import java.awt.Color;
import java.awt.Point;

import org.whired.rsmap.graphics.sprites.TextSprite;

/**
 * Represents a button that can be added to the map. Contains basic button functionality.
 * @author Whired
 */
public abstract class MapButton {

	/** The text to display on this button */
	private String text;
	/** The background color of this button */
	private int hexBackgroundColor = 0xFFFFFF;
	/** The border color of this button */
	private int hexBorderColor = 0x000000;
	/** The x-coordinate of this button */
	private int x;
	/** The y-coordinate of this button */
	private int y;
	/** The width of this button */
	private int width;
	/** The height of this button */
	private int height;
	/** The preferred font for the button */
	private TextSprite textSprite = null;

	/**
	 * Creates a new button
	 * @param text the text to display on this button
	 * @param x the x-coordinate of this button
	 * @param y the y-coordinate of this button
	 * @param width the width of this button
	 * @param height the height of this button
	 */
	public MapButton(final String text, final int x, final int y, final int width, final int height) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates a new button
	 * @param text the text to display on this button
	 * @param x the x-coordinate of this button
	 * @param y the y-coordinate of this button
	 * @param width the width of this button
	 * @param height the height of this button
	 * @param hexBackgroundColor the background color of this button
	 * @param hexBorderColor the border color of this button
	 */
	public MapButton(final String text, final int x, final int y, final int width, final int height, final int hexBackgroundColor, final int hexBorderColor) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hexBackgroundColor = hexBackgroundColor;
		this.hexBorderColor = hexBorderColor;
	}

	/**
	 * Creates a new button
	 * @param text the text to display on this button
	 * @param x the x-coordinate of this button
	 * @param y the y-coordinate of this button
	 * @param width the width of this button
	 * @param height the height of this button
	 * @param backgroundColor the background color of this button
	 * @param borderColor the border color of this button
	 */
	public MapButton(final String text, final int x, final int y, final int width, final int height, final Color backgroundColor, final Color borderColor) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hexBackgroundColor = colorToHex(backgroundColor);
		this.hexBorderColor = colorToHex(borderColor);
	}

	/**
	 * Sets the background color of this button
	 * @param color the color to set
	 */
	public void setBackgroundColor(final Color color) {
		hexBackgroundColor = colorToHex(color);
	}

	/**
	 * Sets the background color of this button
	 * @param hexColor the color to set
	 */
	public void setBackgroundColor(final int hexColor) {
		hexBackgroundColor = hexColor;
	}

	/**
	 * Sets the border color of this button
	 * @param color the color to set
	 */
	public void setBorderColor(final Color color) {
		hexBorderColor = colorToHex(color);
	}

	/**
	 * Sets the border color of this button
	 * @param hexColor the color to set
	 */
	public void setBorderColor(final int hexColor) {
		hexBorderColor = hexColor;
	}

	/**
	 * Gets the background color of this button
	 * @return the color
	 */
	public int getBackgroundColor() {
		return hexBackgroundColor;
	}

	/**
	 * Gets the border color of this button
	 * @return the color
	 */
	public int getBorderColor() {
		return hexBorderColor;
	}

	/**
	 * Sets the preferred text sprite for this button
	 * @param ts the text sprite to set
	 */
	public void setTextSprite(final TextSprite ts) {
		this.textSprite = ts;
	}

	/**
	 * Gets the text sprite for this button
	 * @return the sprite, or {@code null}
	 */
	public TextSprite getTextSprite() {
		return this.textSprite;
	}

	/**
	 * Converts a {@code Color} to a hexadecimal integer
	 * @param color the {@code Color} to convert
	 * @return the {@code int} equivalent of the given {@code Color}
	 */
	private int colorToHex(final Color color) {
		return Integer.parseInt(Integer.toHexString(color.getRGB()).substring(2), 16);
	}

	public abstract void draw();

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(final int y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(final int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(final int height) {
		this.height = height;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * Tests whether or not {@code point} lies within this button
	 * @param point the point to test
	 * @return {@code true} if {@code point} is within this button's bounds, otherwise {@code false}
	 */
	public boolean contains(final Point point) {
		return point.getX() >= this.getX() && point.getX() <= this.getWidth() + this.getX() && point.getY() >= this.getY() && point.getY() <= this.getHeight() + this.getY();
	}

	/**
	 * Invoked when this button has been clicked
	 */
	public abstract void clicked();

	@Override
	public String toString() {
		return this.getText();
	}
}
