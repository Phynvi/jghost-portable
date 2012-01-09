package org.whired.ghost.net;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.Vars;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.UnhandledPacket;

/**
 * @author Whired
 * 
 * A friendly middle layer between the connection protocols and the stream
 * wrappers. Keeps as much of the more advanced code as hidden as possible.
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
		Vars.getLogger().fine("Running");
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
	protected void startReceiving() {
		Vars.getLogger().fine("Initiating new thread for packet listening..");
		listenerThread = new Thread(new Runnable() {

			public void run() {
				Vars.getLogger().log(Level.FINE, "Listening for incoming packets on {0}", Thread.currentThread().getName());
				while (true) {
					try {
						inputStream.expectingNewPacket = true;
						handlePacket(inputStream.readByte(), inputStream.readByte());
						inputStream.expectingNewPacket = false;
					}
					catch (SocketException e) {
						endSession("Stream corrupt");
						break;
					}
					catch (IOException ioe) {
						break;
					}
					catch (ClassNotFoundException cnfe) {
						endSession("RMI error");
					}
				}
				Vars.getLogger().fine("Thread " + Thread.currentThread().getName() + " is exiting.");
			}
		}, "Packet Receiver");
		listenerThread.start();
	}

	private void handlePacket(int packetId, int length) {
		if (expectingPassword) {
			if (packetId == PacketType.AUTHENTICATION) {
				GhostAuthenticationPacket ap = new GhostAuthenticationPacket();
				ap.receive(this);
				if (ap.password.equals(password)) {
					Vars.getLogger().fine("Password matched, client accepted.");
					expectingPassword = false;
				}
				else {
					endSession("Password incorrect"); // expectingPassword
												// = false;
				}
			}
			else {
				endSession("Password expected, but not received"); // expectingPassword
															// =
															// false;
			}
		}
		else if (packetId == 4) {
			try {
				System.out.println((String) inputStream.readObject());
				Exception e;
				if ((e = (Exception) inputStream.readObject()) != null) {
					Vars.getLogger().warning(e.toString());
					e.printStackTrace();
				}
			}
			catch (Exception ex) {
				Logger.getLogger(Connection.class.getName()).log(Level.WARNING, null, ex);
			}
		}
		else {
			Vars.getLogger().fine("Notifying receivable that external packet " + packetId + " has been received.");
			if (!receivable.handlePacket(packetId, length, this)) {
				new UnhandledPacket(length).receive(this);
				Vars.getLogger().fine("Flushed unhandled packet " + packetId + " with length " + length);
			}
		}
	}

	/**
	 * // TODO fix these messy comments Sends a packet with the specified id
	 * and payload
	 * <p>
	 * An example usage of this method might look like this:
	 * 
	 * <pre>
	 * <code>
	 * sendPacket(77, player.getName(), player.getIP());
	 * // Or
	 * sendPacket(15, player.getName(), player.getSkillLevel(0), player.getSkillLevel(1), player.getSkillLevel(2));
	 * </code>
	 * </pre>
	 * 
	 * Alternatively, {@link org.whired.ghost.net.packet.GhostPacket} can be
	 * extended to make this process easier.
	 * </p>
	 * 
	 * @param packetId the id of the packet to send
	 * @param data the data (in correct order, separated by commas) to send
	 */
	public void sendPacket(int packetId, Object... data) {
		try {
			Vars.getLogger().fine("Sending packet " + packetId + ", which consists of " + data.length + " objects.");
			outputStream.writeByte(packetId);
			outputStream.writeByte(data.length);
			for (Object obj : data) {
				outputStream.writeObject(obj);
			}
		}
		catch (Exception e) {
			Vars.getLogger().warning("Unable to send packet " + packetId + ": " + e.toString());
		}
	}

	/**
	 * Closes and releases the output stream
	 */
	private void removeOutputStream() {
		Vars.getLogger().fine("Removing outputStream. Thread: " + Thread.currentThread().getName());
		this.outputStream.closeStream();
	}

	/**
	 * Closes and releases the input stream
	 */
	private void removeInputStream() {
		Vars.getLogger().fine("Removing inputStream. Thread: " + Thread.currentThread().getName());
		this.inputStream.destruct();
	}

	/**
	 * Sets a password that must be received in order for the session to
	 * continue
	 * 
	 * @param newPass the password that must be matched
	 */
	protected void setPassword(String newPass) {
		this.password = newPass;
		this.expectingPassword = true;
		Vars.getLogger().info("Password request acknowledged. Password is now expected.");
	}

	/**
	 * Called then the session must be terminated
	 */
	protected void endSession(final String reason) {
		Vars.getLogger().log(Level.WARNING, "Termination requested [Reason: {0} - Target: {1}", new Object[] { reason != null ? reason + "]" : "unspecified]", this });
		if (this.manager != null) {
			this.manager.sessionEnded(reason);
		}
		synchronized (this) {
			Vars.getLogger().fine("Lock acquired.");
			removeOutputStream();
			removeInputStream();
			Vars.getLogger().fine("Notifying");
			notify();
			Vars.getLogger().fine("Notified");
			Vars.getLogger().fine("Connection reset: " + reason);
		}
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
