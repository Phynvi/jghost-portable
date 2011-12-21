package org.whired.ghostclient.client.module;

import java.awt.Component;
import org.whired.ghostclient.client.ClientGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;

/**
 * A module that can be added to the frame
 * @author Whired
 */
public interface Module {
	
	/**
	 * Gets the name of this module
	 * @return the name
	 */
	public String getModuleName();
	
	/**
	 * Gets the component for this module
	 * @return the component
	 */
	public Component getComponent();
	
	/**
	 * Invoked when this module is activated
	 */
	public void moduleActivated();
	
	/**
	 * Invoked when this module is deactivated
	 */
	public void moduleDeactivated();
	
	/**
	 * Invoked when the frame that uses this module is adding it
	 */
	public void setFrame(ClientGhostFrame frame);
	
	/**
	 * Gets the event listener for this module, can be {@code null}
	 */
	public GhostEventAdapter getEventListener();
}
