package org.whired.ghost.net;

/**
 * This interface is used to provide applicable
 * classes with the means to handle information
 * that is received from a network stream.
 *
 * @author Whired
 */
public interface Receivable
{

	/**
	 * Called when information is received
	 *
	 * @param packetId the ID of the packet that was received
	 * @param inputStream the stream from which the packet was received
	 *
	 * @return true if the packet was handled, otherwise false
	 */
	public boolean handlePacket(int packetId, int packetLength, Connection connection);
}
