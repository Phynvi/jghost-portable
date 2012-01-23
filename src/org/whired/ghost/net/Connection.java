package org.whired.ghost.net;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;
import org.whired.ghost.net.packet.PacketType;

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
	 * The Receivable to delegate events to.
	 */
	protected Receivable receivable = null;

	/**
	 * The session manager for this connection
	 */
	protected final SessionManager manager;
	
	/**
	 * Creates a new connection with the specified streams and listeners
	 * 
	 * @param inputStream the stream to read information from
	 * @param outputStream the stream to send information to
	 * @param receivable the Receivable to delegate events to
	 * @param manager the session manager for this connection
	 * @throws IOException 
	 */
	public Connection(Socket socket, Receivable receivable, SessionManager manager) throws IOException {
		this.socket = socket;
		this.inputStream = new WrappedInputStream(socket.getInputStream());
		this.outputStream = new WrappedOutputStream(socket.getOutputStream());
		this.receivable = receivable;
		this.manager = manager;
	}

	/**
	 * Creates a new connection with the specified streams and listeners
	 * 
	 * @param inputStream the stream to read information from
	 * @param outputStream the stream to send information to
	 * @param receivable the receivable to delegate events to
	 * @throws IOException 
	 */
	public Connection(Socket socket, Receivable receivable) throws IOException {
		this(socket, receivable, null);
	}

	/**
	 * The Thread to interrupt when listening should stop
	 */
	private Thread listenerThread = null;

	/**
	 * Starts listening for and handling all incoming packets
	 */
	public void startReceiving() {
		Constants.getLogger().fine("Initiating new thread for packet listening..");
		listenerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true)
					try {
						readPacket(inputStream);
					}
					catch (Throwable t) {
						endSession(null);
						t.printStackTrace();
						break;
					}
			}
		}, "PacketReceiver");
		listenerThread.start();
	}

	/**
	 * Read a packet from the stream
	 * @param is the input stream that reads the packet
	 * @throws IOException if the packet id can't be read
	 */
	protected abstract void readPacket(WrappedInputStream inputStream) throws IOException;

	/**
	 * Called then the session must be terminated
	 */
	protected void endSession(final String reason) {
		Constants.getLogger().log(Level.WARNING, "Session ended {0}", reason != null ? reason : "");
		if (this.manager != null)
			this.manager.sessionEnded();
		try {
			socket.close();
		}
		catch (IOException e) {
		}
	}

	/**
	 * Gets the input stream associated with this connection
	 * 
	 * @return the stream to read data from
	 */
	public WrappedInputStream getInputStream() {
		return this.inputStream;
	}

	/**
	 * Gets the output stream associated with this connection
	 * 
	 * @return the stream to send data to
	 */
	public WrappedOutputStream getOutputStream() {
		return this.outputStream;
	}
}
