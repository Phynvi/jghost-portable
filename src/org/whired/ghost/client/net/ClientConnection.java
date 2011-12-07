package org.whired.ghost.client.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.*;

/**
 *	This class handles connections.
 */
public class ClientConnection extends Connection
{

	public ClientConnection(Socket sock, Receivable r, SessionManager manager, String passPhrase) throws IOException
	{
		super(new WrappedInputStream(sock.getInputStream()), new WrappedOutputStream(sock.getOutputStream()), r, manager);
		super.setEnforceTimeout(false);
		super.startReceiving();
		sock.getOutputStream().write(48);
		super.sendPacket(0, passPhrase);
	}

	public static Connection connect(String IP, int port, String password, GhostFrame frame) throws UnknownHostException, IOException, InvalidStateException
	{
		if (frame.getConnection() == null)
		{
			Socket s = new Socket(IP, port);
			ClientConnection ccon = new ClientConnection(s, frame, frame, password);
			return ccon;
		}
		else
		{
			throw new InvalidStateException("Connection to server already exists");
		}
	}
}
