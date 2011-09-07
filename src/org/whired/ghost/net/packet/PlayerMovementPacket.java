package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;

/**
 * An movement packet
 * @author Whired
 */
public class PlayerMovementPacket extends GhostPacket
{
	public String playerName;
	public int newAbsX;
	public int newAbsY;

	public PlayerMovementPacket(Connection connection)
	{
		super(connection, PacketType.PLAYER_MOVEMENT);
	}

	public boolean receive()
	{
		try
		{
			this.playerName = connection.getInputStream().readString();
			this.newAbsX = connection.getInputStream().readInt();
			this.newAbsY = connection.getInputStream().readInt();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean send(String name, int newAbsX, int newAbsY)
	{
		this.playerName = name;
		this.newAbsX = newAbsX;
		this.newAbsY = newAbsY;
		return sendUnchecked(name, newAbsX, newAbsY);
	}
}

