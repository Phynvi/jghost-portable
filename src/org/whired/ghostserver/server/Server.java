package org.whired.ghostserver.server;

import org.whired.ghostserver.server.net.ServerConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;

public class Server implements Runnable {

	private final int PORT_NUM;
	private static final int MIN_THROTTLING_MS = 5000;
	private final HashMap<SocketAddress, Long> throttlerList = new HashMap();
	private ServerSocket ssock;
	private final Receivable receivable;
	private final String passPhrase;
	private Connection connection;

	/**
	 * Creates a new ghost server on the default port of {@code 43596}
	 * <p>
	 * The server initializes its own self-contained thread that will not tie up outside code.
	 * Any action initiated by the server is asynchronous.
	 * The recommended method of initializing a server is as follows:
	 * </p>
	 * <pre><code>
	 * org.whired.ghostserver.Server ghostServer = new org.whired.ghostserver.Server(new org.whired.ghost.net.Receivable()
	 * {
	 *	public boolean handlePacket(int id, int length, org.whired.ghost.net.Connection connection)
	 *	{
	 *		// Packet handling logic
	 *	}
	 * }, "mylongpassphrase");
	 * </code></pre>
	 * The above code would be close to the entry point of a server application, where the
	 * RSPS server is initialized.
	 *
	 * For information on how to send a packet using {@link org.whired.ghostserver.Server#getConnection()}, see {@link org.whired.ghost.net.Connection#sendPacket(int, java.lang.Object[])}
	 * @param receivable the receivable that will handle incoming packets
	 * @param passPhrase required by any connecting ghost clients. Must
	 *	be at least 6 characters with symbols/numerals or 12 characters without
	 * @throws IOException if the server cannot be initialized
	 * @throws IllegalArgumentException if {@code passPhrase} does not meet the specified criteria
	 */
	public Server(Receivable receivable, String passPhrase) throws IOException, IllegalArgumentException {
		this(43596, receivable, passPhrase);
	}

	/**
	 * Creates a new ghost server on the port specified
	 * <p>
	 * The server initializes its own self-contained thread that will not tie up outside code.
	 * Any action initiated by the server is asynchronous.
	 * The recommended method of initializing a server is as follows:
	 * </p>
	 * <pre><code>
	 * org.whired.ghostserver.Server ghostServer = new org.whired.ghostserver.Server(new org.whired.ghost.net.Receivable()
	 * {
	 *	public boolean handlePacket(int id, int length, org.whired.ghost.net.Connection connection)
	 *	{
	 *		// Packet handling logic
	 *	}
	 * }, "mylongpassphrase");
	 * </code></pre>
	 * The above code would be close to the entry point of a server application, where the
	 * RSPS server is initialized.
	 *
	 * For information on how to send a packet using {@link org.whired.ghostserver.Server#getConnection()}, see {@link org.whired.ghost.net.Connection#sendPacket(int, java.lang.Object[])}
	 * @param port the port the server will listen on
	 * @param receivable the receivable that will handle incoming packets
	 * @param passPhrase required by any connecting ghost clients. Must
	 *	be at least 6 characters with symbols/numerals or 12 characters without
	 * @throws IOException if the server cannot be initialized
	 * @throws IllegalArgumentException if {@code passPhrase} does not meet the specified criteria
	 *
	 */
	public Server(int port, Receivable receivable, String passPhrase) throws IOException, IllegalArgumentException {
		boolean canBeShort = false;
		for (char c : passPhrase.toCharArray()) {
			if (!Character.isLetter(c)) {
				canBeShort = true;
				break;
			}
		}
		if (((passPhrase.length() >= 6) && canBeShort) || (passPhrase.length() >= 12)) {
			this.PORT_NUM = port;
			this.receivable = receivable;
			this.passPhrase = passPhrase;
			this.ssock = new ServerSocket(this.PORT_NUM);
			new Thread(this, "ServerAccepter").start();
		}
		else {
			throw new IllegalArgumentException("Password must be at least 6 characters with symbols/numerals or 12 characters without.");
		}
	}

	public void run() {
		while (true) {
			try {
				Socket s = this.ssock.accept();
				SocketAddress address = s.getRemoteSocketAddress();
				if (!isThrottling(address)) {
					if (s.getInputStream().read() == 48) {
						Vars.getLogger().fine(address + " was not present in throttlers list.");
						this.connection = new ServerConnection(s, this.receivable, this.passPhrase);
						Vars.getLogger().severe("Connection is now " + this.connection);
						((ServerConnection) this.connection).startListening();
						this.connection = null;
						s.close();
						Vars.getLogger().info("Dropped connection: Session ended");
					}
					else {
						s.close();
						Vars.getLogger().info("Dropped connection: Invalid opcode (expected 48)");
					}
				}
				else {
					s.close();
					Vars.getLogger().info("Dropped connection: Client is throttling");
				}
			}
			catch (IOException ioe) {
				Vars.getLogger().info("Dropped connection: " + ioe.getMessage());
			}
		}
	}

	/**
	 * Gets the current connection to the client if one exists
	 * @return the current connection if one exists, otherwise {@code null}
	 */
	public Connection getConnection() {
		return this.connection;
	}

	private boolean isThrottling(SocketAddress remoteSocketAddress) {
		Long lastConnTime = (Long) this.throttlerList.get(remoteSocketAddress);
		if ((lastConnTime == null) || (System.currentTimeMillis() - lastConnTime.longValue() > MIN_THROTTLING_MS)) {
			Vars.getLogger().fine("Adding " + remoteSocketAddress + " as a throttlng connection.");
			this.throttlerList.put(remoteSocketAddress, Long.valueOf(System.currentTimeMillis()));
			return false;
		}
		return true;
	}
}