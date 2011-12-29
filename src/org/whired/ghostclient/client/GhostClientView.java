package org.whired.ghostclient.client;

import org.whired.ghost.net.event.SessionEventListener;
import org.whired.ghostclient.client.module.Module;

/**
 * The contract that a graphical view must follow
 * @author Whired
 */
public interface GhostClientView extends SessionEventListener {
	/**
	 * Sets the controller for this view
	 * @param controller the controller to set
	 */
	public void setController(GhostClient controller);
	
	/**
	 * Sets the text of the main input box on this view
	 * @param text the text to set
	 */
	public void setInputText(String text);
	
	/**
	 * Invoked when a module's component needs to be added to this view
	 * This method is called from the event dispatching thread.
	 * @param module the module to add
	 */
	public void moduleAdded(Module module);
	
	/**
	 * Notifies the view that some module activity requires the user's
	 *	attention.
	 * @param module the module that requires attention
	 */
	public void displayModuleNotification(Module module);
}
