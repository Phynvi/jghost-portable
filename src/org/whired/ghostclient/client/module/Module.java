package org.whired.ghostclient.client.module;

import java.awt.Component;
import java.io.Serializable;
import org.whired.ghostclient.client.ClientGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;

/**
 * A module that can be added to the frame
 * @author Whired
 */
public interface Module extends Serializable {
	
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
	 * Invoked when the frame that uses this module is adding it; implementing
	 * module should save {@code frame} as a reference
	 */
	public void setFrame(ClientGhostFrame frame);
	
	/**
	 * Gets the event listener for this module, can be {@code null}
	 */
	public GhostEventAdapter getEventListener();

	/**
	 * Invoked after the module is added to a view
	 * @param resourceDir the directory that this module was loaded from --
	 * resources can be loaded by using this directory
	 */
	public void load(String resourceDir);
}
