package org.whired.ghost.net;

/**
 * Used to notify managers of session events
 * @author Whired
 */
public interface SessionManager
{

	/**
	 * Occurs when termination is requested
	 *
	 * @param reason the reason the session was terminated
	 */
	public void terminationRequested(String reason);

	/**
	 * Requests a termination for the specified reason
	 *
	 * @param reason the reason for terminating the session
	 */
	//public void requestTermination(String reason);
}
