package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;

/**
 * An unhandled packet
 * @author Whired
 */
public class UnhandledPacket extends GhostPacket {

	private final int length;

	public UnhandledPacket(Connection connection, int length)
	{
		super(connection, PacketType.UNHANDLED);
		this.length = length;
	}

	public boolean receive()
	{
		try
		{
			for(int i=0; i<length; i++)
			{
				connection.getInputStream().readObject();
			}
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
