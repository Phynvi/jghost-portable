package org.whired.ghostclient.client.impl;

import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.RankHandler;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.GhostClientView;
import org.whired.ghostclient.client.settings.SessionSettings;
import org.whired.ghostclient.client.settings.SettingsFactory;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * @author Whired
 */
public class DefaultClientGhostFrame extends GhostClientFrame {

	public DefaultClientGhostFrame(final GhostClientView view, final GhostUser user) {
		super(view, user);
	}

	public DefaultClientGhostFrame(final GhostClientView view, final GhostUser user, final RankHandler rankHandler) {
		super(view, user, rankHandler);
	}

	@Override
	public void displayPublicChat(final Player sender, final String message) {
		getModuleHandler().publicMessageLogged(sender, message);
		new PublicChatPacket(sender, message).send(getSessionManager().getConnection());
	}

	@Override
	public void displayPrivateChat(final Player sender, final Player recipient, final String message) {
		getModuleHandler().privateMessageLogged(sender, recipient, message);
		new PrivateChatPacket(sender, recipient, message).send(getSessionManager().getConnection());
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
	public void saveSettings() {
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
	public SessionSettings getSettings() {
		return getUser().getSettings();
	}

}
