package org.whired.ghostclient.client.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.whired.ghost.Constants;
import org.whired.ghost.net.packet.ModeratePacket;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.player.Rank;
import org.whired.ghost.player.RankManager;
import org.whired.ghostclient.awt.ConnectDialog;
import org.whired.ghostclient.awt.GhostContextMenu;
import org.whired.ghostclient.awt.GhostMenuItem;
import org.whired.ghostclient.awt.GhostScrollBarUI;
import org.whired.ghostclient.awt.GhostTabbedPane;
import org.whired.ghostclient.awt.RankManagerDialog;
import org.whired.ghostclient.awt.RoundedBorder;
import org.whired.ghostclient.awt.SortedListModel;
import org.whired.ghostclient.client.GhostClient;
import org.whired.ghostclient.client.GhostClientView;
import org.whired.ghostclient.client.GhostUI;
import org.whired.ghostclient.client.module.Module;

public class CompactClientGhostView extends JFrame implements GhostClientView {
	private GhostTabbedPane tabbedPane;
	private JTextField textInput;
	private JLabel lblConnection;
	private JList compPlayerList;
	private final String search = "Search..";
	private final Color diffBlue = new Color(46, 92, 123);
	private final Color transparent = new Color(0, 0, 0, 0);
	private Font ghostFontSmall = new Font("SansSerif", Font.PLAIN, 9);
	private Font ghostFontMedium = ghostFontSmall.deriveFont(10F);
	private final SortedListModel mdlPlayerList = new SortedListModel();
	private GhostClient model;
	private final LinkedList<String> inputHistory = new LinkedList<String>();
	private int historyIndex = 0;
	private GhostUI compactUI;

	public CompactClientGhostView() {
		try {
			initAndShow();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private final void initAndShow() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				}
				catch (final Exception e) {
					Constants.getLogger().warning("Error setting Metal look and feel:");
					e.printStackTrace();
				}
				try {
					ghostFontSmall = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("resources/ubuntu.ttf")).deriveFont(9F);
					ghostFontMedium = ghostFontSmall.deriveFont(10F);
					UIManager.put("TextField.foreground", Color.WHITE);
					UIManager.put("TextField.caretColor", Color.WHITE);
					UIManager.put("PasswordField.caretForeground", Color.WHITE);
					UIManager.put("TabbedPane.borderHightlightColor", diffBlue);
					UIManager.put("TabbedPane.darkShadow", diffBlue);
					UIManager.put("PasswordField.foreground", Color.WHITE);
					UIManager.put("PasswordField.caretColor", Color.WHITE);
					UIManager.put("ToolTip.font", ghostFontSmall);
					UIManager.put("OptionPane.messageFont", ghostFontSmall);
					UIManager.put("Label.foreground", Color.WHITE);
					UIManager.put("TabbedPane.foreground", Color.WHITE);
					UIManager.put("TextField.caretForeground", Color.WHITE);
					UIManager.put("TextField.foreground", Color.WHITE);
					UIManager.put("TextArea.foreground", Color.WHITE);
					UIManager.put("TextPane.foreground", Color.WHITE);
					UIManager.put("TextPane.selectionBackground", diffBlue);
					UIManager.put("MenuItem.selectionBackground", diffBlue);
					UIManager.put("MenuItem.selectionForeground", Color.WHITE);
					UIManager.put("TextPane.selectionForeground", Color.WHITE);
					UIManager.put("List.font", ghostFontSmall);
					UIManager.put("Button.font", ghostFontSmall);
					UIManager.put("Label.font", ghostFontSmall);
					UIManager.put("ComboBox.font", ghostFontSmall);
					UIManager.put("Tree.font", ghostFontMedium);
					UIManager.put("TextArea.font", ghostFontSmall);
					UIManager.put("TextPane.font", ghostFontSmall);
					UIManager.put("TextField.font", ghostFontSmall);
					UIManager.put("Menu.font", ghostFontSmall);
					UIManager.put("MenuItem.font", ghostFontSmall);
					UIManager.put("TabbedPane.font", ghostFontSmall);
					UIManager.put("ScrollBar.width", 8);
					UIManager.put("TabbedPane.contentAreaColor", new Color(0, 0, 0, 0));
					UIManager.put("TabbedPane.contentBorderInsets", new Insets(4, 2, 0, 4));
					UIManager.put("TabbedPane.selected", transparent);
					UIManager.put("TabbedPane.focus", diffBlue);
					UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(3, 3, 3, 3));
				}
				catch (final Exception e) {
					Constants.getLogger().warning("Error while overriding look and feel:");
					e.printStackTrace();
				}
				setTitle("GHOST");
				setResizable(false);
				addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(final WindowEvent e) {
						model.saveSessionSettings();
						System.exit(0);
					}
				});

				final Border emptyBorder = BorderFactory.createEmptyBorder();

				final JLabel imageLabel = new JLabel();
				try {
					compactUI = new GhostUI(new RoundedBorder(diffBlue), ImageIO.read(this.getClass().getResourceAsStream("resources/bluehex.jpg")));
				}
				catch (final Exception e) {
					Constants.getLogger().log(Level.WARNING, "Error while loading graphical resources:", e);
				}
				imageLabel.setIcon(new ImageIcon(compactUI.getBackgroundImage()));
				textInput = new JTextField();
				textInput.addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(final KeyEvent ke) {
						if (ke.getKeyCode() == 38) {
							if (historyIndex < inputHistory.size()) {
								historyIndex++;
							}
							if (inputHistory.size() > 0) {
								textInput.setText(inputHistory.get(inputHistory.size() - historyIndex));
							}
						}
						else if (ke.getKeyCode() == 40) {
							if (historyIndex > 0) {
								historyIndex--;
							}
							final int sz = inputHistory.size();
							if (sz - historyIndex < sz) {
								textInput.setText(inputHistory.get(inputHistory.size() - historyIndex));
							}
							else {
								textInput.setText(null);
							}
						}
					}
				});
				textInput.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent evt) {
						final String message = textInput.getText();
						textInput.setText("");
						if (!message.equals("")) {
							if (!message.startsWith("/")) {
								model.displayPublicChat(model.getUserPlayer(), message);
								model.getSessionManager().getRemoteFrame().displayPublicChat(model.getUserPlayer(), message);
							}
							else if (message.length() > 1) {
								model.handleCommand(message.substring(1, message.length()));
							}
							if (inputHistory.size() == 0 || !inputHistory.getLast().equalsIgnoreCase(message)) {
								if (inputHistory.size() > 50) {
									inputHistory.removeFirst();
								}
								inputHistory.addLast(message);
							}
							historyIndex = 0;
						}
					}
				});
				textInput.setOpaque(false);
				textInput.setBorder(compactUI.getBorder());
				textInput.setBounds(138, 484, 496, 17);
				textInput.setMargin(new Insets(0, 2, 0, 2));
				textInput.setFont(ghostFontSmall);

				lblConnection = new JLabel("Disconnected") {
					@Override
					protected void paintComponent(Graphics g) {
						g.setColor(getBackground());
						((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 5, 5);
						super.paintComponent(g);
					}
				};
				lblConnection.setOpaque(false);
				lblConnection.setBackground(transparent);
				lblConnection.setBorder(compactUI.getBorder());
				lblConnection.setHorizontalAlignment(SwingConstants.CENTER);
				lblConnection.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lblConnection.setBounds(635, 487, 66, 14);

				lblConnection.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(final MouseEvent e) {
						if (model.getSessionManager().sessionIsOpen()) {
							model.handleCommand("disconnect");
						}
						else if (e.getButton() == MouseEvent.BUTTON1) {
							final ConnectDialog cd = new ConnectDialog(compactUI);
							cd.setLocationRelativeTo(CompactClientGhostView.this);
							cd.setVisible(true);
							if (!cd.isCancelled()) {
								model.handleCommand("connect " + cd.getIp() + " " + cd.getPort() + " " + new String(cd.getPassword()));
								cd.dispose();
							}
						}
						else if (e.getButton() == MouseEvent.BUTTON3) {
							model.handleCommand("connect");
						}
					}
				});

				final JLabel lblPlayerCount = new JLabel("Players: 0");
				lblPlayerCount.setBorder(compactUI.getBorder());
				lblPlayerCount.setHorizontalAlignment(SwingConstants.CENTER);
				lblPlayerCount.setBounds(0, 0, 137, 16);

				compPlayerList = new JList();

				JMenuItem jmiDemote = new GhostMenuItem("Demote");
				jmiDemote.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.DEMOTE);
					}
				});
				JMenuItem jmiJail = new GhostMenuItem("Jail");
				jmiJail.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.JAIL);
					}
				});
				JMenuItem jmiKick = new GhostMenuItem("Kick");
				jmiKick.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.KICK);
					}
				});
				JMenuItem jmiBan = new GhostMenuItem("Ban");
				jmiBan.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.BAN);
					}
				});

				JMenuItem jmiIpBan = new GhostMenuItem("IP Ban");
				jmiIpBan.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.IP_BAN);
					}
				});

				JMenuItem jmiUnban = new GhostMenuItem("Unban");
				jmiUnban.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.UN_BAN);
					}
				});

				JMenuItem jmiPromote = new GhostMenuItem("Promote");
				jmiPromote.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tryModerate(ModeratePacket.PROMOTE);
					}
				});

				final JPopupMenu mnuPlayerList = new GhostContextMenu(new JMenuItem[] { jmiDemote, jmiJail, jmiKick, jmiBan, jmiIpBan, jmiUnban, jmiPromote });
				compPlayerList.setCellRenderer(new DefaultListCellRenderer() {

					@Override
					public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
						final GhostPlayer player;
						final Rank playerRank;
						if (value instanceof GhostPlayer) {
							player = (GhostPlayer) value;
							playerRank = model.getRankManager().rankForLevel(player.getRights());
						}
						else {
							player = null;
							playerRank = null;
						}
						final JLabel label = new JLabel(" " + value.toString()) {

							@Override
							public void paint(final Graphics g) {
								if (isSelected) {
									g.setColor(diffBlue);
									((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
									g.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight(), 5, 5);
									g.drawRoundRect(0, 0, this.getWidth() - 2, this.getHeight() - 1, 5, 5);
								}
								super.paint(g);
								if (playerRank != null) {
									final Icon i = playerRank.getIcon();
									i.paintIcon(this, g, getWidth() - i.getIconWidth() - 1, this.getHeight() / 2 - i.getIconHeight() / 2);
								}
								g.dispose();
							}
						};
						if (player != null && playerRank != null) {
							label.setToolTipText(player.getName() + " - " + playerRank.getTitle());
						}
						label.setFont(ghostFontSmall);
						return label;
					}
				});
				compPlayerList.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(final MouseEvent e) {
						switch (e.getButton()) {
							case MouseEvent.BUTTON1:
								final Object x = compPlayerList.getSelectedValue();
								if (x != null) {
									if (e.getClickCount() == 1) {

										model.getPlayerList().playerSelected((GhostPlayer) x);
									}
									else if (e.getClickCount() == 2) {
										textInput.setText("/pm " + x + " ");
										textInput.requestFocus();
									}
								}
							break;
							case MouseEvent.BUTTON2:
								int idx = compPlayerList.locationToIndex(e.getPoint());
								if (idx != -1) {
									model.moderatePlayer(mdlPlayerList.getElementAt(idx).toString(), ModeratePacket.KICK);
								}
							break;
							case MouseEvent.BUTTON3:
								idx = compPlayerList.locationToIndex(e.getPoint());
								if (idx != -1) {
									compPlayerList.setSelectedIndex(idx);
									model.getPlayerList().playerSelected((GhostPlayer) compPlayerList.getSelectedValue());
									mnuPlayerList.show(compPlayerList, e.getPoint().x, e.getPoint().y);
								}
							break;
						}
					}
				});
				compPlayerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				mdlPlayerList.addListDataListener(new ListDataListener() {

					@Override
					public void intervalAdded(final ListDataEvent e) {
					}

					@Override
					public void intervalRemoved(final ListDataEvent e) {
					}

					@Override
					public void contentsChanged(final ListDataEvent e) {
						lblPlayerCount.setText("Players: " + ((AbstractListModel) e.getSource()).getSize());
					}
				});
				compPlayerList.setModel(mdlPlayerList);
				final JScrollPane scrlPlayerList = new JScrollPane();
				scrlPlayerList.setBounds(0, 15, 137, 457);
				scrlPlayerList.setViewportView(compPlayerList);
				scrlPlayerList.getViewport().setOpaque(false);
				scrlPlayerList.setOpaque(false);
				scrlPlayerList.getVerticalScrollBar().setUI(new GhostScrollBarUI(scrlPlayerList.getVerticalScrollBar()));
				compPlayerList.setOpaque(false);
				scrlPlayerList.setBorder(compactUI.getBorder());
				compPlayerList.setBorder(emptyBorder);

				final JLabel btnRestart = new JLabel("Ranks");
				btnRestart.setBounds(69, 487, 68, 14);
				btnRestart.setBorder(compactUI.getBorder());
				btnRestart.setOpaque(true);
				btnRestart.setBackground(transparent);
				btnRestart.setHorizontalAlignment(SwingConstants.CENTER);
				btnRestart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btnRestart.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(final MouseEvent arg0) {
						RankManager rm = model.getRankManager();
						Rank[] oldRanks = rm.getAllRanks();
						RankManagerDialog rmd = new RankManagerDialog(compactUI, CompactClientGhostView.this, oldRanks);
						rmd.setVisible(true);
						if (!rmd.isCancelled()) {
							rm.unregisterAll();
							rm.registerAll(rmd.getRanks());

							// Update current rank icons to match new
							compPlayerList.repaint();
						}
					}
				});

				final JLabel btnBugs = new JLabel("Bugs");
				btnBugs.setBounds(0, 487, 68, 14);
				btnBugs.setBorder(compactUI.getBorder());
				btnBugs.setOpaque(true);
				btnBugs.setBackground(transparent);
				btnBugs.setHorizontalAlignment(SwingConstants.CENTER);
				btnBugs.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btnBugs.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(final MouseEvent arg0) {
						try {
							Desktop.getDesktop().browse(Constants.BUG_REPORT_SITE);
						}
						catch (final Throwable e) {
							Constants.getLogger().info("Could not open " + Constants.BUG_REPORT_SITE + ", go there manually.");
						}
					}
				});

				tabbedPane = new GhostTabbedPane();
				tabbedPane.setTabPlacement(SwingConstants.BOTTOM);
				tabbedPane.setBounds(136, 0, 565, 484);
				tabbedPane.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(final ChangeEvent evt) {
						final int idx = tabbedPane.getSelectedIndex();
						if (idx != -1) {
							tabbedPane.setBackgroundAt(idx, transparent);
						}
					}
				});
				tabbedPane.tabsReordered = new Runnable() {

					@Override
					public void run() {
						final LinkedList<String> tabs = new LinkedList<String>();
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							tabs.add(tabbedPane.getTitleAt(i));
						}
						model.getSessionSettings().setTabOrder(tabs.toArray(new String[tabs.size()]));
					}
				};
				tabbedPane.setBackground(transparent);

				final JTextField textSearch = new JTextField();
				textSearch.setBounds(0, 471, 137, 15);
				textSearch.setOpaque(false);
				textSearch.setBorder(compactUI.getBorder());
				textSearch.setText(search);
				textSearch.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(final DocumentEvent e) {
						changedUpdate(e);
					}

					@Override
					public void removeUpdate(final DocumentEvent e) {
						changedUpdate(e);
					}

					@Override
					public void changedUpdate(final DocumentEvent e) {
						final String search = textSearch.getText().toLowerCase();
						if (search.length() > 0 && !search.equalsIgnoreCase(search)) {
							Object sel = compPlayerList.getSelectedValue();
							// No reason to iterate if we still match
							if (sel != null && sel.toString().toLowerCase().startsWith(search) && search.length() <= sel.toString().length()) {
								return;
							}
							Iterator<Object> it = mdlPlayerList.iterator();
							Object containsMatch = null;
							while (it.hasNext()) {
								Object elem = it.next();
								// startsWith first -- more specific than contains
								if (elem.toString().toLowerCase().startsWith(search.toLowerCase())) {
									compPlayerList.setSelectedValue(elem, true);
									return;
								}
								// If we don't already have a contains match, try this elem
								else if (containsMatch == null && elem.toString().toLowerCase().contains(search.toLowerCase())) {
									containsMatch = elem;
								}

							}
							// If flow reaches here, we couldn't find a startsWith match
							if (containsMatch != null) {
								compPlayerList.setSelectedValue(containsMatch, true);
							}
							else {
								compPlayerList.getSelectionModel().clearSelection();
							}
						}
					}
				});
				textSearch.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(final KeyEvent ke) {
						if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
							int sel = compPlayerList.getSelectedIndex();
							final String search = textSearch.getText().toLowerCase();
							boolean over = false;
							if (ke.getKeyCode() == KeyEvent.VK_ENTER && search.length() > 0) {
								while (true) {
									final Object[] elems = new Object[mdlPlayerList.size() - sel - 1];
									mdlPlayerList.copyInto(sel + 1, elems);
									// Loop through the sub selection
									Object containsMatch = null;
									for (int i = 0; i < elems.length; i++) {
										if (!over && elems[i].toString().toLowerCase().startsWith(search)) {
											compPlayerList.setSelectedValue(elems[i], true);
											return;
										}
										else if (containsMatch == null && elems[i].toString().toLowerCase().contains(search)) {
											containsMatch = elems[i];
										}
									}
									// If flow reaches here we couldn't get a startsWith match
									if (containsMatch != null) {
										compPlayerList.setSelectedValue(containsMatch, true);
										return;
									}
									else { // If we're at the last match, start over
										sel = -1;
										over = true;
									}
								}
							}
						}
					}
				});
				textSearch.addFocusListener(new FocusAdapter() {

					@Override
					public void focusGained(final FocusEvent e) {
						if (textSearch.getText().equals(search)) {
							textSearch.setText("");
						}
						else {
							textSearch.selectAll();
						}
					}
				});
				// Add components to content pane
				final Container c = getContentPane();
				c.add(lblPlayerCount);
				c.add(scrlPlayerList);
				c.add(btnRestart);
				c.add(btnBugs);
				c.add(textSearch);
				c.add(textInput);
				c.add(lblConnection);
				c.add(tabbedPane);
				c.add(imageLabel);

				// Show frame
				pack();
				setLocationRelativeTo(null);
				setVisible(true);
			}
		});
	}

	private void tryModerate(int operation) {
		Object s;
		if ((s = compPlayerList.getSelectedValue()) != null) {
			model.moderatePlayer(s.toString(), operation);
		}
		else {
			Constants.getLogger().info("No player selected");
		}
	}

	@Override
	public void sessionOpened() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblConnection.setText("Connected");
				lblConnection.setBackground(diffBlue);
				repaint();
			}
		});
	}

	@Override
	public void sessionClosed() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				lblConnection.setText("Disconnected");
				lblConnection.setBackground(transparent);
				repaint();
			}
		});
	}

	@Override
	public void setModel(final GhostClient model) {
		this.model = model;
	}

	@Override
	public void setInputText(final String text, final boolean requestFocus) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textInput.setText(text);
				if (requestFocus) {
					textInput.requestFocusInWindow();
				}
			}
		});
	}

	@Override
	public void moduleAdded(final Module module) {
		tabbedPane.addTab(module.getModuleName(), module.getComponent());
	}

	@Override
	public void playerAdded(final GhostPlayer player) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mdlPlayerList.addElement(player);
			}
		});
	}

	@Override
	public void playerRemoved(final GhostPlayer player) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mdlPlayerList.removeElement(player);
			}
		});
	}

	@Override
	public void displayModuleNotification(final Module module) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final int bgTab = tabbedPane.indexOfComponent(module.getComponent());
				final int selTab = tabbedPane.getSelectedIndex();
				if (bgTab != -1 && selTab != -1 && selTab != bgTab && tabbedPane.getBackgroundAt(bgTab) != diffBlue) {
					tabbedPane.setBackgroundAt(bgTab, diffBlue);
				}
			}
		});
	}
}
