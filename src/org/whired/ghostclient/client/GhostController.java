package org.whired.ghostclient.client;

/**
 * A default GHOST client
 * @author Whired
 */
public abstract class GhostController implements GhostClient {
	
	private final GhostClient view;
	private final GhostClient model;
	
	public GhostController(GhostClient model, GhostClient view) {
		this.model = model;
		this.view = view;
		//this.model.setController(this);
		//this.view.setController(this);
		//view.setFrame(this);
		//addModule(new PublicChatModule());
	}
	
	/*
	
	
	private final GhostClient view = new DefaultGhostClientView() {

		@Override
		protected void restartButActionPerformed(ActionEvent evt) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void menuActionPerformed(int i, ActionEvent e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void windowClosing() {
			try {
				SessionSettings.saveToDisk(getUser().getSettings());
			}

			catch (FileNotFoundException ex) {
				Logger.getLogger(DefaultController.class.getName()).log(Level.SEVERE, null, ex);
			}			catch (IOException ex) {
				Logger.getLogger(DefaultController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		@Override
		public void publicChatSent(String message) {
			displayPublicChat(getUser().getSettings().getPlayer(), message);
			new PublicChatPacket().send(getSessionManager().getConnection(), getUser().getSettings().getPlayer(), message);
		}

		@Override
		public void handleCommand(String command) {
			try {
				getCommandHandler().handleInput(command);
			}

			catch (CommandMalformedException ex) {
				Logger.getLogger(DefaultController.class.getName()).log(Level.SEVERE, "Invalid input:", ex);
			}			catch (CommandNotFoundException ex) {
				Logger.getLogger(DefaultController.class.getName()).log(Level.SEVERE, "Command not found:", ex);
			}
		}

		@Override
		public Rank getRankForPlayer(Player player) {
			return getRankHandler().rankForLevel(player.getRights());
		}
	};
	
	private final PlayerList playerList = new ClientPlayerList(this) {

		@Override
		public void playerAdded(Player player) {
			view.getPlayerListModel().addElement(player);
		}

		@Override
		public void playerRemoved(Player player) {
			view.getPlayerListModel().removeElement(player);
		}

		@Override
		public Player[] getPlayers() {
			DefaultListModel dlm = view.getPlayerListModel();
			Player[] players = new Player[dlm.getSize()];
			dlm.copyInto(players);
			return players;
		}
		
	};

	@Override
	public void displayPublicChat(Player sender, String message) {
		getModuleHandler().publicMessageLogged(getUser().getSettings().getPlayer(), message);
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		if (new PrivateChatPacket().send(getSessionManager().getConnection(), sender, recipient, message)) {
			Icon senderIcon = getRankHandler().rankForLevel(sender.getRights()).getIcon();
			Icon recpIcon = getRankHandler().rankForLevel(recipient.getRights()).getIcon();
			try {
				Style iconOnly = view.getPrivateOutput().getStyledDocument().getStyle("iconOnly");
				if (iconOnly == null)
					iconOnly = view.getPrivateOutput().getStyledDocument().addStyle("iconOnly", null);
				StyleConstants.setIcon(iconOnly, senderIcon);
				view.getPrivateOutput().getStyledDocument().insertString(view.getPrivateOutput().getStyledDocument().getLength(), " ", iconOnly);
				StyleConstants.setBold(view.getPrivateOutput().getInputAttributes(), true);
				view.getPrivateOutput().getStyledDocument().insertString(view.getPrivateOutput().getStyledDocument().getLength(), sender.getName(), view.getPrivateOutput().getInputAttributes());
				view.getPrivateOutput().getStyledDocument().insertString(view.getPrivateOutput().getStyledDocument().getLength(), " to ", null);
				if (recipient.getRights() > 0) {
					StyleConstants.setIcon(iconOnly, recpIcon);
					view.getPrivateOutput().getStyledDocument().insertString(view.getPrivateOutput().getStyledDocument().getLength(), " ", iconOnly);
				}
				StyleConstants.setBold(view.getPrivateOutput().getInputAttributes(), true);
				view.getPrivateOutput().getStyledDocument().insertString(view.getPrivateOutput().getStyledDocument().getLength(), recipient.getName(), view.getPrivateOutput().getInputAttributes());
				view.getPrivateOutput().getStyledDocument().insertString(view.getPrivateOutput().getStyledDocument().getLength(), ": " + message + "\n", null);
			}
			catch (Exception e) {
				Logger.getLogger(DefaultController.class.getName()).log(Level.SEVERE, "Unable to display chat:", e);
			}
		}
	}

	@Override
	public void displayDebug(Level level, String message) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected void bindPacket(ReflectionPacketContainer container) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void sessionOpened() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.getConnectionStateLabel().setText("Connected");
				view.getConnectionStateLabel().setOpaque(true);
				view.getConnectionStateLabel().setBackground(new Color(99, 130, 191, 120));
				view.requestRepaint();
			}
		});
	}

	@Override
	public void sessionClosed(String reason) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.getConnectionStateLabel().setText("Disconnected");
				view.getConnectionStateLabel().setOpaque(false);
			}
		});
	}
	
	@Override
	public PlayerList getPlayerList() {
		return this.playerList;
	}
	
	// TODO does this need to go here?
	@Override
	public void packetReceived(GhostPacket packet) {
		getModuleHandler().packetReceived(packet);
	}

	@Override
	public void windowClosing() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setInputText(String text) {
		throw new UnsupportedOperationException("Not supported yet.");
	}*/
}
