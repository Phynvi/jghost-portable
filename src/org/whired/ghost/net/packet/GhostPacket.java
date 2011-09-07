package org.whired.ghost.net.packet;

import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;

/**
 * The layout a standard packet
 * @author Whired
 */
public abstract class GhostPacket {

	/** The connection that will handle data transfers */
	protected final Connection connection;

	/** The ID of this packet */
	protected final int id;

	/**
	 * Creates a new packet on the specified connection with the specified id
	 *
	 * @param connection the connection that will handle data transfer
	 * @param id the id of this packet
	 */
	public GhostPacket(Connection connection, int id)
	{
		this.connection = connection;
		this.id = id;
	}

	/**
	 * Sends a payload with a checked exception
	 *
	 * @param payload the payload to send
	 * @throws Exception the exception that was caught
	 */
	public void sendChecked(Object... payload) throws Exception
	{
		try
		{
			connection.sendPacket(id, payload);
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	/**
	 * Sends a payload without checking for exceptions
	 *
	 * @param payload the payload to send
	 * @return {@code true} if no exception was thrown, otherwise {@code false}
	 */
	public boolean sendUnchecked(Object... payload)
	{
		try
		{
			connection.sendPacket(id, payload);
			return true;
		}
		catch(Exception e)
		{
			if(connection == null)
				Vars.getLogger().warning("Dropped packet (no connection)");
			else
				Vars.getLogger().warning("Dropped packet: "+e.toString());
			return false;
		}
	}

	/**
	 * Called upon instantiation in order to receive and marshal the packet
	 * @return {@code true} if the packet was successfully received,
	 *	otherwise {@code false}
	 */
	public abstract boolean receive();
}