package org.whired.ghost.net;

import java.util.HashSet;

import org.whired.ghost.net.event.SessionEventListener;

/**
 * A session between a client and a server
 * @author Whired
 */
public class SessionManager {
	private Connection connection;
	private final HashSet<SessionEventListener> listeners = new HashSet<SessionEventListener>();

	/**
	 * Adds an event listener to this session manager
	 * @param listener the listener to add
	 */
	public void addEventListener(final SessionEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes an event listener from this session manager
	 * @param listener the listener to remove
	 */
	public void removeEventListener(final SessionEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Determines whether or not this session is open
	 * @return {@code true} if there is currently a connection, otherwise {@code false}
	 */
	public boolean sessionIsOpen() {
		return this.connection != null;
	}

	/**
	 * Removes the current connection
	 * @param reason the reason the connection is being removed
	 */
	public void removeConnection(final String reason) {
		this.connection.endSession(reason);
	}

	/**
	 * Sets the connection for this session
	 * @param connection the connection to set
	 */
	public void setConnection(final Connection connection) {
		if (sessionIsOpen()) {
			throw new IllegalStateException("Connection already exists");
		}
		this.connection = connection;
	}

	/**
	 * Invoked when the session is opened
	 */
	public void sessionOpened() {
		for (final SessionEventListener l : listeners) {
			l.sessionOpened();
		}
	}

	/**
	 * Gets the current connection if one exists
	 * @return the current {@link org.whired.ghost.net.Connection} if one exists, otherwise {@code null}
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Invoked when the session has ended
	 */
	protected void sessionEnded() {
		this.connection = null;
		for (final SessionEventListener l : listeners) {
			l.sessionClosed();
		}
	}
}
