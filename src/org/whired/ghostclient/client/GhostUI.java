package org.whired.ghostclient.client;

import java.awt.Image;

import javax.swing.border.Border;

public class GhostUI {
	private final Border border;
	private final Image image;

	public GhostUI(Border border, Image image) {
		this.border = border;
		this.image = image;
	}

	public Border getBorder() {
		return border;
	}

	public Image getBackgroundImage() {
		return image;
	}
}
