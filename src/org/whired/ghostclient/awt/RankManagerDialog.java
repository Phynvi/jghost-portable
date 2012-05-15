package org.whired.ghostclient.awt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.whired.ghost.player.Rank;
import org.whired.ghostclient.client.GhostUI;

public class RankManagerDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final DefaultListModel mdl = new DefaultListModel();
	private boolean canceled;

	/**
	 * Creates a new rank manager dialog for the specified ranks
	 * @param availableRanks the ranks to manage
	 */
	public RankManagerDialog(final GhostUI ghostUI, Frame owner, final Rank[] availableRanks) {
		setUndecorated(true);
		setResizable(false);
		setModal(true);
		setSize(208, 272);
		setLocationRelativeTo(owner);
		this.setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(ghostUI.getBackgroundImage(), 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), this);
			};
		});
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		final Color diffBlue = new Color(46, 92, 123);
		final JList list = new JList() {
			ListCellRenderer renderer = new ListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, final boolean isSelected, boolean cellHasFocus) {
					final Rank rank = (Rank) value;
					final JLabel label = new JLabel(" " + value.toString()) {

						@Override
						public void paintComponent(final Graphics g) {
							if (isSelected) {
								g.setColor(diffBlue);
								((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
								g.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight(), 5, 5);
								g.drawRoundRect(0, 0, this.getWidth() - 2, this.getHeight() - 1, 5, 5);
							}
							super.paintComponent(g);
							final Icon i = rank.getIcon();
							i.paintIcon(this, g, getWidth() - i.getIconWidth() - 1, this.getHeight() / 2 - i.getIconHeight() / 2);
							g.dispose();
						}
					};
					return label;
				}
			};

			@Override
			public ListCellRenderer getCellRenderer() {
				return renderer;

			}
		};
		for (Rank r : availableRanks) {
			mdl.addElement(r);
		}
		list.setModel(mdl);
		list.setOpaque(false);
		final Border emptyBorder = BorderFactory.createEmptyBorder();
		scrollPane.setViewportView(list);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.getVerticalScrollBar().setUI(new GhostScrollBarUI(scrollPane.getVerticalScrollBar()));
		list.setOpaque(false);
		scrollPane.setBorder(ghostUI.getBorder());
		list.setBorder(emptyBorder);
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int selIdx = list.getSelectedIndex();
				if (selIdx != -1) {
					int move;
					if (e.getKeyCode() == KeyEvent.VK_UP && selIdx > 0) {
						move = -1;
					}
					else if (e.getKeyCode() == KeyEvent.VK_DOWN && selIdx < mdl.getSize() - 1) {
						move = 1;
					}
					else {
						return;
					}
					Object old = mdl.getElementAt(selIdx + move);
					mdl.setElementAt(list.getSelectedValue(), selIdx + move);
					mdl.setElementAt(old, selIdx);
					list.setSelectedIndex(selIdx + move);
					e.consume();
				}
			}
		});
		JLabel lblAvailable = new JLabel("Rank order (from 0, use arrow keys to change)");
		contentPanel.add(lblAvailable, BorderLayout.NORTH);
		lblAvailable.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel buttonPane = new JPanel();
		buttonPane.setOpaque(false);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JLabel lblOk = new JLabel("OK");
		lblOk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				setVisible(false);
			}
		});
		lblOk.setBorder(ghostUI.getBorder());
		buttonPane.add(lblOk);
		JLabel lblCancel = new JLabel("Cancel");
		lblCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				canceled = true;
				setVisible(false);
			}
		});
		lblCancel.setBorder(ghostUI.getBorder());
		buttonPane.add(lblCancel);
	}

	public boolean isCancelled() {
		return canceled;
	}

	public Rank[] getRanks() {
		Rank[] ranks = new Rank[mdl.getSize()];
		Object[] raws = mdl.toArray();
		for (int i = 0; i < raws.length; i++) {
			ranks[i] = (Rank) raws[i];
			ranks[i].setLevel(i);
		}
		return ranks;
	}
}
