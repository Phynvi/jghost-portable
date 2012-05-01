import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import org.whired.ghost.player.Player;
import org.whired.ghost.player.Rank;
import org.whired.ghost.player.RankManager;
import org.whired.ghostclient.awt.ImageUtil;
import org.whired.ghostclient.awt.RoundedBorder;
import org.whired.ghostclient.client.ClientPlayerList;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;
import org.whired.graph.Legend;
import org.whired.graph.Line;
import org.whired.graph.LineGraph;

public class PlayerGraph extends LineGraph implements Module {

	private final HashMap<Rank, Line> lines = new HashMap<Rank, Line>();
	private final HashMap<Rank, Integer> counts = new HashMap<Rank, Integer>();
	private ClientPlayerList playerList;
	private RankManager rankManager;

	@Override
	public String getModuleName() {
		return "Graph";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void setFrame(final GhostClientFrame frame) {
		this.playerList = frame.getPlayerList();
		this.rankManager = frame.getRankManager();
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return null;
	}

	@Override
	public void setResourcePath(final String path) {
	}

	@Override
	public void load() {
		final Legend leg = getLegend();
		for (final Rank r : rankManager.getAllRanks()) {
			final Line l = new Line(r.getTitle(), ImageUtil.colorFromIcon(r.getIcon()));
			lines.put(r, l);
			counts.put(r, 0);
			addLine(l);
		}
		leg.setBackground(new Color(99, 130, 191, 120));
		leg.setBorder(new RoundedBorder(new Color(99, 130, 191)));
		setBackground(Color.BLACK);
		setLabels(Label.ALL ^ Label.POINT_X);
		setFont(new Font("SansSerif", Font.PLAIN, 9));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					updateGraph();
				}
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					updateGraph();
					try {
						Thread.sleep(1000 * 60 * 60);
					}
					catch (final InterruptedException e) {
					}
				}
			}
		}).start();
	}

	private synchronized void updateGraph() {
		// Reset counts
		for (final Rank rank : counts.keySet()) {
			counts.put(rank, 0);
		}

		// Recount
		for (final Player p : playerList.getPlayers()) {
			final Rank r = rankManager.rankForLevel(p.getRights());
			final Integer oldCt = counts.get(r);
			counts.put(r, oldCt + 1);
		}

		// Update lines
		for (final Rank key : counts.keySet()) {
			final Integer igr = counts.get(key);
			lines.get(key).addNextY(igr);
		}
	}
}
