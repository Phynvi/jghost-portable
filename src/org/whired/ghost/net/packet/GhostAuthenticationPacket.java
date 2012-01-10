package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;

/**
 * An authentication packet
 * 
 * @author Whired
 */
public class GhostAuthenticationPacket extends GhostPacket {

	public String password;

	public GhostAuthenticationPacket() {
		super(PacketType.AUTHENTICATION);
	}

	@Override
	public boolean receive(Connection connection) {
		try {
			password = connection.getInputStream().readString();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
}
