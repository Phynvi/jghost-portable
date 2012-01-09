package org.whired.ghost.net;

import java.util.HashMap;
import org.whired.ghost.net.packet.GhostPacket;

/**
 * Handles packets
 * 
 * @author Whired
 */
public class PacketHandler {
	/*
	 * Packets that have been registered
	 */
	private HashMap<Integer, GhostPacket> packets = new HashMap<Integer, GhostPacket>();

	/**
	 * Registers a packet
	 * 
	 * @param packet the packet to register
	 */
	public void registerPacket(GhostPacket packet) {
		packets.put(packet.getId(), packet);
	}

	/**
	 * Registers all of the given packets
	 * 
	 * @param packets the packets to register
	 */
	public void registerPackets(GhostPacket[] packets) {
		if (packets != null) {
			for (GhostPacket packet : packets) {
				this.packets.put(packet.getId(), packet);
			}
		}
	}

	/**
	 * Unregisters a packet
	 * 
	 * @param packet the packet to unregister
	 */
	public void unregisterPacket(GhostPacket packet) {
		packets.values().remove(packet);
	}

	/**
	 * Unregisters a packet
	 * 
	 * @param id the id of the packet to unregister
	 */
	public void unregisterPacket(int id) {
		packets.remove(id);
	}

	/**
	 * Gets the packet that corresponds to the given id
	 * 
	 * @param id the id of the packet to get
	 * @return the packet that was retrieved
	 */
	public GhostPacket get(int id) {
		return packets.get(id);
	}
}
