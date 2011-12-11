package org.whired.ghostclient.ui.graphing.impl;

import java.awt.Component;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghostclient.ui.ClientGhostFrame;
import org.whired.ghostclient.ui.Module;
import org.whired.ghostclient.ui.graphing.LineGraph;

/**
 *
 * @author Whired
 */
public class PlayerGraph extends LineGraph implements Module {

	
	public PlayerGraph() {
		
	}
	
	public void updatePlayers(int players, int staff) {
		
	}
	
	@Override
	public String getModuleName() {
		return "Players";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void moduleActivated() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void moduleDeactivated() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setFrame(ClientGhostFrame frame) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void packetReceived(GhostPacket packet) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean listensFor(int id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
