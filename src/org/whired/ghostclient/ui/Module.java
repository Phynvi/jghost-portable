package org.whired.ghostclient.ui;

import java.awt.Component;

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
}
