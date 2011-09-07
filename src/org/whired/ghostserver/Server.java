package org.whired.ghostserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;

public class Server implements Runnable
{

	private final int PORT_NUM;
	private static final int MIN_THROTTLING_MS = 5000;
	private final HashMap<SocketAddress, Long> throttlerList = new HashMap();
	private ServerSocket ssock;
	private final Receivable receivable;
	private final String passPhrase;
	private Connection connection;

	public Server(Receivable receivable, String passPhrase) throws IOException
	{
		this(43596, receivable, passPhrase);
	}

	public Server(int port, Receivable receivable, String passPhrase) throws IOException, IllegalArgumentException
	{
		if (((passPhrase.length() < 6) && (!passPhrase.contains("[^A-Za-z]"))) || (passPhrase.length() < 12))
		{
			throw new IllegalArgumentException("Password must be at least 6 characters with symbols/numerals or 12 characters without.");
		}
		this.PORT_NUM = port;
		this.receivable = receivable;
		this.passPhrase = passPhrase;
		this.ssock = new ServerSocket(this.PORT_NUM);
		new Thread(this, "ServerAccepter").start();
	}

	public void run()
	{
		while (true)
		{
			try
			{
				Socket s = this.ssock.accept();
				SocketAddress address = s.getRemoteSocketAddress();
				if (!isThrottling(address))
				{
					if (s.getInputStream().read() == 48)
					{
						Vars.getLogger().fine(address + " was not present in throttlers list.");
						this.connection = new ServerConnection(s, this.receivable, this.passPhrase);
						Vars.getLogger().severe("Connection is now " + this.connection);
						((ServerConnection) this.connection).startListening();
						this.connection = null;
						s.close();
						Vars.getLogger().info("Dropped connection: Session ended");
					}
					else
					{
						s.close();
						Vars.getLogger().info("Dropped connection: Invalid opcode (expected 48)");
					}
				}
				else
				{
					s.close();
					Vars.getLogger().info("Dropped connection: Client is throttling");
				}
			}
			catch (IOException ioe)
			{
				Vars.getLogger().info("Dropped connection: " + ioe.getMessage());
			}
		}
	}

	public Connection getConnection()
	{
		return this.connection;
	}

	private boolean isThrottling(SocketAddress remoteSocketAddress)
	{
		Long lastConnTime = (Long) this.throttlerList.get(remoteSocketAddress);
		if ((lastConnTime == null) || (System.currentTimeMillis() - lastConnTime.longValue() > MIN_THROTTLING_MS))
		{
			Vars.getLogger().fine("Adding " + remoteSocketAddress + " as a throttlng connection.");
			this.throttlerList.put(remoteSocketAddress, Long.valueOf(System.currentTimeMillis()));
			return false;
		}
		return true;
	}
}