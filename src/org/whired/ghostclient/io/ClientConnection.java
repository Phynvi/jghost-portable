package org.whired.ghostclient.io;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.InvalidStateException;
import org.whired.ghost.net.Receivable;
import org.whired.ghost.net.SessionManager;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;

/**
 * This class handles connections.
 */
public class ClientConnection extends Connection {

	public ClientConnection(Socket sock, Receivable r, SessionManager manager, String passPhrase) throws IOException {
		super(sock, r, manager);
		Constants.getLogger().info("Connected");
		super.startReceiving();
		Constants.getLogger().info("Listening for incoming data");
		Constants.getLogger().info("Identifying...");
		sock.getOutputStream().write(48);
		Constants.getLogger().info("Authenticating...");
		new GhostAuthenticationPacket(passPhrase).send(this);
	}

	public static Connection connect(String IP, int port, String password, GhostFrame frame) throws UnknownHostException, IOException, InvalidStateException {
		if (!frame.getSessionManager().sessionIsOpen()) {
			Socket s = new Socket(IP, port);
			ClientConnection ccon = new ClientConnection(s, frame, frame.getSessionManager(), password);
			return ccon;
		}
		else
			throw new InvalidStateException("Connection to server already exists");
	}

	@Override
	protected void readPacket(WrappedInputStream inputStream) throws IOException {
		// For now there's not much ClientConnection needs to check up on
		// may change in future
		int packetId = inputStream.readByte();
		try {
			Constants.getLogger().fine("Notifying receivable that external packet " + packetId + " has been received.");
			if (!receivable.handlePacket(packetId, this))
				endSession("Packet " + packetId + " was not handled");
		}
		catch (IOException e) {
			endSession("Error while handling packet " + packetId);
		}
	}
}
