package org.whired.ghostclient.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import org.whired.ghost.client.util.CommandMalformedException;
import org.whired.ghost.client.util.CommandNotFoundException;
import org.whired.ghost.Vars;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.whired.ghost.client.net.ClientConnection;
import org.whired.ghost.client.ui.GhostFrame;
import org.whired.ghost.client.util.Command;
import org.whired.ghost.client.util.CommandHandler;
import org.whired.ghost.net.model.player.MapPlayer;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.rsmap.ui.RSMap;
//import org.whired.rsmap.ui.RSMap;

/**
 * @author Whired
 *
 * The client; contains both graphical and functional methods.
 */
public class GhostFrameImpl extends GhostFrame
{

	private JList playerListComponent = new JList();
	private DefaultListModel playerList;
	private int historyIndex = 1;
	private java.util.HashMap<Integer, String> chatHistory = new java.util.HashMap<Integer, String>();
	private java.util.Calendar cal = java.util.Calendar.getInstance();
	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm:ss a");
	public JProgressBar progress;
	private JLabel atkDisp;
	private JLabel atkLabel;
	public JTextField chatInput;
	public LinkingJTextPane chatOutput;
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
	private ScrollablePanel boundPacketPanel;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JScrollPane jScrollPane4;
	private JTabbedPane jTabbedPane1;
	private JTextArea debugOutput;
	private JLabel mageDisp;
	private JLabel mageLabel;
	private JLabel pkpDisp;
	private JLabel pkpLabel;
	private JLabel playerCount;
	private JTextPane pmOutput;
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

	/**
	 * Builds and initializes the graphical client.
	 */
	public GhostFrameImpl()
	{
		CommandHandler.addCommand(new Command("test")
		{

			@Override
			public void handle(String[] args)
			{
				try
				{
					chatOutput.getDocument().insertString(0, "args", null);
				}
				catch (BadLocationException ex)
				{
					Logger.getLogger(GhostFrameImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		CommandHandler.addCommand(new Command("setrights")
		{

			@Override
			public void handle(String[] args)
			{
				getUser().getSettings().getPlayer().setRights(Integer.parseInt(args[0]));
			}
		});
		CommandHandler.addCommand(new Command("pm")
		{

			@Override
			public void handle(String[] args)
			{
				String message = "";
				for(int i = 1; i < args.length; i++)
				{
					message += args[i]+" ";
				}
				displayPrivateChat(getUser().getSettings().getPlayer(), new Player(args[0], 0), message);
			}
		});
		CommandHandler.addCommand(new Command("connect")
		{

			@Override
			public void handle(String[] args)
			{
				if (args.length > 0 && args[0] != null)
				{
					if (args.length > 1 && args[1] != null)
					{
						if (args.length > 2 && args[2] != null)
						{
							int port = -1;
							try
							{
								port = Integer.parseInt(args[1]);
							}
							catch (Exception e)
							{
								Vars.getLogger().fine("Exception " + e.getMessage());
								return;
							}
							Vars.getLogger().info("Use /connect to quickly connect to this IP in the future.");
							try
							{
								setConnection(ClientConnection.connect(args[0], port, args[2], GhostFrameImpl.this));
								Vars.getLogger().info("Successfully connected to " + args[0] + ":" + port);
							}
							catch (Exception e)
							{//TODO handle properly
								Vars.getLogger().warning("Unable to connect to " + args[0] + ":" + port);
								Vars.getLogger().fine(e.getMessage());
							}
							getUser().getSettings().defaultConnect[0] = args[0];
							getUser().getSettings().defaultConnect[1] = Integer.toString(port);
							getUser().getSettings().defaultConnect[2] = args[2];
						}
						else
						{
							Vars.getLogger().warning("Please specify a password.");
						}
					}
					else
					{
						Vars.getLogger().warning("Please specify a port.");
					}
				}
				else
				{
					Vars.getLogger().warning("Please specify an IP.");
				}
			}
		});
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				try
				{
					redirectSystemStreams();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				log("Running.");
				setLookAndFeel();
				initComponents();
				createTabHandler(chatOutput, 0);
				createTabHandler(pmOutput, 1);
				createTabHandler(debugOutput, 2);
				loadBoundPackets();
				updatePlayerList();
				setVisible(true);
			}
		});
	}

	private void loadBoundPackets()
	{
		for (ReflectionPacketContainer packet : ReflectionPacketContainer.loadAllContainers())
		{
			bindPacket(packet);
		}
	}

	protected void bindPacket(final ReflectionPacketContainer packet)
	{
		JRoundedButton button = new JRoundedButton(packet.packetName);
		button.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				ReflectionPacketContainer.invoke(packet, getConnection());
			}
		});
		boundPacketPanel.add(button);
		//boundPackets.add(packet);
	}

	/**
	 * Sets the look and feel of the program
	 */
	protected void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		}
		catch (Exception e)
		{
			log("Error setting Metal look and feel:");
			e.printStackTrace();
		}
		try
		{
			Font f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("resources/arial.ttf")).deriveFont(9F);
			Font f2 = f.deriveFont(10F);
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
		catch (Exception e)
		{
			log("Error applying custom styles:", e);
		}
	}

	/**
	 * Prints <code>message/code> to debug
	 */
	public final void log(String message)
	{
		System.out.println("Ghost: " + message);
	}

	/**
	 * Prints an exception to debug
	 */
	public void log(String message, Exception e)
	{
		log(message);
		e.printStackTrace();
	}

	/**
	 * Gets the path for opening quick logs.
	 * @return the String that represents the absolute path name of the file chosen
	 */
	public String getPathTo()
	{
		final JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(jPanel1);
		String f = "";
		if (fc.getSelectedFile() != null)
		{
			f = fc.getSelectedFile().getAbsolutePath();
		}
		return f;
	}

	/**
	 * Quickly handles a command.
	 * @param command the String that will be parsed to a command
	 */
	private void doCommand(String command)
	{
		try
		{
			new CommandHandler(Vars.getLogger()).handleInput(command);
		}
		catch (CommandMalformedException ex)
		{
			Vars.getLogger().warning(ex.toString());
		}
		catch (CommandNotFoundException ex)
		{
			Vars.getLogger().warning(ex.toString());
		}
	}

	/**
	 * // TODO the read @note x
	 * Updates the list that holds the names of the players on the server
	 * @note this method is unfinished and might be unnecessary; this list should be
	 * updated one String at a time, using a remove String and an add String packet.
	 */
	public void updatePlayerList()
	{
		playerList.clear();
	}

	/**
	 * Sets up the handlers that control the tab font color;
	 * Used for notifying the user of changes happening elsewhere.
	 * @param box the JTextArea to set up the handler for
	 * @param tabIndex the corresponding index for the tab in which "box" lies
	 */
	private void createTabHandler(JTextComponent box, int tabIndex)
	{
		final int ti = tabIndex;
		box.getDocument().addDocumentListener(new DocumentListener()
		{

			public void insertUpdate(DocumentEvent e)
			{
				if (jTabbedPane1.getSelectedIndex() != ti && jTabbedPane1.getForegroundAt(ti) != Color.red)
				{
					jTabbedPane1.setForegroundAt(ti, Color.red);
				}
			}

			public void removeUpdate(DocumentEvent e)
			{
			}

			public void changedUpdate(DocumentEvent e)
			{
			}
		});
	}

	public void restartButActionPerformed(ActionEvent evt)
	{
		System.out.println("restartButActionPerformed");
		playerList.addElement(new MapPlayer("Whired", 4, 2000, 2000, map));
	}

	/**
	 * Used for hooking debugOutput to System.out/err
	 */
	private synchronized void redirectSystemStreams()
	{
		java.io.OutputStream out = new java.io.OutputStream()
		{

			@Override
			public void write(int b) throws IOException
			{
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException
			{
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException
			{
				write(b, 0, b.length);
			}
		};
		PrintStream p = new PrintStream(out, true);
		System.setOut(p);
		System.setErr(p);
	}

	/**
	 * Displays a chat message to {@code chatOutput}
	 * @param rights the rights of he user
	 * @param name the name of the user
	 * @param message the message the user sent
	 */
	public void displayPublicChat(Player sender, String message)
	{
		Icon i = rightsIcons[sender.getRights()];
		try
		{
			Style iconOnly = chatOutput.getStyledDocument().getStyle("iconOnly");
			if (iconOnly == null)
			{
				iconOnly = chatOutput.getStyledDocument().addStyle("iconOnly", null);
			}
			StyleConstants.setIcon(iconOnly, i);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), "dummytext", iconOnly);
			StyleConstants.setBold(chatOutput.getInputAttributes(), true);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), sender.getName(), chatOutput.getInputAttributes());
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
		}
		catch (Exception e)
		{
			Vars.getLogger().severe("Unable to display chat:");
			e.printStackTrace();
		}
	}

	public void displayPrivateChat(Player sender, Player recipient, String message)
	{
		if (new PrivateChatPacket(getConnection()).send(sender, recipient, message))
		{
			Icon senderIcon = rightsIcons[sender.getRights()];
			Icon recpIcon = rightsIcons[recipient.getRights()];
			try
			{
				Style iconOnly = pmOutput.getStyledDocument().getStyle("iconOnly");
				if (iconOnly == null)
				{
					iconOnly = pmOutput.getStyledDocument().addStyle("iconOnly", null);
				}
				StyleConstants.setIcon(iconOnly, senderIcon);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), "dummytext", iconOnly);
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), sender.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " to ", null);
				if(recipient.getRights() > 0)
				{
					StyleConstants.setIcon(iconOnly, recpIcon);
					pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), "dummytext", iconOnly);
				}
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), recipient.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
			}
			catch (Exception e)
			{
				Vars.getLogger().severe("Unable to display chat:");
				e.printStackTrace();
			}
			//pmOutput.append("[" + rightsIcons[sender.getRights()] + " " + sender.getName() + " to " + rightsIcons[recipient.getRights()] + recipient.getName() + "]: " + message); // TODO beautify
		}
	}

	/**
	 * Handles menu actions.
	 * @param i the index of the JMenuItem that was clicked
	 */
	public void menuActionPerformed(int i, ActionEvent e)
	{
		switch (i)
		{
			case 0: //Connect with dialog
				tryExtendedConnect();
				break;
			case 1: //Connect to default
				doCommand("connect"); // TODO make proper -- this is sloppy
				break;
			case 2:
				// TODO Send the file request packet here
				break;
			case 3:
				if (JOptionPane.showConfirmDialog(this.getOwner(), "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					System.exit(0);
				}
				break;
			case 4:
				displayReflectionManager();
				break;
		}
	}

	private void tryExtendedConnect()
	{
		final JDialog jd = new JDialog();
		jd.setTitle("Connect");
		jd.setLocationRelativeTo(null);
		final JTextField connectInput = new JTextField();
		final JTextField portInput = new JTextField();
		final JPasswordField passwordInput = new JPasswordField();
		final JOptionPane jo = new JOptionPane(new Object[]
			   {
				   "Enter IP, port, and password.", connectInput, portInput, passwordInput
			   }, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new Object[]
			   {
				   "OK", "Cancel"
			   }, "OK");
		jd.setContentPane(jo);
		jd.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// Close the window here, but save the values entered
		jd.addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent we)
			{
				jo.setValue(JOptionPane.CLOSED_OPTION);
			}
		});
		jo.addPropertyChangeListener(
			   new java.beans.PropertyChangeListener()
			   {

				   public void propertyChange(java.beans.PropertyChangeEvent e)
				   {
					   String prop = e.getPropertyName();
					   if (isVisible() && (e.getSource() == jo) && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)))
					   {
						   Object value = jo.getValue();
						   if (value == JOptionPane.UNINITIALIZED_VALUE)
						   {
							   return;
						   }
						   jo.setValue(JOptionPane.UNINITIALIZED_VALUE);
						   if (value.equals("OK"))
						   {
							   String IP = connectInput.getText();
							   String port = portInput.getText();
							   String password = new String(passwordInput.getPassword());
							   if (IP.length() == 0 || port.length() == 0 || password.length() == 0)
							   {
								   JOptionPane.showMessageDialog(jd, "Not all fields were filled out properly.", "Error", JOptionPane.ERROR_MESSAGE);
								   return;
							   }
							   try
							   {
								   port = "" + Integer.parseInt(portInput.getText());
							   }
							   catch (Exception err)
							   {
								   JOptionPane.showMessageDialog(jd, "The port must be numeric!", "Error", JOptionPane.ERROR_MESSAGE);
								   portInput.setText(null);
								   portInput.requestFocusInWindow();
								   return;
							   }
							   if (!IP.contains(".") && !IP.toLowerCase().equals("localhost"))
							   {
								   JOptionPane.showMessageDialog(jd, "The IP entered was invalid.", "Error", JOptionPane.ERROR_MESSAGE);
								   connectInput.setText(null);
								   connectInput.requestFocusInWindow();
								   return;
							   }
							   jd.dispose();
							   doCommand("connect " + IP + " " + port + " " + password);
						   }
						   else
						   {
							   jd.dispose();
						   }
					   }
				   }
			   });
		jd.setResizable(false);
		jd.pack();
		jd.setModal(true);
		jd.setVisible(true);
		connectInput.requestFocusInWindow();
	}

	/**
	 * Updates debugOutput when System.out is written to
	 * @see redirectSystemStreams()
	 * @param text the text to append to debugOutput
	 */
	private void updateTextArea(final String text)
	{
		// Supposedly thread-safe, apparently not--keep on EDT
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				cal = java.util.Calendar.getInstance();
				// Append the debug, but ensure it displays properly
				if (debugOutput != null && debugOutput.getDocument() != null)
				{
					debugOutput.append(!text.contains(System.getProperty("line.separator")) ? "[" + sdf.format(cal.getTime()) + "]" + " " + text : text);
					debugOutput.setCaretPosition(debugOutput.getDocument().getLength());
				}
			}
		});
	}
	private JLabel imageLabel;
	private Icon[] rightsIcons;// = new Icon[5];

	/**
	 * Builds the GUI components
	 */
	private void initComponents()
	{
		jPanel1 = new JPanel();
		try
		{
			imageLabel = new JLabel();
			imageLabel.setIcon(new ImageIcon(this.getClass().getResource("resources/interfacetest.png")));
			rightsIcons = new ImageIcon[5];
			for (int i = 0; i < rightsIcons.length; i++)
			{
				rightsIcons[i] = new ImageIcon(this.getClass().getResource("resources/level_" + i + ".png"));
			}
		}
		catch (Exception e)
		{
			log("Critical error while loading GUI:");
			e.printStackTrace();
		}
		progress = new JProgressBar();
		ipDisp = new JLabel();
		defLabel = new JLabel();
		thpDisp = new JLabel();
		atkLabel = new JLabel();
		jTabbedPane1 = new JTabbedPane();
		jScrollPane1 = new JScrollPane();
		chatOutput = new LinkingJTextPane();
		jScrollPane2 = new JScrollPane();
		pmOutput = new JTextPane();
		jScrollPane3 = new JScrollPane();
		debugOutput = new JTextArea();
		pkpDisp = new JLabel();
		chatInput = new JTextField();
		rangeDisp = new JLabel();
		coordsLabel = new JLabel();
		jScrollPane4 = new JScrollPane();
		MouseListener mouseListener = new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				Object x = playerListComponent.getSelectedValue();
				if (e.getClickCount() == 1)
				{
					if (x != null)
					{
						//Load stats here
					}
				}
				else
				{
					if (e.getClickCount() == 1)
					{
						chatOutput.setText("/pm " + x + " ");
						chatInput.requestFocus();
					}
				}
			}
		};
		playerListComponent = new JList();
		playerList = new DefaultListModel();
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
		jmiConnect.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				menuActionPerformed(0, evt);
			}
		});
		jmiQConnect.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				menuActionPerformed(1, evt);
			}
		});
		jmiOpen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				menuActionPerformed(2, evt);
			}
		});
		jmiExit.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				menuActionPerformed(3, evt);
			}
		});
		jmiNew.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				menuActionPerformed(4, evt);
			}
		});
		progress.setBounds(660, 319, 79, 14);
		progress.setStringPainted(true);
		progress.setString("Ready");
		ipDisp.setHorizontalAlignment(SwingConstants.CENTER);
		ipDisp.setText("n/a");
		defLabel.setText("Defence: ");
		thpDisp.setHorizontalAlignment(SwingConstants.CENTER);
		thpDisp.setText("n/a");
		atkLabel.setText("Attack: ");
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(132, 254, 739, 335);
		jTabbedPane1.setBounds(0, 0, 739, 335);
		jTabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);
		jTabbedPane1.setAutoscrolls(true);
		layeredPane.add(jTabbedPane1, 1);
		chatOutput.setEditable(false);
		chatOutput.addLinkEventListener(new LinkEventListener()
		{

			@Override
			public void linkClicked(String linkText)
			{
				chatInput.setText("/pm " + linkText + " ");
				chatInput.requestFocus();
			}
		});
		jScrollPane1.setViewportView(chatOutput);
		jTabbedPane1.addTab("Chat", jScrollPane1);
		pmOutput.setEditable(false);
		jScrollPane2.setViewportView(pmOutput);
		jTabbedPane1.addTab("PM", jScrollPane2);
		debugOutput.setColumns(20);
		debugOutput.setRows(5);
		debugOutput.setEditable(false);
		debugOutput.setLineWrap(true);
		jScrollPane3.setViewportView(debugOutput);
		jTabbedPane1.addTab("Debug", jScrollPane3);
		map = new RSMap(737, 318);//738, 315
		// is this changing the size..?
		jTabbedPane1.addTab("Map", map);
		jTabbedPane1.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent evt)
			{
				if (jTabbedPane1.getForegroundAt(jTabbedPane1.getSelectedIndex()) == Color.red)
				{
					jTabbedPane1.setForegroundAt(jTabbedPane1.getSelectedIndex(), Color.black);
				}
			}
		});
		pkpDisp.setHorizontalAlignment(SwingConstants.CENTER);
		pkpDisp.setText("n/a");
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("Settings.NS"));
					obj.writeObject(getUser().getSettings());
				}
				catch (Exception fe)
				{
					// It's too late to do anything here
				}
				System.exit(0);
			}
		});
		chatInput.addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent ke)
			{
				if (ke.getKeyCode() == 38)
				{
					if (historyIndex > 1)
					{
						historyIndex--;
					}
					chatInput.setText((String) chatHistory.get(historyIndex));
				}
				else
				{
					if (ke.getKeyCode() == 40)
					{
						if (historyIndex < (chatHistory.size() + 1))
						{
							historyIndex++;
							chatInput.setText((String) chatHistory.get(historyIndex));
						}
						else
						{
							chatInput.setText(null);
						}
					}
				}
			}
		});
		chatInput.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				String message = chatInput.getText();
				chatInput.setText("");
				if (!message.equals(""))
				{
					if (!message.startsWith("/"))
					{
						displayPublicChat(getUser().getSettings().getPlayer(), message);
						new PublicChatPacket(getConnection()).send(getUser().getSettings().getPlayer(), message);
					}
					else
					{
						doCommand(message.substring(1, message.length()));
					}
					if (historyIndex > 250)
					{
						historyIndex = 1;
						chatHistory.clear();
					}
					if (chatHistory.isEmpty())
					{
						historyIndex = 1;
					}
					else
					{
						historyIndex = chatHistory.size() + 1;
					}
					if (chatHistory.get(historyIndex - 1) == null || !chatHistory.get(historyIndex - 1).equalsIgnoreCase(message))
					{
						chatHistory.put(historyIndex++, message);
					}
					else
					{
						Vars.getLogger().fine("History match, input not saved");
					}
				}
			}
		});
		boundPacketPanel = new ScrollablePanel(new GridLayout(0, 4, 4, 2));
		boundPacketPanel.setBackground(new Color(0, 0, 250, 0));
		boundPacketPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
		packetPanelContainer = new JScrollPane(boundPacketPanel);
		packetPanelContainer.getViewport().setOpaque(false);//setBackground(new Color(0, 0, 250, 10));
		packetPanelContainer.setBackground(new Color(0, 0, 250, 0));
		packetPanelContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Border border = BorderFactory.createEmptyBorder();
		Border border2 = BorderFactory.createLineBorder(new Color(99, 130, 191), 1);
		packetPanelContainer.setBorder(border2);
		packetPanelContainer.setBounds(390, 48, 481, 190);
		jPanel1.add(packetPanelContainer);
		chatInput.setBounds(164, 319, 496, 15);
		layeredPane.add(chatInput, 0);
		layeredPane.add(progress);
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
		restartBut.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				restartButActionPerformed(evt);
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
		playerList.addListDataListener(new ListDataListener()
		{

			public void intervalAdded(ListDataEvent e)
			{
				consumeEvent(((DefaultListModel) e.getSource()), (String) ((DefaultListModel) e.getSource()).elementAt(e.getIndex0()).toString(), true);
			}

			public void intervalRemoved(ListDataEvent e)
			{
				consumeEvent(((DefaultListModel) e.getSource()), (String) ((DefaultListModel) e.getSource()).elementAt(e.getIndex0()).toString(), false);
			}

			public void contentsChanged(ListDataEvent e)
			{
			}

			private void consumeEvent(DefaultListModel source, String item, boolean add)
			{
				playerCount.setText("Players - " + source.getSize());
				if (add)
				{
					chatOutput.addMatch(item);
				}
				else
				{
					chatOutput.removeMatch(item);
				}
			}
		});
		playerListComponent.setModel(playerList);
		GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
			   jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPane4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE).addComponent(playerCount, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE).addComponent(restartBut, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jLabel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE).addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addGap(10, 10, 10).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(thpLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(thpDisp, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addComponent(hpLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(hpDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(rangeLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(rangeDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(mageLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(mageDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(defLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(defDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(strLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(strDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(atkLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(atkDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(pkpLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(pkpDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(ipLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(ipDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(pwLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(pwDisp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(coordsLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(coordDisp, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))))).addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))))).addContainerGap()));
		jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[]
			   {
				   atkLabel, coordsLabel, defLabel, hpLabel, ipLabel, mageLabel, pkpLabel, pwLabel, rangeLabel, strLabel, thpLabel
			   });
		jPanel1Layout.setVerticalGroup(
			   jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(playerCount).addComponent(jLabel2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(coordsLabel).addComponent(coordDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(pwLabel).addComponent(pwDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(ipLabel).addComponent(ipDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(pkpLabel).addComponent(pkpDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(atkLabel).addComponent(atkDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(strLabel).addComponent(strDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(defLabel).addComponent(defDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(mageLabel).addComponent(mageDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(rangeLabel).addComponent(rangeDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(hpLabel).addComponent(hpDisp)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(thpLabel).addComponent(thpDisp))).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel5).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))).addGap(14, 14, 14)).addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(restartBut)).addContainerGap()));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			   layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
			   layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		jScrollPane1.setBorder(border);
		jScrollPane1.setViewportBorder(border);
		chatOutput.setBorder(border);
		jScrollPane2.setBorder(border);
		jScrollPane2.setViewportBorder(border);
		pmOutput.setBorder(border);
		jScrollPane3.setBorder(border);
		jScrollPane3.setViewportBorder(border);
		debugOutput.setBorder(border);

		jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		jScrollPane1.setOpaque(false);
		jScrollPane2.setOpaque(false);
		jScrollPane3.setOpaque(false);
		chatOutput.setOpaque(false);
		pmOutput.setOpaque(false);
		debugOutput.setOpaque(false);
		jTabbedPane1.setOpaque(false);
		jScrollPane1.getViewport().setOpaque(false);
		jScrollPane2.getViewport().setOpaque(false);
		jScrollPane3.getViewport().setOpaque(false);
		pack();
		imageLabel.setSize(jPanel1.getSize());
		jPanel1.add(imageLabel);
	}
}
