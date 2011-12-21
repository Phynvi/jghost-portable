package org.whired.ghostclient.client.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.Rank;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;
import org.whired.ghostclient.client.ClientGhostFrame;
import org.whired.ghostclient.client.user.SessionSettings;

/**
 *
 * @author Whired
 */
public class DefaultClientGhostFrame extends ClientGhostFrame {

	@Override
	protected void bindPacket(ReflectionPacketContainer container) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void displayPublicChat(Player sender, String message) {
		getModuleHandler().publicMessageLogged(sender, message);
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		getModuleHandler().privateMessageLogged(sender, recipient, message);
	}

	@Override
	public void displayDebug(Level level, String message) {
		Logger.getLogger(DefaultClientGhostFrame.class.getName()).log(level, message);
	}

	@Override
	public void sessionOpened() {
		Logger.getLogger(DefaultClientGhostFrame.class.getName()).info("Session opened");
		getView().sessionOpened();
	}

	@Override
	public void sessionClosed(String reason) {
		Logger.getLogger(DefaultClientGhostFrame.class.getName()).log(Level.INFO, "Session closed: {0}", reason);
		getView().sessionClosed(reason);
	}

	@Override
	public Rank getRankForPlayer(Player player) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void saveSettings() {
		try {
			SessionSettings.saveToDisk(getUser().getSettings());
		}
		catch (Exception ex) {
			Logger.getLogger(DefaultClientGhostFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void restartServer() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Player getUserPlayer() {
		return getUser().getSettings().getPlayer();
	}

}