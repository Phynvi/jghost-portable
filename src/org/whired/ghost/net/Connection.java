package org.whired.ghost.net;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.DebugPacket;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;
import org.whired.ghost.net.packet.PacketType;

/**
 * @author Whired A friendly middle layer between the connection protocols and the stream wrappers. Keeps as much of the more advanced code as hidden as possible.
 */
public abstract class Connection {

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
	private Receivable receivable = null;
	/**
	 * The password that must be received before client is accepted.
	 */
	private String password = "";
	/**
	 * Specifies whether or not client must send password.
	 */
	private boolean expectingPassword = false;
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
	 */
	public Connection(WrappedInputStream inputStream, WrappedOutputStream outputStream, Receivable receivable, SessionManager manager) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.receivable = receivable;
		this.manager = manager;
		this.inputStream.setDisconnectCallback(new DisconnectCallback() {

			@Override
			public void disconnected(String reason) {
				endSession(reason);
			}
		});
		Constants.getLogger().fine("Running");
	}

	protected interface DisconnectCallback {
		public void disconnected(String reason);
	}

	/**
	 * Creates a new connection with the specified streams and listeners
	 * 
	 * @param inputStream the stream to read information from
	 * @param outputStream the stream to send information to
	 * @param receivable the receivable to delegate events to
	 */
	public Connection(WrappedInputStream inputStream, WrappedOutputStream outputStream, Receivable receivable) {
		this(inputStream, outputStream, receivable, null);
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
				Constants.getLogger().log(Level.FINE, "Listening for incoming packets on {0}", Thread.currentThread().getName());
				while (true)
					try {
						inputStream.expectingNewPacket = true;
						handlePacket(inputStream.readByte());
						inputStream.expectingNewPacket = false;
					}
					catch (SocketException e) {
						endSession("Stream corrupt");
						break;
					}
					catch (IOException ioe) {
						break;
					}
				Constants.getLogger().fine("Thread " + Thread.currentThread().getName() + " is exiting.");
			}
		}, "Packet Receiver");
		listenerThread.start();
	}

	private void handlePacket(int packetId) {
		Constants.getLogger().log(Level.FINE, "Received packet {0}", packetId);
		if (expectingPassword) {
			if (packetId == PacketType.AUTHENTICATION) {
				GhostAuthenticationPacket ap = new GhostAuthenticationPacket();
				if (ap.receive(this))
					if (ap.password.equals(password)) {
						Constants.getLogger().fine("Password matched, client accepted.");
						try {
							getOutputStream().writeByte(PacketType.AUTHENTICATE_SUCCESS);
						}
						catch (IOException e) {
						}
						expectingPassword = false;
					}
					else
						endSession("Password incorrect");
			}
			else
				endSession("Password expected, but not received");
		}
		else {
			Constants.getLogger().fine("Notifying receivable that external packet " + packetId + " has been received.");
			try {
				if (!receivable.handlePacket(packetId, this))
					endSession("Packet " + packetId + " was not handled");
			}
			catch (IOException e) {
				endSession("Error while handling packet " + packetId);
			}
		}
	}

	/**
	 * Closes and releases the output stream
	 */
	private void removeOutputStream() {
		Constants.getLogger().fine("Removing outputStream. Thread: " + Thread.currentThread().getName());
		this.outputStream.closeStream();
	}

	/**
	 * Closes and releases the input stream
	 */
	private void removeInputStream() {
		Constants.getLogger().fine("Removing inputStream. Thread: " + Thread.currentThread().getName());
		this.inputStream.destruct();
	}

	/**
	 * Sets a password that must be received in order for the session to continue
	 * 
	 * @param newPass the password that must be matched
	 */
	protected void setPassword(String newPass) {
		this.password = newPass;
		this.expectingPassword = true;
		Constants.getLogger().info("Password request acknowledged. Password is now expected.");
	}

	/**
	 * Called then the session must be terminated
	 */
	protected void endSession(final String reason) {
		new DebugPacket(Level.WARNING.intValue(), reason == null ? "Unspecified" : reason).send(this);
		Constants.getLogger().log(Level.WARNING, "Termination requested [Reason: {0} - Target: {1}", new Object[] { reason != null ? reason + "]" : "unspecified]", this });
		if (this.manager != null)
			this.manager.sessionEnded(reason);
		removeOutputStream();
		removeInputStream();
		Constants.getLogger().fine("Connection reset: " + reason);
	}

	public void setEnforceTimeout(boolean b) {
		this.inputStream.enforceTimeout = b;
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
