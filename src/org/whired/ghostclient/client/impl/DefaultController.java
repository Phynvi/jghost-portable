package org.whired.ghostclient.client.impl;

import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * 
 * @author Whired
 */
public class DefaultController {

	final DefaultClientGhostFrame c;
	final DefaultClientGhostView v = new DefaultClientGhostView();

	public DefaultController(GhostUser user) {
		c = new DefaultClientGhostFrame(v, user);
		c.setView(v);
		v.setController(c);
	}

	public GhostClientFrame getModel() {
		return c;
	}
}
