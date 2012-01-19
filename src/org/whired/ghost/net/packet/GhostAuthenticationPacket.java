package org.whired.ghost.net.packet;

import java.io.IOException;

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
		this.setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					password = connection.getInputStream().readString();
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	public GhostAuthenticationPacket(String password) {
		super(PacketType.AUTHENTICATION);
		this.password = password;
		this.setSendAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					connection.getOutputStream().writeString(GhostAuthenticationPacket.this.password);
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}
}
