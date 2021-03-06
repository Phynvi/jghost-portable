package org.whired.ghost.player;

import java.util.TreeMap;

/**
 * Manages ranks
 * @author Whired
 */
public class RankManager {

	/**
	 * Creates a new rank manager and registers the specified ranks
	 * @param ranks
	 */
	public RankManager(final Rank[] ranks) {
		registerAll(ranks);
	}

	/**
	 * Creates a new rank manager
	 */
	public RankManager() {
	}

	/**
	 * Registers a rank
	 * @param rank the rank to register
	 */
	public void registerRank(final Rank rank) {
		ranks.put(rank.getLevel(), rank);
	}

	/**
	 * Registers a set of ranks
	 * @param ranks the ranks to register
	 */
	public void registerAll(final Rank[] ranks) {
		for (final Rank r : ranks) {
			registerRank(r);
		}
	}

	/**
	 * Unregisters a rank
	 * @param rank the rank to unregister
	 */
	public void unregisterRank(final Rank rank) {
		ranks.remove(rank.getLevel());
	}

	/**
	 * Gets the rank that corresponds to the specified level
	 * @param level the level of the rank to get
	 * @return the rank, or {@code null} if none was matched
	 */
	public Rank rankForLevel(final int level) {
		return ranks.get(level);
	}

	/**
	 * Gets the rank that corresponds to the specified title
	 * @param title the title of the rank to get
	 * @return the rank, or {@code null} if none was matched
	 */
	public Rank rankForName(final String title) {
		for (final Rank r : ranks.values()) {
			if (r.getTitle().toLowerCase().equals(title.toLowerCase())) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Gets all ranks currently registered
	 */
	public Rank[] getAllRanks() {
		return ranks.values().toArray(new Rank[ranks.size()]);
	}

	public void unregisterAll() {
		ranks.clear();
	}

	private final TreeMap<Integer, Rank> ranks = new TreeMap<Integer, Rank>();
}
