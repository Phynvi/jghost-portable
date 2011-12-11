package org.whired.ghost.net.model.player;

import java.util.TreeMap;

/**
 * The definition of ranks
 * @author Whired
 */
public class RankHandler {
	/**
	 * Registers a rank
	 * @param rank the rank to register
	 */
	public void registerRank(Rank rank) {
		ranks.put(rank.getLevel(), rank);
	}
	/**
	 * Registers a set of ranks
	 * @param ranks the ranks to register
	 */
	public void registerRanks(Rank[] ranks) {
		for(Rank r : ranks)
			registerRank(r);
	}
	/**
	 * Unregisters a rank
	 * @param rank the rank to unregister
	 */
	public void unregisterRank(Rank rank) {
		ranks.remove(rank.getLevel());
	}
	/**
	 * Gets the rank that corresponds to the specified level
	 * @param level the level of the rank to get
	 * @return the rank, or {@code null} if none was matched
	 */
	public Rank rankForLevel(int level) {
		return ranks.get(level);
	}
	/**
	 * Gets the rank that corresponds to the specified title
	 * @param title the title of the rank to get
	 * @return the rank, or {@code null} if none was matched
	 */
	public Rank rankForName(String title) {
		for(Rank r : ranks.values())
			if(r.getTitle().toLowerCase().equals(title.toLowerCase()))
				return r;
		return null;
	}
	private final TreeMap<Integer, Rank> ranks = new TreeMap<Integer, Rank>();
}
