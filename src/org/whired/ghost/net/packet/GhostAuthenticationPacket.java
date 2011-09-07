package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;

/**
 * An authentication packet
 * @author Whired
 */
public class GhostAuthenticationPacket extends GhostPacket
{
	public String password;

	public GhostAuthenticationPacket(Connection connection)
	{
		super(connection, PacketType.AUTHENTICATION);
	}

	public boolean receive()
	{
		try
		{
			password = connection.getInputStream().readString();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
