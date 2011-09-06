package org.whired.ghostclient.ui;

// TODO add descrip
import org.whired.ghost.client.ui.GhostFrame;
import org.whired.ghost.client.ui.GhostUser;
import org.whired.ghost.client.util.DataSave;

public class GhostUserImpl implements GhostUser
{
	/** Provides access to the graphical client */
	private final GhostFrame frame;

	/** The settings for this user */
	private final DataSave settings;
	
	/**
	 * Constructs a new GhostUser
	 * 
	 * @param frame the <code>GhostFrame</code> that provides access to 
	 * the graphical client
	 */
	public GhostUserImpl(GhostFrame frame)
	{
		this.frame = frame;
		this.frame.setUser(this);
		this.settings = DataSave.getSettings();
	}

	/**
	 * Called when an external packet is received
	 *
	 * @param packetId the id of the packet that was received
	 * @param connection the connection that received the packet
	 * 
	 * @return {@code true} if the packet was handled, otherwise {@code false}
	 *	so it can be disposed of properly
	 */
	public boolean handlePacket(int packetId, int packetLength, org.whired.ghost.net.Connection connection)
	{
		switch(packetId)
		{
			case 0: break; // Internal use // TODO use constants
			case 2: break; // Internal use
			case 4: break; // Internal use
			case 1: break; // Internal use
			default: return false; // If unhandled, return false so packet is disposed
		}
		return true;
	}

	/**
	 * Gets the settings for this user
	 *
	 * @return the settings associated with this user
	 */
	public DataSave getSettings()
	{
		return this.settings;
	}
}
