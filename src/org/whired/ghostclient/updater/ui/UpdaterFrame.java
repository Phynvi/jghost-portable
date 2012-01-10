package org.whired.ghostclient.updater.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * The frame for the updater
 * 
 * @author Whired
 */
public class UpdaterFrame extends JFrame {

	private final JTextArea output;
	private final JLabel iconLbl;
	private Runnable onLaunch;
	private final JCheckBox box;

	/**
	 * Creates a new updater form
	 */
	public UpdaterFrame() {
		UIManager.put("ScrollBar.width", 12);
		Exception lafError = null;
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("resources/ubuntu.ttf")).deriveFont(9F);
			UIManager.put("TextArea.font", f);
			UIManager.put("CheckBox.font", f);
			UIManager.put("Label.font", f);
		}
		catch (Exception e) {
			lafError = e;
		}

		this.setTitle("Updater");
		this.setSize(400, 265);
		this.setResizable(false);

		this.getContentPane().setLayout(null);
		iconLbl = new JLabel();
		iconLbl.setSize(new Dimension(400, 265));
		iconLbl.setIcon(new ImageIcon(this.getClass().getResource("resources/updaterbg.png")));

		JScrollPane pane = new JScrollPane();
		pane.setSize(new Dimension(198, 166));
		pane.setLocation(18, 40);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setViewportBorder(BorderFactory.createEmptyBorder());
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setOpaque(false);

		output = new JTextArea();
		output.setLineWrap(true);
		output.setEditable(false);
		output.setBorder(BorderFactory.createEmptyBorder());
		output.setOpaque(false);

		pane.setViewportView(output);
		pane.getViewport().setOpaque(false);

		box = new JCheckBox("Download official modules");
		box.setSize(140, 14);
		box.setOpaque(false);
		box.setLocation(85, 209);
		box.setSelected(true);

		JLabel launch = new JLabel("Launch");
		launch.setSize(40, 16);
		launch.setLocation(20, 209);
		launch.setOpaque(false);
		launch.setBorder(new RoundedBorder(5));
		launch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		launch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onLaunch.run();
			}
		});

		this.getContentPane().add(launch);
		this.getContentPane().add(box);
		this.getContentPane().add(pane);
		this.getContentPane().add(iconLbl);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		if (lafError != null) {
			log("Error while setting look and feel: " + lafError.toString());
		}
	}

	public void setOnLaunch(Runnable onLaunch) {
		this.onLaunch = onLaunch;
	}

	public boolean downloadModules() {
		return box.isSelected();
	}

	private static class RoundedBorder implements Border {

		private final int radius;

		RoundedBorder(int radius) {
			this.radius = radius;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}
	}

	/**
	 * Logs a message to the primary output box
	 * 
	 * @param message the message to log
	 */
	public void log(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				output.append(message + "\n");
			}
		});
	}
}
