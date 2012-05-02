package org.whired.ghost.net;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketHandler;

/**
 * @author Whired A friendly middle layer between the connection protocols and the stream wrappers. Keeps as much of the more advanced code as hidden as possible.
 */
public abstract class Connection {

	/**
	 * The socket that contains the underlying streams
	 */
	protected final Socket socket;
	/**
	 * The WrappedInputStream to be utilized.
	 */
	private final WrappedInputStream inputStream;
	/**
	 * The WrappedOutputStream to be utilized.
	 */
	private final WrappedOutputStream outputStream;

	/**
	 * The session manager for this connection
	 */
	protected final SessionManager manager;

	/**
	 * The packet handler that tells this connection how to handle a packet
	 */
	private final PacketHandler handler;

	/**
	 * Creates a new connection with the specified streams and listeners
	 * 
	 * @param socket the raw socket this connection uses
	 * @param manager the session manager for this connection
	 * @param handler the packet handler that tells this connection how to handle a packet
	 * @throws IOException
	 */
	public Connection(final Socket socket, final SessionManager manager, final PacketHandler handler) throws IOException {
		this.socket = socket;
		this.inputStream = new WrappedInputStream(socket.getInputStream());
		this.outputStream = new WrappedOutputStream(socket.getOutputStream());
		this.manager = manager;
		this.handler = handler;
	}

	/**
	 * Starts listening for and handling all incoming packets
	 */
	public void startReceiving() {
		while (true) {
			try {
				readPacket(inputStream);
			}
			catch (final Throwable t) {
				endSession(null);
				break;
			}
		}
	}

	/**
	 * Read a packet from the stream
	 * @param is the input stream that reads the packet
	 * @throws IOException if the packet id can't be read
	 */
	protected void readPacket(WrappedInputStream inputStream) throws IOException {
		final int packetId = inputStream.readByte();
		Constants.getLogger().fine("Notifying receivable that external packet " + packetId + " has been received."); // TODO chmsg
		GhostPacket p = handler.get(packetId);
		if (p != null && p.receive(this)) {
			handler.firePacketReceived(p);
		}
		else {
			endSession("Packet " + packetId + " was not handled");
		}
	}

	/**
	 * Called then the session must be terminated
	 */
	protected void endSession(final String reason) {
		Constants.getLogger().log(Level.WARNING, "Session ended {0}", reason != null ? reason : "");
		if (this.manager != null) {
			this.manager.sessionEnded();
		}
		try {
			socket.close();
		}
		catch (final IOException e) {
		}
	}

	/**
	 * Gets the input stream associated with this connection
	 * @return the stream to read data from
	 */
	public WrappedInputStream getInputStream() {
		return this.inputStream;
	}

	/**
	 * Gets the output stream associated with this connection
	 * @return the stream to send data to
	 */
	public WrappedOutputStream getOutputStream() {
		return this.outputStream;
	}
}
