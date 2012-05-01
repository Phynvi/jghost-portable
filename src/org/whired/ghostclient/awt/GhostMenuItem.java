package org.whired.ghostclient.awt;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;

/**
 * A pretty menu item
 * @author Whired
 */
public class GhostMenuItem extends JMenuItem {

	public GhostMenuItem(String label) {
		super(label);
		this.setForeground(Color.WHITE);
		this.setOpaque(false);
		this.setBorder(BorderFactory.createEmptyBorder());
	}
}
