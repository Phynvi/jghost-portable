import java.awt.Component;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.whired.ghost.Constants;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghostclient.awt.GhostScrollBarUI;
import org.whired.ghostclient.awt.JAutoScrollPane;
import org.whired.ghostclient.awt.LinkEventListener;
import org.whired.ghostclient.awt.LinkingJTextPane;
import org.whired.ghostclient.client.LocalGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;

public class PrivateChatModule extends LinkingJTextPane implements Module {

	private LocalGhostFrame frame;
	private final JAutoScrollPane scrollPane = new JAutoScrollPane();

	private final LinkEventListener linkListener = new LinkEventListener() {

		@Override
		public void linkClicked(final String linkText) {
			frame.getView().setInputText("/pm " + linkText + " ", true);
		}
	};
	private final GhostEventAdapter ghostEventListener = new GhostEventAdapter() {

		@Override
		public void playerAdded(final GhostPlayer player) {
			addMatch(player.getName());
		}

		@Override
		public void playerRemoved(final GhostPlayer player) {
			removeMatch(player.getName());
		}

		@Override
		public void privateMessageLogged(final GhostPlayer from, final GhostPlayer to, final String message) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPane.autoscrollNext();
					try {
						final StyledDocument doc = getStyledDocument();
						final Style iconOnly = doc.getStyle("iconOnly");
						getStyledDocument().insertString(getStyledDocument().getLength(), "[" + Constants.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] ", null);
						if (from.getRights() > 0) {
							StyleConstants.setIcon(iconOnly, frame.getRankManager().rankForLevel(from.getRights()).getIcon());
							doc.insertString(doc.getLength(), " ", iconOnly);
						}
						doc.insertString(doc.getLength(), from.getName() + " to ", null);
						if (to.getRights() > 0) {
							StyleConstants.setIcon(iconOnly, frame.getRankManager().rankForLevel(to.getRights()).getIcon());
							doc.insertString(doc.getLength(), " ", iconOnly);
						}
						doc.insertString(doc.getLength(), to.getName() + ": " + message + "\n", null);
					}
					catch (final Throwable e) {
						e.printStackTrace();
					}
				}
			});
			frame.getView().displayModuleNotification(PrivateChatModule.this);
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
		final Border border = BorderFactory.createEmptyBorder();
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
	public void setFrame(final LocalGhostFrame frame) {
		this.frame = frame;
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return ghostEventListener;
	}

	@Override
	public void setResourcePath(final String path) {
	}

	@Override
	public void load() {
	}

}
