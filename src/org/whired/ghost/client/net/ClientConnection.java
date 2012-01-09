package org.whired.ghost.client.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.whired.ghost.Vars;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.*;

/**
 * This class handles connections.
 */
public class ClientConnection extends Connection {

	public ClientConnection(Socket sock, Receivable r, SessionManager manager, String passPhrase) throws IOException {
		super(new WrappedInputStream(sock.getInputStream()), new WrappedOutputStream(sock.getOutputStream()), r, manager);
		Vars.getLogger().info("Connected");
		super.setEnforceTimeout(false);
		super.startReceiving();
		Vars.getLogger().info("Listening for incoming data");
		Vars.getLogger().info("Identifying...");
		sock.getOutputStream().write(48);
		Vars.getLogger().info("Authenticating...");
		super.sendPacket(0, passPhrase);
	}

	public static Connection connect(String IP, int port, String password, GhostFrame frame) throws UnknownHostException, IOException, InvalidStateException {
		if (!frame.getSessionManager().sessionIsOpen()) {
			Socket s = new Socket(IP, port);
			ClientConnection ccon = new ClientConnection(s, frame, frame.getSessionManager(), password);
			return ccon;
		}
		else {
			throw new InvalidStateException("Connection to server already exists");
		}
	}
}
