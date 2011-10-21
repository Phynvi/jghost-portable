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
		ranks.register(new Rank(0, "Player", new ImageIcon(impl.getClass().getResource("resources/level_0.png"))));
		ranks.register(new Rank(1, "Donator", new ImageIcon(impl.getClass().getResource("resources/level_1.png"))));
		ranks.register(new Rank(2, "Moderator", new ImageIcon(impl.getClass().getResource("resources/level_2.png"))));
		ranks.register(new Rank(3, "Administrator", new ImageIcon(impl.getClass().getResource("resources/level_3.png"))));
		ranks.register(new Rank(4, "Owner", new ImageIcon(impl.getClass().getResource("resources/level_4.png"))));
		SessionSettings settings;
		try {
			settings = SessionSettings.loadFromDisk(ranks);
		} catch(Exception e) {
			settings = new SessionSettings(new Player("Whired", ranks.forLevel(4)), ranks);
		}
		new GhostUserImpl(impl, settings);
	}
}
