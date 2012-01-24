package org.whired.ghostclient.client.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.whired.ghost.Constants;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.Rank;
import org.whired.ghostclient.client.GhostClient;
import org.whired.ghostclient.client.GhostClientView;
import org.whired.ghostclient.client.module.Module;

/**
 * The graphical components for the client
 * 
 * @author Whired
 */
public class DefaultClientGhostView extends JFrame implements GhostClientView {

	private JList playerListComponent = new JList();
	public DefaultListModel playerListModel;
	private int historyIndex = 1;
	private final java.util.HashMap<Integer, String> chatHistory = new java.util.HashMap<Integer, String>();
	public JLabel lblState;
	private JLabel atkDisp;
	private JLabel atkLabel;
	public JTextField chatInput;
	private JLabel coordDisp;
	private JLabel coordsLabel;
	private JLabel defDisp;
	private JLabel defLabel;
	private JLabel hpDisp;
	private JLabel hpLabel;
	private JLabel ipDisp;
	private JLabel ipLabel;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel5;
	private JPanel jPanel1;
	protected ScrollablePanel boundPacketPanel;
	private JScrollPane jScrollPane4;
	private JLabel mageDisp;
	private JLabel mageLabel;
	private JLabel pkpDisp;
	private JLabel pkpLabel;
	private JLabel playerCount;
	private JLabel pwDisp;
	private JLabel pwLabel;
	private JLabel rangeDisp;
	private JLabel rangeLabel;
	private JRoundedButton restartBut;
	private JLabel strDisp;
	private JLabel strLabel;
	private JLabel thpDisp;
	private JLabel thpLabel;
	private JScrollPane packetPanelContainer;
	private GhostTabbedPane jTabbedPane1;
	private GhostClient controller;

	/**
	 * Invoked when a menu item has been selected
	 * 
	 * @param i the index of the {@code JMenuItem} that was clicked
	 */
	public void menuActionPerformed(int i, ActionEvent e) {
		switch (i) {
		case 0: // Connect with dialog
			tryExtendedConnect();
		break;
		case 1: // Connect to default
			controller.handleCommand("connect");
		break;
		case 2:
		// TODO Send the file request packet here
		break;
		case 3:
			if (JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				controller.saveSettings();
				System.exit(0);
			}
		break;
		case 4:
			throw new UnsupportedOperationException("Momentarily disabled.");
			// controller.displayReflectionManager();
			// break;
		}
	}

	protected void tryExtendedConnect() {
		final JDialog jd = new JDialog();
		jd.setTitle("Connect");
		jd.setLocationRelativeTo(null);
		final JTextField connectInput = new JTextField();
		final JTextField portInput = new JTextField();
		final JPasswordField passwordInput = new JPasswordField();
		final JOptionPane jo = new JOptionPane(new Object[] { "Enter IP, port, and password.", connectInput, portInput, passwordInput }, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new Object[] { "OK", "Cancel" }, "OK");
		jd.setContentPane(jo);
		jd.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// Close the window here, but save the values entered
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
					if (value == JOptionPane.UNINITIALIZED_VALUE)
						return;
					jo.setValue(JOptionPane.UNINITIALIZED_VALUE);
					if (value.equals("OK")) {
						String IP = connectInput.getText();
						String port = portInput.getText();
						String password = new String(passwordInput.getPassword());
						if (IP.length() == 0 || port.length() == 0 || password.length() == 0) {
							JOptionPane.showMessageDialog(jd, "Not all fields were filled out properly.", "Error", JOptionPane.ERROR_MESSAGE);
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
						controller.handleCommand("connect " + IP + " " + port + " " + password);
					}
					else
						jd.dispose();
				}
			}
		});
		jd.setResizable(false);
		jd.pack();
		jd.setLocationRelativeTo(this);
		jd.setModal(true);
		jd.setVisible(true);
		connectInput.requestFocusInWindow();
	}

	public DefaultClientGhostView() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setLookAndFeel();
				initComponents();
				setLocationRelativeTo(null);
				setVisible(true);
			}
		});
	}

	/**
	 * Sets the look and feel of the program
	 */
	protected void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		}
		catch (Exception e) {
			Constants.getLogger().warning("Error setting Metal look and feel:");
			e.printStackTrace();
		}
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("resources/ubuntu.ttf")).deriveFont(9F);
			Font f2 = f.deriveFont(10F);
			UIManager.put("ToolTip.font", f);
			UIManager.put("OptionPane.messageFont", f);
			UIManager.put("List.font", f);
			UIManager.put("Button.font", f);
			UIManager.put("Label.font", f);
			UIManager.put("ComboBox.font", f);
			UIManager.put("Tree.font", f2);
			UIManager.put("TextArea.font", f2);
			UIManager.put("TextPane.font", f2);
			UIManager.put("TextField.font", f2);
			UIManager.put("Menu.font", f);
			UIManager.put("MenuItem.font", f);
			UIManager.put("TabbedPane.font", f);
			UIManager.put("ProgressBar.font", f);
			UIManager.put("ScrollBar.width", 12);
			UIManager.put("TabbedPane.contentAreaColor", new Color(0, 0, 250, 0));
			UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 1, 1, 1));
			UIManager.put("TabbedPane.tabsOpaque", false);

		}
		catch (Exception e) {
			Constants.getLogger().warning("Error while overriding look and feel:");
			e.printStackTrace();
		}
	}

	private JLabel imageLabel;

	/**
	 * Builds the GUI components
	 */
	private void initComponents() {
		jPanel1 = new JPanel();
		try {
			imageLabel = new JLabel();
			imageLabel.setIcon(new ImageIcon(this.getClass().getResource("resources/interfacetest.png")));
		}
		catch (Exception e) {
			Constants.getLogger().warning("Error while loading graphical resources:");
			e.printStackTrace();
		}
		lblState = new JLabel("Disconnected");
		ipDisp = new JLabel();
		defLabel = new JLabel();
		thpDisp = new JLabel();
		atkLabel = new JLabel();
		jTabbedPane1 = new GhostTabbedPane();
		jTabbedPane1.tabsReordered = new Runnable() {

			@Override
			public void run() {
				LinkedList<String> tabs = new LinkedList<String>();
				for (int i = 0; i < jTabbedPane1.getTabCount(); i++)
					tabs.add(jTabbedPane1.getTitleAt(i));
				controller.getSettings().setTabOrder(tabs.toArray(new String[tabs.size()]));
			}
		};
		pkpDisp = new JLabel();
		chatInput = new JTextField();
		rangeDisp = new JLabel();
		coordsLabel = new JLabel();
		jScrollPane4 = new JScrollPane();
		MouseListener mouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Object x = playerListComponent.getSelectedValue();
				if (e.getClickCount() == 1) {
					if (x != null)
						controller.getPlayerList().playerSelected((Player) x);
				}
				else if (e.getClickCount() == 2) {
					chatInput.setText("/pm " + x + " ");
					chatInput.requestFocus();
				}
			}
		};
		playerListComponent = new JList();
		playerListComponent.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list, final Object value, int index, final boolean isSelected, boolean cellHasFocus) {
				final Player player;
				final Rank playerRank;
				if (value instanceof Player) {
					player = (Player) value;
					playerRank = controller.getRankHandler().rankForLevel(player.getRights());
				}
				else {
					player = null;
					playerRank = null;
				}
				JLabel label = new JLabel(value.toString()) {

					@Override
					public void paint(Graphics g) {
						if (isSelected) {
							g.setColor(new Color(206, 222, 237, 200));
							g.fillRect(0, 0, this.getWidth(), this.getHeight());
							g.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
						}
						super.paint(g);
						if (playerRank != null) {
							Icon i = playerRank.getIcon();
							i.paintIcon(this, g, getWidth() - i.getIconWidth(), this.getHeight() / 2 - i.getIconHeight() / 2);
						}
						g.dispose();
					}
				};
				if (player != null && playerRank != null)
					label.setToolTipText(player.getName() + " - " + playerRank.getTitle());
				return label;
			}
		});
		playerListModel = new DefaultListModel();
		playerListComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerListComponent.addMouseListener(mouseListener);
		pwLabel = new JLabel();
		mageDisp = new JLabel();
		restartBut = new JRoundedButton();
		hpLabel = new JLabel();
		strLabel = new JLabel();
		pwDisp = new JLabel();
		rangeLabel = new JLabel();
		atkDisp = new JLabel();
		defDisp = new JLabel();
		thpLabel = new JLabel();
		jLabel5 = new JLabel();
		ipLabel = new JLabel();
		mageLabel = new JLabel();
		strDisp = new JLabel();
		hpDisp = new JLabel();
		jLabel3 = new JLabel();
		jLabel2 = new JLabel();
		coordDisp = new JLabel();
		playerCount = new JLabel();
		pkpLabel = new JLabel();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("jGHOST.Portable");
		setResizable(false);
		JMenuBar jmb = new JMenuBar();
		JMenu jmFile = new JMenu("File");
		JMenu jmBinding = new JMenu("Bindings");
		JMenuItem jmiConnect = new JMenuItem("Connect..");
		JMenuItem jmiQConnect = new JMenuItem("Quick Connect");
		JMenuItem jmiOpen = new JMenuItem("Open (server)");
		JMenuItem jmiExit = new JMenuItem("Exit");
		jmFile.add(jmiConnect);
		jmFile.add(jmiQConnect);
		jmFile.add(jmiOpen);
		jmFile.addSeparator();
		jmFile.add(jmiExit);
		JMenuItem jmiNew = new JMenuItem("New..");
		jmBinding.add(jmiNew);
		jmb.add(jmFile);
		jmb.add(jmBinding);
		setJMenuBar(jmb);
		jmiConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				menuActionPerformed(0, evt);
			}
		});
		jmiQConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				menuActionPerformed(1, evt);
			}
		});
		jmiOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				menuActionPerformed(2, evt);
			}
		});
		jmiExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				menuActionPerformed(3, evt);
			}
		});
		jmiNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				menuActionPerformed(4, evt);
			}
		});
		lblState.setBounds(660, 319, 79, 14);
		lblState.setBorder(BorderFactory.createLineBorder(new Color(99, 130, 191)));
		lblState.setHorizontalAlignment(SwingConstants.CENTER);
		ipDisp.setHorizontalAlignment(SwingConstants.CENTER);
		ipDisp.setText("n/a");
		defLabel.setText("Defence: ");
		thpDisp.setHorizontalAlignment(SwingConstants.CENTER);
		thpDisp.setText("n/a");
		atkLabel.setText("Attack: ");
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(132, 254, 739, 335);
		jTabbedPane1.setBounds(0, 0, 739, 335);
		jTabbedPane1.setTabPlacement(SwingConstants.BOTTOM);
		jTabbedPane1.setAutoscrolls(true);
		layeredPane.add(jTabbedPane1, 1);
		new LinkEventListener() {

			@Override
			public void linkClicked(String linkText) {
				chatInput.setText("/pm " + linkText + " ");
				chatInput.requestFocus();
			}
		};
		jTabbedPane1.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent evt) {
				jTabbedPane1.setForegroundAt(jTabbedPane1.getSelectedIndex(), Color.black);
			}
		});
		pkpDisp.setHorizontalAlignment(SwingConstants.CENTER);
		pkpDisp.setText("n/a");
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				controller.saveSettings();
				System.exit(0);
			}
		});
		chatInput.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == 38) {
					if (historyIndex > 1)
						historyIndex--;
					chatInput.setText(chatHistory.get(historyIndex));
				}
				else if (ke.getKeyCode() == 40)
					if (historyIndex < chatHistory.size() + 1) {
						historyIndex++;
						chatInput.setText(chatHistory.get(historyIndex));
					}
					else
						chatInput.setText(null);
			}
		});
		chatInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				String message = chatInput.getText();
				chatInput.setText("");
				if (!message.equals("")) {
					if (!message.startsWith("/"))
						controller.displayPublicChat(controller.getUserPlayer(), message);
					else
						controller.handleCommand(message.substring(1, message.length()));
					if (historyIndex > 250) {
						historyIndex = 1;
						chatHistory.clear();
					}
					if (chatHistory.isEmpty())
						historyIndex = 1;
					else
						historyIndex = chatHistory.size() + 1;
					if (chatHistory.get(historyIndex - 1) == null || !chatHistory.get(historyIndex - 1).equalsIgnoreCase(message))
						chatHistory.put(historyIndex++, message);
					else
						Constants.getLogger().fine("History match, input not saved");
				}
			}
		});
		boundPacketPanel = new ScrollablePanel(new GridLayout(0, 4, 4, 2));
		boundPacketPanel.setBackground(new Color(0, 0, 250, 0));
		boundPacketPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
		packetPanelContainer = new JScrollPane(boundPacketPanel);
		packetPanelContainer.getViewport().setOpaque(false);
		packetPanelContainer.setBackground(new Color(0, 0, 250, 0));
		packetPanelContainer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		Border border = BorderFactory.createEmptyBorder();
		Border border2 = BorderFactory.createLineBorder(new Color(99, 130, 191), 1);
		packetPanelContainer.setBorder(border2);
		packetPanelContainer.setBounds(390, 48, 481, 190);
		jPanel1.add(packetPanelContainer);
		chatInput.setBounds(3, 319, 657, 15);
		layeredPane.add(chatInput, 0);
		layeredPane.add(lblState);
		jPanel1.add(layeredPane);
		rangeDisp.setHorizontalAlignment(SwingConstants.CENTER);
		rangeDisp.setText("n/a");
		coordsLabel.setText("Coordinates: ");
		jScrollPane4.setViewportView(playerListComponent);
		jScrollPane4.getViewport().setOpaque(false);
		jScrollPane4.setOpaque(false);
		playerListComponent.setOpaque(false);
		playerListComponent.setBackground(new Color(0, 0, 250, 0));
		jScrollPane4.setBorder(border2);
		playerListComponent.setBorder(border);
		pwLabel.setText("Password: ");
		mageDisp.setHorizontalAlignment(SwingConstants.CENTER);
		mageDisp.setText("n/a");
		restartBut.setText("Restart");
		restartBut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				controller.restartServer();
			}
		});
		hpLabel.setText("Hitpoints: ");
		strLabel.setText("Strength: ");
		pwDisp.setHorizontalAlignment(SwingConstants.CENTER);
		pwDisp.setText("n/a");
		rangeLabel.setText("Ranged: ");
		atkDisp.setHorizontalAlignment(SwingConstants.CENTER);
		atkDisp.setText("n/a");
		defDisp.setHorizontalAlignment(SwingConstants.CENTER);
		defDisp.setText("n/a");
		thpLabel.setText("Total hours played: ");
		jLabel5.setFont(new Font("Arial", 1, 9));
		jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel5.setText("Bound Packets");
		ipLabel.setText("IP: ");
		mageLabel.setText("Magic: ");
		strDisp.setHorizontalAlignment(SwingConstants.CENTER);
		strDisp.setText("n/a");
		hpDisp.setHorizontalAlignment(SwingConstants.CENTER);
		hpDisp.setText("n/a");
		jLabel3.setFont(new Font("Arial", 1, 9));
		jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel3.setText("Statistics"); // TODO Add [+selectedPlayerName+]
		jLabel2.setFont(new Font("Arial", 1, 9));
		jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		coordDisp.setHorizontalAlignment(SwingConstants.CENTER);
		coordDisp.setText("n/a");
		playerCount.setFont(new Font("Arial", 1, 9));
		playerCount.setHorizontalAlignment(SwingConstants.CENTER);
		playerCount.setText("Players - 0");
		pkpLabel.setText("PK points: ");
		playerListModel.addListDataListener(new ListDataListener() {

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
				playerCount.setText("Players - " + source.getSize());
			}
		});
		playerListComponent.setModel(playerListModel);
		GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPane4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE).addComponent(playerCount, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE).addComponent(restartBut, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jLabel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE).addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addGap(10, 10, 10).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(thpLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(thpDisp, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addComponent(hpLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(hpDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(rangeLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(rangeDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(mageLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(mageDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(defLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(defDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(strLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(strDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(atkLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(atkDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(pkpLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(pkpDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(ipLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(ipDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(pwLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(pwDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(coordsLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(coordDisp, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))))).addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))))).addContainerGap()));
		jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] { atkLabel, coordsLabel, defLabel, hpLabel, ipLabel, mageLabel, pkpLabel, pwLabel, rangeLabel, strLabel, thpLabel });
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(playerCount).addComponent(jLabel2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(coordsLabel).addComponent(coordDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(pwLabel).addComponent(pwDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(ipLabel).addComponent(ipDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(pkpLabel).addComponent(pkpDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(atkLabel).addComponent(atkDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(strLabel).addComponent(strDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(defLabel).addComponent(defDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(mageLabel).addComponent(mageDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(rangeLabel).addComponent(rangeDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(hpLabel).addComponent(hpDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(thpLabel).addComponent(thpDisp))).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel5).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))).addGap(14, 14, 14)).addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(restartBut)).addContainerGap()));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		jScrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jTabbedPane1.setOpaque(false);
		pack();
		imageLabel.setSize(jPanel1.getSize());
		jPanel1.add(imageLabel);
	}

	@Override
	public void moduleAdded(final Module module) {
		jTabbedPane1.addTab(module.getModuleName(), module.getComponent());
		// Module size changes here
		int offs = jTabbedPane1.getBoundsAt(jTabbedPane1.getTabCount() - 1).width;
		chatInput.setSize(chatInput.getWidth() - offs, chatInput.getHeight());
		chatInput.setLocation(chatInput.getLocation().x + offs, chatInput.getLocation().y);
	}

	@Override
	public void setInputText(String text, boolean requestFocus) {
		// TODO edt
		this.chatInput.setText(text);
		if(requestFocus)
			this.chatInput.requestFocus();
	}

	@Override
	public void setModel(GhostClient controller) {
		this.controller = controller;
	}

	@Override
	public void sessionOpened() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblState.setText("Connected");
				lblState.setOpaque(true);
				lblState.setBackground(new Color(99, 130, 191, 120));
				DefaultClientGhostView.this.repaint();
			}
		});
	}

	@Override
	public void sessionClosed() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				lblState.setText("Disconnected");
				lblState.setOpaque(false);
				DefaultClientGhostView.this.repaint();
			}
		});
	}

	@Override
	public void displayModuleNotification(Module module) {
		int bgTab = jTabbedPane1.indexOfComponent(module.getComponent());
		int selTab = jTabbedPane1.getSelectedIndex();
		if (bgTab != -1 && selTab != -1 && selTab != bgTab && jTabbedPane1.getForegroundAt(bgTab) != Color.red)
			jTabbedPane1.setForegroundAt(bgTab, Color.red);
	}

	@Override
	public void playerAdded(Player player) {
		this.playerListModel.addElement(player);
	}

	@Override
	public void playerRemoved(Player player) {
		this.playerListModel.removeElement(player);
	}
}
