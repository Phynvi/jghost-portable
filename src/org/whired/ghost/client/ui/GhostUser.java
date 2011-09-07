package org.whired.ghost.client.ui;

import org.whired.ghost.client.util.DataSave;
import org.whired.ghost.net.Receivable;

/**
 * The layout for any implements of {@code GhostUser}
 * @author Whired
 */
public interface GhostUser extends Receivable {

	/**
	 * Gets the settings for this user
	 *
	 * @return the settings associated with this user
	 */
	public DataSave getSettings();
}
