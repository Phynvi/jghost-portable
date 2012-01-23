import java.awt.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

import org.whired.ghost.Constants;
import org.whired.ghost.util.GhostFormatter;
import org.whired.ghostclient.awt.GhostScrollBarUI;
import org.whired.ghostclient.awt.JAutoScrollPane;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;
import org.whired.ghostclient.client.module.Module;

/**
 * Used to display logger output
 * 
 * @author Whired
 */
public class DebugModule implements Module {

	private final JAutoScrollPane scrollPane = new JAutoScrollPane();
	// private final JScrollPane scrollPane = new JScrollPane();
	private final JTextPane textPane = new JTextPane();
	private GhostClientFrame frame;
	private final OutputStream out = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			updateTextArea(String.valueOf((char) b), false);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			updateTextArea(new String(b, off, len), false);
		}

		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}
	};

	public DebugModule() {
		((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		textPane.setEditable(false);
		Border border = BorderFactory.createEmptyBorder();
		textPane.setBorder(border);
		textPane.setOpaque(false);
		scrollPane.setViewportView(textPane);
		scrollPane.setBorder(border);
		scrollPane.setViewportBorder(border);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUI(new GhostScrollBarUI(scrollPane.getVerticalScrollBar()));

		final GhostFormatter formatter = new GhostFormatter();
		Constants.getLogger().addHandler(new Handler() {

			@Override
			public void publish(final LogRecord record) {
				if (isLoggable(record))
					updateTextArea(formatter.format(record), true);
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		});

		PrintStream p = new PrintStream(out, true);
		System.setOut(p);
		System.setErr(p);
	}

	/**
	 * Appends string to {@code textArea}
	 * 
	 * @param text the text to append
	 */
	private void updateTextArea(final String text, final boolean preFormatted) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				scrollPane.autoscrollNext();
				Document doc = textPane.getStyledDocument();
				try {
					doc.insertString(doc.getLength(), !text.contains(System.getProperty("line.separator")) && !preFormatted ? "[" + Constants.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] System: " + text : text, null);
				}
				catch (BadLocationException e) {
				}
				if (frame != null)
					frame.getView().displayModuleNotification(DebugModule.this);
			}
		});
	}

	@Override
	public Component getComponent() {
		return scrollPane;
	}

	@Override
	public GhostEventAdapter getEventListener() {
		return null;
	}

	@Override
	public String getModuleName() {
		return "Debug";
	}

	@Override
	public void load() {
	}

	@Override
	public void setFrame(GhostClientFrame frame) {
		this.frame = frame;
	}

	@Override
	public void setResourcePath(String arg0) {
	}

}
