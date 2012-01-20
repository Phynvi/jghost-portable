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
import org.whired.ghost.net.WrappedOutputStream;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;

/**
 * This class handles connections.
 */
public class ClientConnection extends Connection {

	public ClientConnection(Socket sock, Receivable r, SessionManager manager, String passPhrase) throws IOException {
		super(new WrappedInputStream(sock.getInputStream()), new WrappedOutputStream(sock.getOutputStream()), r, manager);
		Constants.getLogger().info("Connected");
		super.setEnforceTimeout(false);
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
}
