package org.whired.ghost.net.packet;

import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;

/**
 *
 * @author Whired
 */
public class PublicChatPacket extends GhostChatPacket
{
	/**
	 * Creates a new public chat packet on the connection specified
	 * @param connection the connection to transfer the packet
	 */
	public PublicChatPacket(Connection connection)
	{
		super(connection, PacketType.PUBLIC_CHAT);
	}

	public boolean receive()
	{
		try
		{
			this.sender = (Player) connection.getInputStream().readObject();
			this.message = connection.getInputStream().readString();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean send(Player sender, String message)
	{
		this.sender = sender;
		this.message = message;
		return sendUnchecked(sender, message);
	}

	public boolean send(Player sender, byte[] chatText, int chatTextSize)
	{
		this.sender = sender;
		this.message = unpackMessage(chatText, chatTextSize);
		return sendUnchecked(sender, message);
	}
}
