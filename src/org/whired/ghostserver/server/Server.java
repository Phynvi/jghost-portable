package org.whired.ghostserver.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.Receivable;
import org.whired.ghostserver.server.io.ServerConnection;

public class Server implements Runnable {

	private final int PORT_NUM;
	private static final long MIN_THROTTLING_MS = 000;
	private final HashMap<String, Long> throttlerList = new HashMap<String, Long>();
	private ServerSocket ssock;
	private final Receivable receivable;
	private final String passPhrase;
	private Connection connection;

	/**
	 * Creates a new ghost server on the default port of {@code 43596}
	 * <p>
	 * The server initializes its own self-contained thread that will not tie up outside code. Any action initiated by the server is asynchronous. The recommended method of initializing a server is as follows:
	 * </p>
	 * 
	 * <pre>
	 * <code>
	 * org.whired.ghostserver.Server ghostServer = new org.whired.ghostserver.Server(new org.whired.ghost.net.Receivable()
	 * {
	 * public boolean handlePacket(int id, int length, org.whired.ghost.net.Connection connection)
	 * {
	 * 	// Packet handling logic
	 * }
	 * }, "mylongpassphrase");
	 * </code>
	 * </pre>
	 * 
	 * The above code would be close to the entry point of a server application, where the RSPS server is initialized. For information on how to send a packet using {@link org.whired.ghostserver.Server#getConnection()}, see {@link org.whired.ghost.net.Connection#sendPacket(int, java.lang.Object[])}
	 * 
	 * @param receivable the receivable that will handle incoming packets
	 * @param passPhrase required by any connecting ghost clients. Must be at least 6 characters with symbols/numerals or 12 characters without
	 * @throws IOException if the server cannot be initialized
	 * @throws IllegalArgumentException if {@code passPhrase} does not meet the specified criteria
	 */
	public Server(Receivable receivable, String passPhrase) throws IOException, IllegalArgumentException {
		this(43596, receivable, passPhrase);
	}

	/**
	 * Creates a new ghost server on the port specified
	 * <p>
	 * The server initializes its own self-contained thread that will not tie up outside code. Any action initiated by the server is asynchronous. The recommended method of initializing a server is as follows:
	 * </p>
	 * 
	 * <pre>
	 * <code>
	 * org.whired.ghostserver.Server ghostServer = new org.whired.ghostserver.Server(new org.whired.ghost.net.Receivable()
	 * {
	 * public boolean handlePacket(int id, int length, org.whired.ghost.net.Connection connection)
	 * {
	 * 	// Packet handling logic
	 * }
	 * }, "mylongpassphrase");
	 * </code>
	 * </pre>
	 * 
	 * The above code would be close to the entry point of a server application, where the RSPS server is initialized. For information on how to send a packet using {@link org.whired.ghostserver.Server#getConnection()}, see {@link org.whired.ghost.net.Connection#sendPacket(int, java.lang.Object[])}
	 * 
	 * @param port the port the server will listen on
	 * @param receivable the receivable that will handle incoming packets
	 * @param passPhrase required by any connecting ghost clients. Must be at least 6 characters with symbols/numerals or 12 characters without
	 * @throws IOException if the server cannot be initialized
	 * @throws IllegalArgumentException if {@code passPhrase} does not meet the specified criteria
	 */
	public Server(int port, Receivable receivable, String passPhrase) throws IOException, IllegalArgumentException {
		boolean canBeShort = false;
		for (char c : passPhrase.toCharArray())
			if (!Character.isLetter(c)) {
				canBeShort = true;
				break;
			}
		if (passPhrase.length() >= 6 && canBeShort || passPhrase.length() >= 12) {
			this.PORT_NUM = port;
			this.receivable = receivable;
			this.passPhrase = passPhrase;
			this.ssock = new ServerSocket(this.PORT_NUM);
			new Thread(this, "ServerAccepter").start();
		}
		else
			throw new IllegalArgumentException("Password must be at least 6 characters with symbols/numerals or 12 characters without.");
		final PrintStream con = new PrintStream(System.out);
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				String s = format(String.valueOf((char) b));
				con.print(s);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				String s = format(new String(b, off, len));
				con.print(s);
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		PrintStream p = new PrintStream(out, true);
		System.setOut(p);
		System.setErr(p);
	}

	@Override
	public void run() {
		while (true)
			try {
				Socket s = this.ssock.accept();
				InetAddress address = s.getInetAddress();
				if (!isThrottling(address)) {
					if (s.getInputStream().read() == 48) {
						this.connection = new ServerConnection(s, this.receivable, this.passPhrase);
						Constants.getLogger().severe("Connection is now " + this.connection);
						this.connection.startReceiving();
						this.connection = null;
						s.close();
						Constants.getLogger().info("Dropped connection: Session ended");
					}
					else {
						s.close();
						Constants.getLogger().info("Dropped connection: Invalid opcode (expected 48)");
					}
				}
				else {
					s.close();
					Constants.getLogger().info("Dropped connection: Client is throttling");
				}
			}
			catch (IOException ioe) {
				Constants.getLogger().info("Dropped connection: " + ioe.getMessage());
			}
	}

	/**
	 * Gets the current connection to the client if one exists
	 * 
	 * @return the current connection if one exists, otherwise {@code null}
	 */
	public Connection getConnection() {
		return this.connection;
	}

	private boolean isThrottling(InetAddress address) {
		Long lastConnTime = this.throttlerList.get(address.toString());
		if (lastConnTime == null || System.currentTimeMillis() - lastConnTime.longValue() > MIN_THROTTLING_MS) {
			Constants.getLogger().fine("Adding " + address.toString() + " as a throttling connection.");
			this.throttlerList.put(address.toString(), Long.valueOf(System.currentTimeMillis()));
			return false;
		}
		return true;
	}

	private final String format(String in) {
		return !in.contains(System.getProperty("line.separator")) ? "[" + Constants.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] System: " + in : in;
	}
}