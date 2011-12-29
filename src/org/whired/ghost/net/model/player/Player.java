package org.whired.ghost.net.model.player;

import java.awt.Point;
import java.io.Serializable;

/**
 * Provides basic details about a player
 *
 * @author Whired
 */
public class Player implements Serializable {

	/**
	 * The name of this player
	 */
	private String name;
	/**
	 * The rights of this player
	 */
	private byte rights;
	private int x, y;
	
	/**
	 * Creates a new player with the specified name and rights
	 * @param name the name of the player
	 * @param rights the rights of the player (-128 to 127)
	 */
	public Player(String name, int rights, int x, int y) {
		this.name = name;
		this.rights = (byte)rights;
		this.x = x;
		this.y = y;
	}

	public Point getLocation()
	{
		return new Point(x, y);
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the long that represents this player's name
	 * @return the converted long
	 */
	public long nameToLong() {
		long l = 0L;
		for (int i = 0; i < getName().length() && i < 12; i++) {
			char c = getName().charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}

	/**
	 * Gets the name of this player
	 * @return the name of this player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this player
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the rights of this player
	 * @return the rights
	 */
	public int getRights() {
		return rights;
	}
	/**
	 * Sets the rights of this player
	 * @param rights the rights to set (-128 to 127)
	 */
	public void setRights(int rights) {
		if (rights > 127)
			rights = 127;
		if (rights < 0)
			rights = 0;
		this.rights = (byte) rights;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
