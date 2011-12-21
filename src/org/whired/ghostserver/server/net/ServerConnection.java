package org.whired.ghostserver.server.net;

import java.io.IOException;
import java.net.Socket;
import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;

public class ServerConnection extends Connection
{

	private final Socket ssock;

	public ServerConnection(Socket sock, Receivable receivable, String passPhrase)
		   throws IOException
	{
		super(new WrappedInputStream(sock.getInputStream()), new WrappedOutputStream(sock.getOutputStream()), receivable);
		setPassword(passPhrase);
		this.ssock = sock;
	}

	public void startListening()
	{
		startReceiving();
		Vars.getLogger().info("Session started with new client.");
		this.sessionCorrupt = false;

		validateSession();
		try
		{
			this.ssock.close();
		}
		catch (IOException ioe)
		{
		}
	}

	private void validateSession()
	{
		synchronized (this)
		{
			while (!this.sessionCorrupt)
			{
				try
				{
					Vars.getLogger().fine("Lock is waiting for notification.");
					wait();
					Vars.getLogger().fine("Lock notification received. No longer waiting.");
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}
}