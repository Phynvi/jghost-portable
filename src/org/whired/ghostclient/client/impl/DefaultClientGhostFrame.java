package org.whired.ghostclient.client.impl;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.ModeratePacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.RankManager;
import org.whired.ghostclient.client.GhostClientView;
import org.whired.ghostclient.client.LocalGhostFrame;
import org.whired.ghostclient.client.settings.SessionSettings;
import org.whired.ghostclient.client.settings.SettingsFactory;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * @author Whired
 */
public class DefaultClientGhostFrame extends LocalGhostFrame {

	public DefaultClientGhostFrame(final GhostClientView view, final GhostUser user) {
		super(view, user);
	}

	public DefaultClientGhostFrame(final GhostClientView view, final GhostUser user, final RankManager rankManager) {
		super(view, user, rankManager);
	}

	@Override
	public void displayPublicChat(final Player sender, final String message) {
		getModuleManager().publicMessageLogged(sender, message);
	}

	@Override
	public void displayPrivateChat(final Player sender, final Player recipient, final String message) {
		getModuleManager().privateMessageLogged(sender, recipient, message);
	}

	@Override
	public void displayDebug(final Level level, final String message) {
		Constants.getLogger().log(level, message);
	}

	@Override
	public void sessionOpened() {
		getView().sessionOpened();
	}

	@Override
	public void sessionClosed() {
		getPlayerList().removeAll();
		getView().sessionClosed();
	}

	@Override
	public void saveSessionSettings() {
		SettingsFactory.saveToDatabase(Constants.getLocalCodebase(), getUser().getSettings());
	}

	@Override
	public void restartServer() {
	}

	@Override
	public Player getUserPlayer() {
		return getUser().getSettings().getPlayer();
	}

	@Override
	public SessionSettings getSessionSettings() {
		return getUser().getSettings();
	}

	@Override
	public void moderatePlayer(String playerName, int operation) {
		new ModeratePacket(playerName, operation).send(getSessionManager().getConnection());
	}

}
