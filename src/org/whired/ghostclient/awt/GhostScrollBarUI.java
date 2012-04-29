package org.whired.ghostclient.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class GhostScrollBarUI extends BasicScrollBarUI {

	final Color transparent = new Color(0, 0, 0, 0);
	final Color ghostBlue = new Color(99, 130, 191, 120);
	final Color ghostHighlight = new Color(119, 150, 211, 140);

	public GhostScrollBarUI(final JScrollBar scrollbar) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				scrollbar.setOpaque(false);
				if (decrButton != null && decrButton.getParent() != null) {
					decrButton.getParent().remove(decrButton);
				}
				if (incrButton != null && incrButton.getParent() != null) {
					incrButton.getParent().remove(incrButton);
				}
			}
		});
	}

	@Override
	protected void paintThumb(final Graphics g, final JComponent c, final Rectangle r) {
		c.setOpaque(false);
		if (this.isThumbRollover() || this.isDragging) {
			g.setColor(ghostHighlight);
		}
		else {
			g.setColor(ghostBlue);
		}
		((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.fillRoundRect(r.x, r.y, r.width, r.height, 5, 5);
	}

	@Override
	protected void paintTrack(final Graphics g, final JComponent c, final Rectangle r) {
		g.setColor(transparent);
		g.fillRect(r.x, r.y, r.width, r.height);
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		/*
		 * if (decrButton.isVisible()) { this.decrButton.setVisible(false); } if (incrButton.isVisible()) { this.incrButton.setVisible(false); }
		 */
		super.paint(g, c);
	}

}
