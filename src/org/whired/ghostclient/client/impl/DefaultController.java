package org.whired.ghostclient.client.impl;

import javax.swing.ImageIcon;

import org.whired.ghost.player.DefaultRightsConstants;
import org.whired.ghost.player.Rank;
import org.whired.ghost.player.RankHandler;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * @author Whired
 */
public class DefaultController {

	final DefaultClientGhostFrame c;
	final CompactClientGhostView v = new CompactClientGhostView();

	public DefaultController(final GhostUser user) {
		c = new DefaultClientGhostFrame(v, user, new RankHandler(new Rank[] { new Rank(DefaultRightsConstants.PLAYER, "Player", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/player.png"))), new Rank(DefaultRightsConstants.VETERAN, "Veteran", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/veteran.png"))), new Rank(DefaultRightsConstants.DONATOR, "Donator", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/donator.png"))), new Rank(DefaultRightsConstants.DEVELOPER, "Developer", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/javacup.png"))), new Rank(DefaultRightsConstants.MODERATOR, "Moderator", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/moderator.png"))), new Rank(DefaultRightsConstants.ADMINISTRATOR, "Administrator", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/administrator.png"))), new Rank(DefaultRightsConstants.OWNER, "Owner", new ImageIcon(DefaultClientGhostFrame.class.getResource("resources/owner2.png"))) }));
		c.setView(v);
		v.setModel(c);
	}

	public GhostClientFrame getModel() {
		return c;
	}
}
