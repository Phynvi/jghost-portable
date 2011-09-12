/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.whired.ghostclient.updater.ui;

import java.awt.Color;
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
public class UpdaterForm extends JFrame
{
	public UpdaterForm()
	{
		UIManager.put("ScrollBar.width", 12);
		Exception lafError = null;
		try
		{
			Font f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("resources/arial.ttf")).deriveFont(9F);
			UIManager.put("TextArea.font", f);
		}
		catch (Exception e)
		{
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
		//pane..setBackground(new Color(0, 0, 250, 20));
		//
		this.getContentPane().add(pane, 0);
		this.getContentPane().add(iconLbl, 1);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		if(lafError != null)
		{
			log("Error while setting look and feel: "+lafError.toString());
		}
	}

	private JTextArea output;
	private JLabel iconLbl;
	public static void main(String[] arg)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new UpdaterForm().setVisible(true);
			}
		});
	}

	public void log(final String message)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				output.append(message + "\n");
			}
		});
	}
}
