package org.whired.ghostclient.client.impl;

import org.whired.ghostclient.client.ClientGhostFrame;


/**
 *
 * @author Whired
 */
public class DefaultController {
	
	final DefaultClientGhostFrame c = new DefaultClientGhostFrame();
	final DefaultClientGhostView v = new DefaultClientGhostView();
	
	public DefaultController() {
		c.setView(v);
		v.setController(c);
	}
	
	public ClientGhostFrame getModel() {
		return c;
	}
	
	//public DefaultClientGhostView getView() {
	//	return v;
	//}
}
