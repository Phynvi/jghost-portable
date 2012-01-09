/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.whired.ghostclient.updater.ui;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 
 * @author Whired
 */
public class UpdaterForm extends JFrame {

	private JTextArea output;
	private JLabel iconLbl;

	/**
	 * Creates a new updater form
	 */
	public UpdaterForm() {
		UIManager.put("ScrollBar.width", 12);
		Exception lafError = null;
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("resources/ubuntu.ttf")).deriveFont(9F);
			UIManager.put("TextArea.font", f);
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
		pane.setSize(new Dimension(198, 150));
		pane.setLocation(18, 75);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setViewportBorder(BorderFactory.createEmptyBorder());
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setOpaque(false);

		output = new JTextArea();
		output.setLineWrap(true);
		output.setEditable(false);
		output.setBorder(BorderFactory.createEmptyBorder());
		output.setOpaque(false);

		pane.setViewportView(output);
		pane.getViewport().setOpaque(false);

		this.getContentPane().add(pane, 0);
		this.getContentPane().add(iconLbl, 1);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		if (lafError != null) {
			log("Error while setting look and feel: " + lafError.toString());
		}
	}

	/**
	 * Logs a message to the primary output box
	 * 
	 * @param message the message to log
	 */
	public void log(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				output.append(message + "\n");
			}
		});
	}
}
