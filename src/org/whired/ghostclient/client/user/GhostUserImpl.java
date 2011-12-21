package org.whired.ghostclient.client.user;

// TODO add descrip
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghostclient.client.user.SessionSettings;

public class GhostUserImpl implements GhostUser {
	
	/**
	 * Provides access to the graphical client
	 */
	private final GhostFrame frame;
	/**
	 * The settings for this user
	 */
	private final SessionSettings settings;
	/**
	 * Constructs a new GhostUser
	 * 
	 * @param frame the <code>GhostFrame</code> that provides access to 
	 * the graphical client
	 */
	public GhostUserImpl(GhostFrame frame, SessionSettings settings) {
		this.frame = frame;
		this.frame.setUser(this);
		this.settings = settings;
	}

	public GhostFrame getFrame() {
		return this.frame;
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
	public boolean handlePacket(int packetId, int packetLength, org.whired.ghost.net.Connection connection) {
		switch (packetId) {
			case 0:
				break; // Internal use // TODO use constants
			case 2:
				break; // Internal use
			case 4:
				break; // Internal use
			case 1:
				break; // Internal use
			default:
				return false; // If unhandled, return false so packet is disposed
		}
		return true;
	}

	/**
	 * Gets the settings for this user
	 *
	 * @return the settings associated with this user
	 */
	public SessionSettings getSettings() {
		return this.settings;
	}
}
