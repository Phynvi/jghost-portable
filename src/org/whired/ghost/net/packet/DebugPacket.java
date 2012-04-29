package org.whired.ghost.net.packet;

import java.io.IOException;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.WrappedInputStream;
import org.whired.ghost.net.WrappedOutputStream;

/**
 * Used for transferring log messages
 * @author Whired
 */
public class DebugPacket extends GhostPacket {
	public int level;
	public String message;

	public DebugPacket() {
		super(PacketType.DEBUG_MESSAGE);
		setReceiveAction(new TransmitAction() {

			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					final WrappedInputStream is = connection.getInputStream();
					level = is.readInt();
					message = is.readString();
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}

	public DebugPacket(final int level, final String message) {
		this();
		this.level = level;
		this.message = message;
		setSendAction(new TransmitAction() {

			@Override
			public boolean onTransmit(final Connection connection) {
				try {
					final WrappedOutputStream os = connection.getOutputStream();
					os.writeInt(DebugPacket.this.level);
					os.writeString(DebugPacket.this.message);
					return true;
				}
				catch (final IOException e) {
					return false;
				}
			}
		});
	}
}
