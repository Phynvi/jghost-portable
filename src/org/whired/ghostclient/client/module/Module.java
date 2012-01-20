package org.whired.ghostclient.client.module;

import java.awt.Component;
import java.io.Serializable;

import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;

//TODO make abstract class
/**
 * A module that can be added to the frame
 * 
 * @author Whired
 */
public interface Module extends Serializable {

	/**
	 * Gets the name of this module
	 * 
	 * @return the name
	 */
	public String getModuleName();

	/**
	 * Gets the component for this module
	 * 
	 * @return the component
	 */
	public Component getComponent();

	/**
	 * Invoked when the frame that uses this module is adding it; implementing module should save {@code frame} as a reference
	 */
	public void setFrame(GhostClientFrame frame);

	/**
	 * Gets the event listener for this module, can be {@code null}
	 */
	public GhostEventAdapter getEventListener();

	/**
	 * Invoked after the module is initialized to set the location of this module on the disk. This value should be saved if remote resources need to be loaded.
	 * 
	 * @param path the path to save
	 */
	public void setResourcePath(String path);

	/**
	 * Invoked from the EDT after the module is added to a view
	 */
	public void load();
}
