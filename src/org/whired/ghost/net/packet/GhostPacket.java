package org.whired.ghost.net.packet;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection;

/**
 * The layout a standard packet
 * 
 * @author Whired
 */
public class GhostPacket {

	/**
	 * The ID of this packet
	 */
	protected final int id;
	/**
	 * Whether or not this packet has been received
	 */
	private boolean isReceived;
	/**
	 * The receivable action
	 */
	private TransmitAction onReceive;
	/**
	 * The sendable action
	 */
	private TransmitAction onSend;

	/**
	 * Creates a new packet on the specified connection with the specified id
	 * 
	 * @param connection the connection that will handle data transfer
	 * @param id the id of this packet
	 */
	public GhostPacket(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setSendAction(TransmitAction action) {
		this.onSend = action;
	}

	public void setReceiveAction(TransmitAction action) {
		this.onReceive = action;
	}

	public final boolean receive(Connection connection) throws IllegalStateException {
		if (!isReceived && onReceive != null) {
			isReceived = true;
			return onReceive.onTransmit(connection);
		}
		else if (isReceived)
			throw new IllegalStateException("This packet has already been received");
		return false;
	}

	public final boolean send(Connection connection) {
		if (onSend != null)
			try {
				if (connection != null) {
					connection.getOutputStream().writeByte(id);
					return onSend.onTransmit(connection);
				}
				else
					Constants.getLogger().log(Level.WARNING, "Packet {0} not sent: No connection to remote", id);
			}
			catch (Throwable t) {
				Constants.getLogger().log(Level.WARNING, "Packet {0} not sent: {1}", new Object[] { id, t });
				Constants.getLogger().log(Level.FINE, "Exception details: ", t);
				return false;
			}
		return false;
	}

}
