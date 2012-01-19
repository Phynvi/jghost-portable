package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;

/**
 * Used for transferring log messages
 * 
 * @author Whired
 */
public class DebugPacket extends GhostPacket {
	public int level;
	public String message;

	public DebugPacket() {
		super(PacketType.DEBUG_MESSAGE);
		setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedInputStream is = connection.getInputStream();
					level = is.readInt();
					message = is.readString();
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}

	public DebugPacket(int level, String message) {
		this();
		this.level = level;
		this.message = message;
		setSendAction(new TransmitAction() {

			@Override
			public boolean onTransmit(Connection connection) {
				try {
					WrappedOutputStream os = connection.getOutputStream();
					os.writeInt(DebugPacket.this.level);
					os.writeString(DebugPacket.this.message);
					return true;
				}
				catch (IOException e) {
					return false;
				}
			}
		});
	}
}
