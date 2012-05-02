package org.whired.ghost.net.packet;

/**
 * Listens for packets
 * @author Whired
 */
public interface PacketListener {
	/**
	 * Invoked when a packet has been fully received
	 * @param packet the packet that was received
	 */
	void packetReceived(GhostPacket packet);
}
