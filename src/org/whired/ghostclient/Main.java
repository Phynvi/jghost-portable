package org.whired.ghostclient;

import javax.swing.ImageIcon;
import org.whired.ghost.client.util.SessionSettings;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.Rank;
import org.whired.ghost.net.model.player.RankDefinitions;
import org.whired.ghostclient.ui.GhostFrameImpl;
import org.whired.ghostclient.ui.GhostUserImpl;

public class Main
{
	public static void main(String args[])
	{
		GhostFrameImpl impl = new GhostFrameImpl();
		RankDefinitions ranks = new RankDefinitions();
		ranks.register(new Rank(0, "Player", new ImageIcon(impl.getClass().getResource("resources/player.png"))));
		ranks.register(new Rank(1, "Veteran", new ImageIcon(impl.getClass().getResource("resources/veteran.png"))));
		ranks.register(new Rank(2, "Donator", new ImageIcon(impl.getClass().getResource("resources/donator.png"))));
		ranks.register(new Rank(3, "Developer", new ImageIcon(impl.getClass().getResource("resources/developer.png"))));
		ranks.register(new Rank(4, "Moderator", new ImageIcon(impl.getClass().getResource("resources/moderator.png"))));
		ranks.register(new Rank(5, "Administrator", new ImageIcon(impl.getClass().getResource("resources/administrator.png"))));
		ranks.register(new Rank(6, "Owner", new ImageIcon(impl.getClass().getResource("resources/owner.png"))));
		SessionSettings settings;
		try {
			settings = SessionSettings.loadFromDisk(ranks);
		} catch(Exception e) {
			settings = new SessionSettings(new Player("Admin", ranks.forLevel(5)), ranks);
		}
		new GhostUserImpl(impl, settings);
	}
}
