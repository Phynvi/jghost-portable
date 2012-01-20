package org.whired.ghostclient.client;

import org.whired.ghost.net.event.SessionEventListener;
import org.whired.ghost.player.Player;
import org.whired.ghostclient.client.module.Module;

/**
 * A graphical view for the client
 * 
 * @author Whired
 */
public interface GhostClientView extends SessionEventListener {
	/**
	 * Sets the controller for this view
	 * 
	 * @param controller the controller to set
	 */
	public void setModel(GhostClient controller);

	/**
	 * Sets the text of the main input box on this view
	 * 
	 * @param text the text to set
	 */
	public void setInputText(String text);

	/**
	 * Applies focus to the main input box on this view
	 */
	public void focusInputBox();

	/**
	 * Invoked when a module's component needs to be added to this view This method is called from the event dispatching thread.
	 * 
	 * @param module the module to add
	 */
	public void moduleAdded(Module module);

	/**
	 * Invoked when a player needs to be added to this view's graphical list
	 * 
	 * @param player the player to add
	 */
	public void playerAdded(Player player);

	/**
	 * Invoked when a player needs to be removed from this view's graphical list
	 * 
	 * @param player the player to remove
	 */
	public void playerRemoved(Player player);

	/**
	 * Notifies the view that some module activity requires the user's attention.
	 * 
	 * @param module the module that requires attention
	 */
	public void displayModuleNotification(Module module);
}
