package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;

public interface TransmitAction {
	/**
	 * The actions that will be performed when a packet needs to be transmitted
	 * @param connection the connection that is requesting the transmission
	 * @return {@code true} if the operation was successful, otherwise {@code false}
	 */
	public boolean onTransmit(Connection connection);
}
