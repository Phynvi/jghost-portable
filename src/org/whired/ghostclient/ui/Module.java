package org.whired.ghostclient.ui;

import java.awt.Component;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.packet.GhostPacket;

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
	 * Invoked when a packet is received
	 * @param packet the packet that was received
	 */
	public void packetReceived(GhostPacket packet);

	public boolean listensFor(int id);
}
