package org.whired.ghostclient.client;

import org.whired.ghost.net.event.SessionEventListener;
import org.whired.ghostclient.client.module.Module;

/**
 *
 * @author Whired
 */
public interface GhostClientView extends SessionEventListener {
	public void setController(GhostClient controller);
	public void setInputText(String text);
	public void moduleAdded(Module module);
}
