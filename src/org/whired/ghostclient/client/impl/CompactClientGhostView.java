package org.whired.ghostclient.client.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.whired.ghost.Constants;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.Rank;
import org.whired.ghostclient.awt.GhostScrollBarUI;
import org.whired.ghostclient.awt.RoundedBorder;
import org.whired.ghostclient.client.GhostClient;
import org.whired.ghostclient.client.GhostClientView;
import org.whired.ghostclient.client.module.Module;

public class CompactClientGhostView extends JFrame implements GhostClientView {
	private GhostTabbedPane tabbedPane;
	private JTextField textInput;
	private JLabel lblConnection;
	private final Color highlight = new Color(99, 130, 191, 120);
	private final Color transparent = new Color(0, 0, 0, 0);
	private final Font ghostFont = new Font("SansSerif", Font.PLAIN, 9);

	private final DefaultListModel mdlPlayerList = new DefaultListModel();
	private GhostClient model;
	private final LinkedList<String> inputHistory = new LinkedList<String>();
	private int historyIndex = 0;

	public CompactClientGhostView() {
		initAndShow();
	}

	private final void initAndShow() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				}
				catch (Exception e) {
					Constants.getLogger().warning("Error setting Metal look and feel:");
					e.printStackTrace();
				}
				try {
					Font f2 = ghostFont.deriveFont(10F);
					UIManager.put("ToolTip.font", ghostFont);
					UIManager.put("OptionPane.messageFont", ghostFont);
					UIManager.put("Label.foreground", Color.WHITE);
					UIManager.put("TabbedPane.foreground", Color.WHITE);
					UIManager.put("TextField.caretForeground", Color.WHITE);
					UIManager.put("TextField.foreground", Color.WHITE);
					UIManager.put("TextArea.foreground", Color.WHITE);
					UIManager.put("TextPane.foreground", Color.WHITE);
					UIManager.put("TextPane.selectionBackground", highlight);
					UIManager.put("TextPane.selectionForeground", Color.WHITE);
					UIManager.put("List.font", ghostFont);
					UIManager.put("Button.font", ghostFont);
					UIManager.put("Label.font", ghostFont);
					UIManager.put("ComboBox.font", ghostFont);
					UIManager.put("Tree.font", f2);
					UIManager.put("TextArea.font", f2);
					UIManager.put("TextPane.font", ghostFont);
					UIManager.put("TextField.font", f2);
					UIManager.put("Menu.font", ghostFont);
					UIManager.put("MenuItem.font", ghostFont);
					UIManager.put("TabbedPane.font", ghostFont);
					UIManager.put("ScrollBar.width", 8);
					UIManager.put("TabbedPane.contentAreaColor", new Color(0, 0, 0, 0));
					UIManager.put("TabbedPane.contentBorderInsets", new Insets(4, 0, 0, 4));
					UIManager.put("TabbedPane.selected", transparent);
				}
				catch (Exception e) {
					Constants.getLogger().warning("Error while overriding look and feel:");
					e.printStackTrace();
				}
				setTitle("GHOST lite");
				setResizable(false);
				addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						model.saveSettings();
						System.exit(0);
					}
				});

				Border emptyBorder = BorderFactory.createEmptyBorder();
				Border lineBorder = new RoundedBorder(new Color(99, 130, 191));

				JLabel imageLabel = new JLabel();
				try {
					imageLabel.setIcon(new ImageIcon(this.getClass().getResource("resources/blueleaf.jpg")));
				}
				catch (Exception e) {
					Constants.getLogger().log(Level.WARNING, "Error while loading graphical resources:", e);
				}

				textInput = new JTextField();
				textInput.addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent ke) {
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
							int sz = inputHistory.size();
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
					public void actionPerformed(ActionEvent evt) {
						String message = textInput.getText();
						textInput.setText("");
						if (!message.equals("")) {
							if (!message.startsWith("/")) {
								model.displayPublicChat(model.getUserPlayer(), message);
							}
							else {
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
				textInput.setBorder(lineBorder);
				textInput.setBounds(144, 483, 490, 14);

				lblConnection = new JLabel("Disconnected");
				lblConnection.setOpaque(true);
				lblConnection.setBackground(transparent);
				lblConnection.setBorder(lineBorder);
				lblConnection.setHorizontalAlignment(SwingConstants.CENTER);
				lblConnection.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lblConnection.setBounds(635, 483, 63, 14);

				lblConnection.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (model.getSessionManager().sessionIsOpen()) {
							model.handleCommand("disconnect");
						}
						else if (e.getButton() == MouseEvent.BUTTON1) {
							final JDialog jd = new JDialog();
							jd.setTitle("Connect");
							final JTextField connectInput = new JTextField();
							final JTextField portInput = new JTextField();
							final JPasswordField passwordInput = new JPasswordField();
							final JOptionPane jo = new JOptionPane(new Object[] { "Enter IP, port, and password.", connectInput, portInput, passwordInput }, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new Object[] { "OK", "Cancel" }, "OK");
							jd.setContentPane(jo);
							jd.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
							jd.addWindowListener(new WindowAdapter() {

								@Override
								public void windowClosing(WindowEvent we) {
									jo.setValue(JOptionPane.CLOSED_OPTION);
								}
							});
							jo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

								@Override
								public void propertyChange(java.beans.PropertyChangeEvent e) {
									String prop = e.getPropertyName();
									if (isVisible() && e.getSource() == jo && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
										Object value = jo.getValue();
										if (value == JOptionPane.UNINITIALIZED_VALUE) {
											return;
										}
										jo.setValue(JOptionPane.UNINITIALIZED_VALUE);
										if (value.equals("OK")) {
											String IP = connectInput.getText();
											String port = portInput.getText();
											String password = new String(passwordInput.getPassword());
											if (IP.length() == 0 || port.length() == 0 || password.length() == 0) {
												JOptionPane.showMessageDialog(jd, "Not all fields were filled out properly", "Error", JOptionPane.ERROR_MESSAGE);
												return;
											}
											try {
												port = "" + Integer.parseInt(portInput.getText());
											}
											catch (Exception err) {
												JOptionPane.showMessageDialog(jd, "The port must be numeric!", "Error", JOptionPane.ERROR_MESSAGE);
												portInput.setText(null);
												portInput.requestFocusInWindow();
												return;
											}
											if (!IP.contains(".") && !IP.toLowerCase().equals("localhost")) {
												JOptionPane.showMessageDialog(jd, "The IP entered was invalid.", "Error", JOptionPane.ERROR_MESSAGE);
												connectInput.setText(null);
												connectInput.requestFocusInWindow();
												return;
											}
											jd.dispose();
											model.handleCommand("connect " + IP + " " + port + " " + password);
										}
										else {
											jd.dispose();
										}
									}
								}
							});
							jd.setResizable(false);
							jd.pack();
							jd.setLocationRelativeTo(CompactClientGhostView.this);
							jd.setModal(true);
							jd.setVisible(true);
							connectInput.requestFocusInWindow();
						}
						else if (e.getButton() == MouseEvent.BUTTON3) {
							model.handleCommand("connect");
						}
					}
				});

				final JLabel lblPlayerCount = new JLabel("Players: 0");
				lblPlayerCount.setBorder(lineBorder);
				lblPlayerCount.setHorizontalAlignment(SwingConstants.CENTER);
				lblPlayerCount.setBounds(3, 1, 140, 15);

				final JList compPlayerList = new JList();
				compPlayerList.setCellRenderer(new DefaultListCellRenderer() {

					@Override
					public Component getListCellRendererComponent(JList list, final Object value, int index, final boolean isSelected, boolean cellHasFocus) {
						final Player player;
						final Rank playerRank;
						if (value instanceof Player) {
							player = (Player) value;
							playerRank = model.getRankHandler().rankForLevel(player.getRights());
						}
						else {
							player = null;
							playerRank = null;
						}
						JLabel label = new JLabel(" " + value.toString()) {

							@Override
							public void paint(Graphics g) {
								if (isSelected) {
									g.setColor(highlight);
									((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
									g.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight(), 5, 5);
									g.drawRoundRect(0, 0, this.getWidth() - 2, this.getHeight() - 1, 5, 5);
								}
								super.paint(g);
								if (playerRank != null) {
									Icon i = playerRank.getIcon();
									i.paintIcon(this, g, getWidth() - i.getIconWidth() - 1, this.getHeight() / 2 - i.getIconHeight() / 2);
								}
								g.dispose();
							}
						};
						if (player != null && playerRank != null) {
							label.setToolTipText(player.getName() + " - " + playerRank.getTitle());
						}
						label.setFont(ghostFont);
						return label;
					}
				});
				compPlayerList.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						Object x = compPlayerList.getSelectedValue();
						if (e.getClickCount() == 1) {
							if (x != null) {
								model.getPlayerList().playerSelected((Player) x);
							}
						}
						else if (e.getClickCount() == 2) {
							textInput.setText("/pm " + x + " ");
							textInput.requestFocus();
						}
					}
				});
				compPlayerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				mdlPlayerList.addListDataListener(new ListDataListener() {

					@Override
					public void intervalAdded(ListDataEvent e) {
						consumeEvent((DefaultListModel) e.getSource(), ((DefaultListModel) e.getSource()).elementAt(e.getIndex0()).toString());
					}

					@Override
					public void intervalRemoved(ListDataEvent e) {
						consumeEvent((DefaultListModel) e.getSource(), ((DefaultListModel) e.getSource()).elementAt(e.getIndex0()).toString());
					}

					@Override
					public void contentsChanged(ListDataEvent e) {
					}

					private void consumeEvent(DefaultListModel source, String item) {
						lblPlayerCount.setText("Players: " + source.getSize());
					}
				});
				compPlayerList.setModel(mdlPlayerList);
				JScrollPane scrlPlayerList = new JScrollPane();
				scrlPlayerList.setBounds(3, 15, 140, 453);
				scrlPlayerList.setViewportView(compPlayerList);
				scrlPlayerList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrlPlayerList.getViewport().setOpaque(false);
				scrlPlayerList.setOpaque(false);
				scrlPlayerList.getVerticalScrollBar().setUI(new GhostScrollBarUI(scrlPlayerList.getVerticalScrollBar()));
				compPlayerList.setOpaque(false);
				scrlPlayerList.setBorder(lineBorder);
				compPlayerList.setBorder(emptyBorder);

				JLabel btnRestart = new JLabel("Restart");
				btnRestart.setBounds(3, 483, 140, 14);
				btnRestart.setBorder(lineBorder);
				btnRestart.setOpaque(true);
				btnRestart.setBackground(transparent);
				btnRestart.setHorizontalAlignment(SwingConstants.CENTER);
				btnRestart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				tabbedPane = new GhostTabbedPane();
				tabbedPane.setTabPlacement(SwingConstants.BOTTOM);
				tabbedPane.setBounds(144, 0, 554, 484);
				tabbedPane.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent evt) {
						int idx = tabbedPane.getSelectedIndex();
						if (idx != -1) {
							tabbedPane.setBackgroundAt(idx, transparent);
						}
					}
				});
				tabbedPane.tabsReordered = new Runnable() {

					@Override
					public void run() {
						LinkedList<String> tabs = new LinkedList<String>();
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							tabs.add(tabbedPane.getTitleAt(i));
						}
						model.getSettings().setTabOrder(tabs.toArray(new String[tabs.size()]));
					}
				};
				tabbedPane.setBackground(transparent);

				final JTextField textSearch = new JTextField();
				textSearch.setBounds(3, 467, 140, 14);
				textSearch.setOpaque(false);
				textSearch.setBorder(lineBorder);
				textSearch.setText("Search..");
				textSearch.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(DocumentEvent e) {
						changedUpdate(e);
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						changedUpdate(e);
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						String search = textSearch.getText();
						if (search.length() > 0) {
							Object[] elems = new Object[mdlPlayerList.size()];
							mdlPlayerList.copyInto(elems);
							for (Object elem : elems) {
								if (elem.toString().toLowerCase().contains(search.toLowerCase())) {
									compPlayerList.setSelectedValue(elem, true);
									break;
								}
							}
						}
					}
				});
				textSearch.addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent ke) {
						int sel = compPlayerList.getSelectedIndex();
						String search = textSearch.getText();
						if (ke.getKeyCode() == KeyEvent.VK_ENTER && sel != -1 && search.length() > 0) {
							Object[] elems = new Object[mdlPlayerList.size()];
							mdlPlayerList.copyInto(elems);
							for (int i = sel + 1; i < elems.length; i++) {
								if (elems[i].toString().toLowerCase().contains(search.toLowerCase())) {
									compPlayerList.setSelectedValue(elems[i], true);
									break;
								}
							}
						}
					}
				});
				textSearch.addFocusListener(new FocusListener() {

					@Override
					public void focusGained(FocusEvent e) {
						textSearch.setText("");
					}

					@Override
					public void focusLost(FocusEvent e) {
						textSearch.setText("Search..");
					}
				});
				// Add components to content pane
				Container c = getContentPane();
				c.add(lblPlayerCount);
				c.add(scrlPlayerList);
				c.add(btnRestart);
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

	@Override
	public void sessionOpened() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblConnection.setText("Connected");
				lblConnection.setBackground(highlight);
				repaint();
			}
		});
	}

	@Override
	public void sessionClosed(String reason) {
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
	public void setModel(GhostClient model) {
		this.model = model;
	}

	@Override
	public void setInputText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textInput.setText(text);
			}
		});
	}

	@Override
	public void focusInputBox() {
		textInput.requestFocusInWindow();
	}

	@Override
	public void moduleAdded(final Module module) {
		tabbedPane.addTab(module.getModuleName(), module.getComponent());
	}

	@Override
	public void playerAdded(final Player player) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mdlPlayerList.addElement(player);
			}
		});
	}

	@Override
	public void playerRemoved(final Player player) {
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
				int bgTab = tabbedPane.indexOfComponent(module.getComponent());
				int selTab = tabbedPane.getSelectedIndex();
				if (bgTab != -1 && selTab != -1 && selTab != bgTab && tabbedPane.getBackgroundAt(bgTab) != highlight) {
					tabbedPane.setBackgroundAt(bgTab, highlight);
				}
			}
		});
	}
}
