package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;

/**
 * A packet that has no body, only an opcode
 * @author Whired
 */
public class BodylessPacket extends GhostPacket {
	public BodylessPacket(int id) {
		super(id);
		this.setReceiveAction(new TransmitAction() {
			@Override
			public boolean onTransmit(Connection connection) {
				return true;
			}
		});
	}
}
