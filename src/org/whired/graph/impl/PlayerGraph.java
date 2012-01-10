package org.whired.graph.impl;

import java.awt.Component;

import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;
import org.whired.graph.LineGraph;

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
	public void setFrame(GhostClientFrame frame) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public GhostEventAdapter getEventListener() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setResourcePath(String path) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
