package org.whired.ghost.net.model.player;

import java.util.TreeMap;

/**
 * The definition of ranks
 * @author Whired
 */
public class RankHandler {
	public void registerRank(Rank rank) {
		ranks.put(rank.getLevel(), rank);
	}
	public void registerRanks(Rank[] ranks) {
		for(Rank r : ranks)
			registerRank(r);
	}
	public void unregisterRank(Rank rank) {
		ranks.remove(rank.getLevel());
	}
	public Rank rankForLevel(int level) {
		return ranks.get(level);
	}
	private final TreeMap<Integer, Rank> ranks = new TreeMap<Integer, Rank>();
}
