package org.whired.ghost.net;

/**
 * Used to provide applicable
 * the means to handle information
 * that is received from a network stream.
 *
 * @author Whired
 */
public interface Receivable
{

	/**
	 * Invoked when information is received
	 *
	 * @param packetId the ID of the packet that was received
	 * @param packetLength The size of the packet that was received
	 * @param connection The connection that received the packet
	 *
	 * @return true if the packet was handled, otherwise false
	 */
	public boolean handlePacket(int packetId, int packetLength, Connection connection);
}
