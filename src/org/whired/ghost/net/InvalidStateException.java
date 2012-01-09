package org.whired.ghost.net;

/**
 * Thrown when a user requests a state change that is invalid
 * 
 * @author Whired
 */
public class InvalidStateException extends Exception {

	/**
	 * Creates a new {@code InvalidStateException} with the specified message
	 * 
	 * @param message
	 */
	public InvalidStateException(String message) {
		super(message);
	}
}
