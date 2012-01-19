package org.whired.ghost.net.packet;


public class AccessorPacket extends GhostPacket {

	public AccessorPacket() {
		super(PacketType.INVOKE_ACCESSOR);
	}
}
