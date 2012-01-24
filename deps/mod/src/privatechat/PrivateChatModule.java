import java.awt.Component;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.whired.ghost.Constants;
import org.whired.ghost.player.Player;
import org.whired.ghostclient.awt.GhostScrollBarUI;
import org.whired.ghostclient.awt.JAutoScrollPane;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.impl.LinkEventListener;
import org.whired.ghostclient.client.impl.LinkingJTextPane;
import org.whired.ghostclient.client.module.Module;

public class PrivateChatModule extends LinkingJTextPane implements Module {

	private GhostClientFrame frame;
	private final JAutoScrollPane scrollPane = new JAutoScrollPane();

	private final LinkEventListener linkListener = new LinkEventListener() {

		@Override
		public void linkClicked(String linkText) {
			frame.getView().setInputText("/pm " + linkText + " ", true);
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
		public void privateMessageLogged(final Player from, final Player to, final String message) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPane.autoscrollNext();
					try {
						StyledDocument doc = getStyledDocument();
						Style iconOnly = doc.getStyle("iconOnly");
						getStyledDocument().insertString(getStyledDocument().getLength(), "[" + Constants.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] ", null);
						if (from.getRights() > 0) {
							StyleConstants.setIcon(iconOnly, frame.getRankHandler().rankForLevel(from.getRights()).getIcon());
							doc.insertString(doc.getLength(), " ", iconOnly);
						}
						doc.insertString(doc.getLength(), from.getName() + " to ", null);
						if (to.getRights() > 0) {
							StyleConstants.setIcon(iconOnly, frame.getRankHandler().rankForLevel(to.getRights()).getIcon());
							doc.insertString(doc.getLength(), " ", iconOnly);
						}
						doc.insertString(doc.getLength(), to.getName() + ": " + message + "\n", null);
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
			
		}
	};

	public PrivateChatModule() {
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
	public String getModuleName() {
		return "PM";
	}

	@Override
	public Component getComponent() {
		return scrollPane;
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
	public void setResourcePath(String path) {
	}

	@Override
	public void load() {
	}

}
