package org.whired.ghost.player;

import javax.swing.Icon;

/**
 * A rank for {@link org.whired.ghost.net.model.player.Player}s
 * @author Whired
 */
public class Rank implements DefaultRightsConstants {
	private final Icon icon;
	private final String title;
	private final int level;

	public Rank(final int level, final String title, final Icon icon) {
		if (level > 127 || level < 0) {
			throw new IllegalArgumentException("level must be between 0 and 127, inclusive");
		}
		this.level = level;
		this.title = title;
		this.icon = icon;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public int getLevel() {
		return this.level;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
