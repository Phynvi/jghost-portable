package org.whired.ghostserver.server.net;

import java.io.IOException;
import java.net.Socket;

import org.whired.ghost.constants.Vars;
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
		Vars.getLogger().info("Session started with new client.");
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
		Vars.getLogger().fine("Notifying");
		notify();
		Vars.getLogger().fine("Notified");
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
		while (sessionValid) {
			try {
				Vars.getLogger().fine("Lock is waiting for notification.");
				wait();
				Vars.getLogger().fine("Lock notification received. No longer waiting.");
			}
			catch (InterruptedException e) {
			}
		}
	}
}