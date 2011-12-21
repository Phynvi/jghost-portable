package org.whired.ghostclient.client.impl.module;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghostclient.client.ClientGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.impl.LinkEventListener;
import org.whired.ghostclient.client.impl.LinkingJTextPane;
import org.whired.ghostclient.client.module.Module;

/**
 * A default public chat module
 * @author Whired
 */
public class PublicChatModule extends LinkingJTextPane implements Module {

	private ClientGhostFrame frame;
	private final GhostEventAdapter listener = new GhostEventAdapter() {

		@Override
		public void publicMessageLogged(Player from, String message) {
			Icon i = frame.getRankHandler().rankForLevel(from.getRights()).getIcon();
			try {
				Style iconOnly = getStyledDocument().getStyle("iconOnly");
				if (iconOnly == null) {
					iconOnly = getStyledDocument().addStyle("iconOnly", null);
				}
				StyleConstants.setIcon(iconOnly, i);
				getStyledDocument().insertString(getStyledDocument().getLength(), " ", iconOnly);
				StyleConstants.setBold(getInputAttributes(), true);
				getStyledDocument().insertString(getStyledDocument().getLength(), from.getName(), getInputAttributes());
				getStyledDocument().insertString(getStyledDocument().getLength(), ": " + message + "\n", null);
			}
			catch (Exception e) {
				Logger.getLogger(Module.class.getName()).log(Level.SEVERE, "Unable to display chat:", e);
			}
		}
	};

	public PublicChatModule(final ClientGhostFrame frame) {
		super(false);
		setEditable(false);
		this.frame = frame;
		LinkEventListener l = new LinkEventListener() {

			@Override
			public void linkClicked(String linkText) {

				frame.getView().setInputText("/pm " + linkText + " ");
			}
		};
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void moduleActivated() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void moduleDeactivated() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setFrame(ClientGhostFrame frame) {
		this.frame = frame;
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return listener;
	}

	@Override
	public String getModuleName() {
		return "newchat";
	}
}
