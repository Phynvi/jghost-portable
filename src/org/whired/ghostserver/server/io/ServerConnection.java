package org.whired.ghostserver.server.io;

import java.io.IOException;
import java.net.Socket;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;
import org.whired.ghost.net.packet.PacketType;

public class ServerConnection extends Connection {

	private boolean sessionValid = true;
	private boolean expectingPassword = true;
	private final String password;
	private final int HOUR = 60000*60;
	public ServerConnection(Socket sock, Receivable receivable, String password) throws IOException {
		super(sock, receivable);
		this.password = password;
	}

	@Override
	public void startReceiving() {
		super.startReceiving();
		validateSession();
	}

	/**
	 * Marks this session as invalid and notifies all waiting threads
	 */
	public final synchronized void invalidateSession() {
		sessionValid = false;
		notify();
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
				wait();
			}
			catch (InterruptedException e) {
			}
	}

	@Override
	protected void readPacket(WrappedInputStream inputStream) throws IOException {
		socket.setSoTimeout(expectingPassword ? 1000 : 72*HOUR);
		int packetId = inputStream.readByte();
		if (expectingPassword) {
			if (packetId == PacketType.AUTHENTICATION) {
				GhostAuthenticationPacket ap = new GhostAuthenticationPacket();
				if (ap.receive(this))
					if (ap.password.equals(password)) {
						Constants.getLogger().info("Password matched, client accepted.");
						try {
							getOutputStream().writeByte(PacketType.AUTHENTICATE_SUCCESS);
						}
						catch (IOException e) {
						}
						expectingPassword = false;
					}
					else
						endSession("Password incorrect");
			}
			else
				endSession("Password expected, but not received");
		}
		else {
			try {
				if (!receivable.handlePacket(packetId, this))
					endSession("Packet " + packetId + " was not handled");
			}
			catch (IOException e) {
				endSession("Error while handling packet " + packetId);
			}
		}
	}
}