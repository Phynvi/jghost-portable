package org.whired.ghostclient.client.settings;

import java.io.Serializable;

import org.whired.ghost.player.Player;

public class SessionSettings implements Serializable {

	public String[] defaultConnect = new String[3];
	public boolean debugOn = false;
	private final Player player;
	private String[] tabOrder = new String[0];
	private final int idInTable;

	public SessionSettings(Player player, int databaseId) {
		this.idInTable = databaseId;
		this.player = player;
	}

	/**
	 * Gets the player representation of this user
	 * 
	 * @return the player for this user
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Sets the order of the tabs on the view
	 * 
	 * @param tabs the tabs, in order
	 */
	public void setTabOrder(String[] tabs) {
		tabOrder = tabs;
	}

	/**
	 * Gets the names of tabs in their preferred order
	 * 
	 * @return the names of the tabs
	 */
	public String[] getTabOrder() {
		return tabOrder;
	}

	protected int getUserId() {
		return idInTable;
	}
}
