package org.whired.ghost.net.event;

/**
 * Listens for events pertaining to connection states
 * 
 * @author Whired
 */
public interface SessionEventListener {
	/**
	 * Invoked when a connection has been established
	 */
	public void sessionOpened();

	/**
	 * Invoked when a connection has been broken
	 */
	public void sessionClosed();
}
