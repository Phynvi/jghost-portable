import java.awt.Component;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import org.whired.ghost.Constants;
import org.whired.ghost.player.Player;
import org.whired.ghostclient.awt.GhostScrollBarUI;
import org.whired.ghostclient.awt.JAutoScrollPane;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.impl.LinkEventListener;
import org.whired.ghostclient.client.impl.LinkingJTextPane;
import org.whired.ghostclient.client.module.Module;

/**
 * A default public chat module
 * 
 * @author Whired
 */
public class PublicChatModule extends LinkingJTextPane implements Module {

	private GhostClientFrame frame;
	private final String name = "Chat";
	private final JAutoScrollPane scrollPane = new JAutoScrollPane();

	private final LinkEventListener linkListener = new LinkEventListener() {

		@Override
		public void linkClicked(String linkText) {
			frame.getView().setInputText("/pm " + linkText + " ");
			frame.getView().focusInputBox();
		}
	};
	private final GhostEventAdapter ghostEventListener = new GhostEventAdapter() {

		@Override
		public void playerAdded(Player player) {
			Constants.getLogger().info("Adding match: " + player.getName());
			addMatch(player.getName());
		}

		@Override
		public void playerRemoved(Player player) {
			removeMatch(player.getName());
		}

		@Override
		public void publicMessageLogged(final Player from, final String message) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollPane.autoscrollNext();
					Icon i = frame.getRankHandler().rankForLevel(from.getRights()).getIcon();
					try {
						Style iconOnly = getStyledDocument().getStyle("iconOnly");
						StyleConstants.setIcon(iconOnly, i);
						getStyledDocument().insertString(getStyledDocument().getLength(), "[" + Constants.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] ", null);
						getStyledDocument().insertString(getStyledDocument().getLength(), " ", iconOnly);
						StringBuilder b = new StringBuilder().append(from.getName()).append(": ").append(message).append("\n");
						getStyledDocument().insertString(getStyledDocument().getLength(), b.toString(), null);
						frame.getView().displayModuleNotification(PublicChatModule.this);
					}
					catch (Exception e) {
						Logger.getLogger(Module.class.getName()).log(Level.SEVERE, "Unable to display chat:", e);
					}
				}
			});
		}
	};

	public PublicChatModule() {
		super(false);
		((DefaultCaret) this.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		this.setEditable(false);
		this.addLinkEventListener(linkListener);
		this.setOpaque(false);
		this.getStyledDocument().addStyle("iconOnly", null);
		scrollPane.setViewportView(this);
		Border border = BorderFactory.createEmptyBorder();
		this.setBorder(border);
		scrollPane.setBorder(border);
		scrollPane.setViewportBorder(border);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUI(new GhostScrollBarUI(scrollPane.getVerticalScrollBar()));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
	}
	
	@Override
	public Component getComponent() {
		return this.scrollPane;
	}

	@Override
	public void setFrame(GhostClientFrame frame) {
		this.frame = frame;
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return ghostEventListener;
	}

	@Override
	public String getModuleName() {
		return this.name;
	}

	@Override
	public void load() {
	}

	@Override
	public void setResourcePath(String path) {
	}
}
