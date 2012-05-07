package org.whired.ghostclient.io;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.InvalidStateException;
import org.whired.ghost.net.SessionManager;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;
import org.whired.ghost.net.packet.PacketHandler;

/**
 * Handles client connections
 */
public class ClientConnection extends Connection {
	public ClientConnection(final Socket sock, final String passPhrase, final SessionManager manager, PacketHandler handler) throws IOException {
		super(sock, manager, handler);
		Constants.getLogger().info("Connected");
		if (sock.getInputStream().read() == 1) {
			Constants.getLogger().info("Authenticating...");
			new GhostAuthenticationPacket(passPhrase).send(this);
			new Thread(new Runnable() {
				@Override
				public void run() {
					startReceiving();
				}
			}).start();
		}
		else {
			endSession("Client is connecting too fast!");
		}
	}

	public static Connection connect(final String IP, final int port, final String password, final SessionManager manager, final PacketHandler handler) throws UnknownHostException, IOException, InvalidStateException {
		if (!manager.sessionIsOpen()) {
			final Socket s = new Socket(IP, port);
			final ClientConnection ccon = new ClientConnection(s, password, manager, handler);
			return ccon;
		}
		else {
			throw new InvalidStateException("Connection to server already exists");
		}
	}
}
