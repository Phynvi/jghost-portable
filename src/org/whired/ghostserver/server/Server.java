package org.whired.ghostserver.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.SessionManager;
import org.whired.ghost.net.packet.PacketHandler;
import org.whired.ghostserver.server.io.ServerConnection;

public class Server implements Runnable {

	private final int PORT_NUM;
	private static final long MIN_THROTTLING_MS = 25000;
	private final HashMap<String, Long> throttlerList = new HashMap<String, Long>();
	private ServerSocket ssock;
	private final String passPhrase;
	private Connection connection;
	private final SessionManager sessionManager = new SessionManager();
	private final PacketHandler packetHandler = new PacketHandler();

	/**
	 * Creates a new ghost server on the default port of {@code 43596} with the specified passphrase
	 * @param passPhrase required by any connecting ghost clients. Must be at least 6 characters with symbols/numerals or 12 characters without
	 * @throws IOException if the server cannot be initialized
	 * @throws IllegalArgumentException if {@code passPhrase} does not meet the specified criteria
	 */
	public Server(final String passPhrase) throws IOException, IllegalArgumentException {
		this(43596, passPhrase);
	}

	/**
	 * Creates a new ghost server on the specified port
	 * @param port the port the server will listen on
	 * @param passPhrase required by any connecting ghost clients. Must be at least 6 characters with symbols/numerals or 12 characters without
	 * @throws IOException if the server cannot be initialized
	 * @throws IllegalArgumentException if {@code passPhrase} does not meet the specified criteria
	 */
	public Server(final int port, final String passPhrase) throws IOException, IllegalArgumentException {
		boolean canBeShort = false;
		for (final char c : passPhrase.toCharArray()) {
			if (!Character.isLetter(c)) {
				canBeShort = true;
				break;
			}
		}
		if (passPhrase.length() >= 6 && canBeShort || passPhrase.length() >= 12) {
			this.PORT_NUM = port;
			this.passPhrase = passPhrase;
			this.ssock = new ServerSocket(this.PORT_NUM, 50);
			Constants.getLogger().info("Ghost server is running.");
			new Thread(this, "ClientAccepter").start();
		}
		else {
			Constants.getLogger().severe("Ghost server not started:");
			throw new IllegalArgumentException("Password must be at least 6 characters with symbols/numerals or 12 characters without.");
		}
		final PrintStream con = new PrintStream(System.out);
		final OutputStream out = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				final String s = format(String.valueOf((char) b));
				con.print(s);
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				final String s = format(new String(b, off, len));
				con.print(s);
			}

			@Override
			public void write(final byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		final PrintStream p = new PrintStream(out, true);
		System.setOut(p);
		System.setErr(p);
	}

	@Override
	public void run() {
		while (true) {
			try {
				final Socket s = this.ssock.accept();
				final InetAddress address = s.getInetAddress();
				s.setSoTimeout(400);
				if (!isThrottling(address)) {
					throttlerList.remove(address);
					s.getOutputStream().write(1);
					this.connection = new ServerConnection(s, this.passPhrase, sessionManager, packetHandler);
					this.connection.startReceiving();
					this.connection = null;
					s.close();
				}
				else {
					System.out.println("Connection is throttling!");
					s.getOutputStream().write(0);
					s.shutdownOutput();
					s.close();
				}
			}
			catch (final IOException ioe) {
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

	public SessionManager getSessionManager() {
		return this.sessionManager;
	}

	public PacketHandler getPacketHandler() {
		return this.packetHandler;
	}

	private boolean isThrottling(final InetAddress address) {
		final boolean toReturn = addAndCheck(address.toString());
		if (throttlerList.size() > 50) {
			final Iterator<String> it = throttlerList.keySet().iterator();
			while (it.hasNext()) {
				final String key = it.next();
				if (hasExpired(key)) {
					it.remove();
				}
			}
		}
		return toReturn;
	}

	boolean hasExpired(final String address) {
		final Long lastConnTime = this.throttlerList.get(address);
		if (lastConnTime == null || System.currentTimeMillis() - lastConnTime.longValue() > MIN_THROTTLING_MS) {
			return true;
		}
		return false;
	}

	private boolean addAndCheck(final String address) {
		if (hasExpired(address)) {
			this.throttlerList.put(address.toString(), Long.valueOf(System.currentTimeMillis()));
			return false;
		}
		return true;
	}

	private final String format(final String in) {
		return !in.contains(System.getProperty("line.separator")) ? "[" + Constants.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] System: " + in : in;
	}
}