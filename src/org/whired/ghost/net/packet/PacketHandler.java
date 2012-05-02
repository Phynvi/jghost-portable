package org.whired.ghost.net.packet;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Handles packets
 * @author Whired
 */
public class PacketHandler {
	/*
	 * Packets that have been registered
	 */
	private final HashMap<Integer, GhostPacket> packets = new HashMap<Integer, GhostPacket>();

	/**
	 * Packet listeners that have been registered
	 */
	private final HashMap<Integer, HashSet<PacketListener>> listeners = new HashMap<Integer, HashSet<PacketListener>>();

	/**
	 * Registers a packet
	 * @param packet the packet to register
	 */
	public void registerPacket(final GhostPacket packet, final PacketListener defaultListener) {
		packets.put(packet.getId(), packet);
		addPacketListener(packet.getId(), defaultListener);
	}

	/**
	 * Registers all of the given packets
	 * @param packets the packets to register
	 */
	public void registerPackets(final GhostPacket[] packets) {
		if (packets != null) {
			for (final GhostPacket packet : packets) {
				this.packets.put(packet.getId(), packet);
			}
		}
	}

	public void firePacketReceived(GhostPacket received) {
		HashSet<PacketListener> set = listeners.get(received.getId());
		if (set != null) {
			for (PacketListener l : set) {
				l.packetReceived(received);
			}
		}
		received.isReceived = false;
	}

	/**
	 * Registers a listener for the specified packet id
	 * @param packetId the id of the packet to listen for
	 * @param listener the listener to notify
	 */
	public void addPacketListener(int packetId, PacketListener listener) {
		HashSet<PacketListener> set = listeners.get(packetId);
		if (set == null) {
			set = new HashSet<PacketListener>();
			listeners.put(packetId, set);
		}
		set.add(listener);
	}

	/**
	 * Unregisters a listener for the specified packet id
	 * @param packetId the id of the packet to stop listening for
	 * @param listener the listener to remove
	 */
	public void removePacketListener(int packetId, PacketListener listener) {
		HashSet<PacketListener> set = listeners.get(packetId);
		if (set != null) {
			set.remove(listener);
		}
	}

	/**
	 * Unregisters a packet
	 * @param packet the packet to unregister
	 */
	public void unregisterPacket(final GhostPacket packet) {
		packets.values().remove(packet);
	}

	/**
	 * Unregisters a packet
	 * @param id the id of the packet to unregister
	 */
	public void unregisterPacket(final int id) {
		packets.remove(id);
	}

	/**
	 * Gets the packet that corresponds to the given id
	 * @param id the id of the packet to get
	 * @return the packet that was retrieved
	 */
	public GhostPacket get(final int id) {
		return packets.get(id);
	}
}
