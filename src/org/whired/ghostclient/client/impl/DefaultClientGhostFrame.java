package org.whired.ghostclient.client.impl;

import java.util.logging.Level;

import org.whired.ghost.constants.Vars;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.GhostClientView;
import org.whired.ghostclient.client.settings.SessionSettings;
import org.whired.ghostclient.client.settings.SettingsFactory;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * @author Whired
 */
public class DefaultClientGhostFrame extends GhostClientFrame {

	public DefaultClientGhostFrame(GhostClientView v, GhostUser user) {
		super(v, user);
	}

	@Override
	public void displayPublicChat(Player sender, String message) {
		getModuleHandler().publicMessageLogged(sender, message);
		new PublicChatPacket(sender, message).send(getSessionManager().getConnection());
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		getModuleHandler().privateMessageLogged(sender, recipient, message);
		new PrivateChatPacket(sender, recipient, message).send(getSessionManager().getConnection());
	}

	@Override
	public void displayDebug(Level level, String message) {
		Vars.getLogger().log(level, message);
	}

	@Override
	public void sessionOpened() {
		getView().sessionOpened();
	}

	@Override
	public void sessionClosed(String reason) {
		Vars.getLogger().log(Level.INFO, "Session closed: {0}", reason);
		getView().sessionClosed(reason);
	}

	@Override
	public void saveSettings() {
		SettingsFactory.saveToDatabase(Vars.getLocalCodebase(), getUser().getSettings());
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
