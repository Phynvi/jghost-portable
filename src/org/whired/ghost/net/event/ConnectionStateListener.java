package org.whired.ghost.net.event;

/**
 * Listens for events pertaining to connection states
 * @author Whired
 */
public interface ConnectionStateListener {
	/**
	 * Invoked when a connection has been established
	 */
	void connected();
	
	/**
	 * Invoked when a connection has been broken
	 */
	void disconnected();
}
