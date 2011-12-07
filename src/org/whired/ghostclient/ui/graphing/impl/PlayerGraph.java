package org.whired.ghostclient.ui.graphing.impl;

import java.awt.Component;
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
}
