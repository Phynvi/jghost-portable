package org.whired.ghostserver.server.io;

import java.io.IOException;
import java.net.Socket;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;

public class ServerConnection extends Connection {

	private final Socket ssock;
	private boolean sessionValid = true;

	public ServerConnection(Socket sock, Receivable receivable, String passPhrase) throws IOException {
		super(new WrappedInputStream(sock.getInputStream()), new WrappedOutputStream(sock.getOutputStream()), receivable);
		setPassword(passPhrase);
		this.ssock = sock;
	}

	@Override
	public void startReceiving() {
		super.startReceiving();
		Constants.getLogger().info("Session started with new client.");
		validateSession();
		try {
			this.ssock.close();
		}
		catch (IOException ioe) {
		}
	}

	/**
	 * Marks this session as invalid and notifies all waiting threads
	 */
	public final synchronized void invalidateSession() {
		sessionValid = false;
		Constants.getLogger().fine("Notifying");
		notify();
		Constants.getLogger().fine("Notified");
	}

	@Override
	protected void endSession(String reason) {
		super.endSession(reason);
		invalidateSession();
	}

	/**
	 * Blocks until this session becomes invalid
	 */
	private final synchronized void validateSession() {
		while (sessionValid)
			try {
				Constants.getLogger().fine("Lock is waiting for notification.");
				wait();
				Constants.getLogger().fine("Lock notification received. No longer waiting.");
			}
			catch (InterruptedException e) {
			}
	}
}