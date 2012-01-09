package org.whired.ghost.net.reflection;

/**
 * Interface that listens for requests to load packets
 * 
 * @author Whired
 */
public interface PacketLoader {
	public abstract void loadPacket(ReflectionPacketContainer packetContainer);
}
