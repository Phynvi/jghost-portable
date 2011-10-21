package org.whired.ghost.net.model.player;

import java.util.TreeMap;

/**
 * The definition of ranks
 * @author Whired
 */
public class RankDefinitions {
	public void register(Rank rank) {
		ranks.put(rank.getLevel(), rank);
	}
	public void deregister(Rank rank) {
		ranks.remove(rank.getLevel());
	}
	public Rank forLevel(int level) {
		return ranks.get(level);
	}
	private final TreeMap<Integer, Rank> ranks = new TreeMap<Integer, Rank>();
}
