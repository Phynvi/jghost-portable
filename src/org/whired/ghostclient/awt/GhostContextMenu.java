package org.whired.ghostclient.awt;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * A pretty context menu
 * @author Whired
 */
public class GhostContextMenu extends JPopupMenu {
	public GhostContextMenu(JMenuItem[] items) {
		for (JMenuItem jmi : items) {
			add(jmi);
		}
		this.setOpaque(true);
		RoundedBorder b = new RoundedBorder(new Color(99, 130, 191));
		b.setBorderInsets(new Insets(2, 0, 2, 0));
		this.setBorder(b);
		this.setBackground(new Color(7, 59, 106));
	}
}
