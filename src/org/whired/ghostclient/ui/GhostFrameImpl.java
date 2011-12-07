package org.whired.ghostclient.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.reflection.Accessor;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;

/**
 * Contains the logic for driving the UI
 *
 * @author Whired
 */
public class GhostFrameImpl extends ClientGhostFrame {
	
	public GhostFrameImpl() {
		
	}
	/*
	public GhostFrameImpl() {
		//initCommands();
		registerPacket(new GhostPacket(PacketType.INVOKE_ACCESSOR) {

			@Override
			public boolean receive(Connection connection) {
				try {
					Accessor a = (Accessor) connection.getInputStream().readObject();
					System.out.println(a.invoke());
				}
				catch (Exception ex) {
					Vars.getLogger().warning("Unable to invoke accessor: "+ex);
					ex.printStackTrace();
				}
				return true;
			}
			
		});
	}*/
//
//	/**
//	 * Creates a new ghost frame with the specified set of commands
//	 * @param commands the commands to registerRank
//	 */
//	public GhostFrameImpl(Command[] commands) {
//		commandHandler.registerCommands(commands);
//	}
//	
//	/**
//	 * Creates a new ghost frame with the specified set of modules
//	 * @param modules the modules to registerRank
//	 */
//	public GhostFrameImpl(Module[] modules) {
//		
//	}
//	
//	/**
//	 * Creates a new ghost frame with the specified set of packets
//	 * @param packets the packets to registerRank
//	 */
//	public GhostFrameImpl(GhostPacket[] packets) {
//		packetHandler.registerPackets(packets);
//	}
//	
//	/**
//	 * Creates a new ghost frame with the specified set of commands and modules
//	 * @param commands the commands to registerRank
//	 * @param modules the modules to registerRank
//	 */
//	public GhostFrameImpl(Command[] commands, Module[] modules) {
//		commandHandler.registerCommands(commands);
//	}
//	
//	/**
//	 * Creates a new ghost frame with the specified set of commands and packets
//	 * @param commands the commands to registerRank
//	 * @param packets the packets to registerRank
//	 */
//	public GhostFrameImpl(Command[] commands, GhostPacket[] packets) {
//		commandHandler.registerCommands(commands);
//		packetHandler.registerPackets(packets);
//	}
//	
//	/**
//	 * Creates a new ghost frame with the specified set of modules and packets
//	 * @param modules the modules to registerRank
//	 * @param packets the packets to registerRank
//	 */
//	public GhostFrameImpl(Module[] modules, GhostPacket[] packets) {
//		
//	}
//	
//	/**
//	 * Creates a new ghost frame with the specified set of commands, modules, and packets
//	 * @param commands the commands to registerRank
//	 * @param modules the modules to registerRank
//	 * @param packets the packets to registerRank
//	 */
//	public GhostFrameImpl(Command[] commands, Module[] modules, GhostPacket[] packets) {
//		
//	}
	
	/**
	 * Initializes the commands that this frame will utilize
	 */
	public void initCommands() {
		
		loadBoundPackets();
	}

	/**
	 * Loads saved packets from the disk and binds them
	 */
	private void loadBoundPackets() {
		for (ReflectionPacketContainer packet : ReflectionPacketContainer.loadAllContainers())
			bindPacket(packet);
	}
	
	@Override
	protected void bindPacket(final ReflectionPacketContainer packet) {
		JRoundedButton button = new JRoundedButton(packet.packetName);
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				ReflectionPacketContainer.invoke(packet, getConnection());
			}
		});
		boundPacketPanel.add(button);
	}
	
	@Override
	public void restartButActionPerformed(ActionEvent evt) {
		getConnection().sendPacket(88);
		//playerList.addElement(new MapPlayer("whired", curRight--, 2000, 2000, map)); // TODO actual implementation
	}

	/**
	 * Displays a chat message to {@code chatOutput}
	 * @param rights the rights of he user
	 * @param name the name of the user
	 * @param message the message the user sent
	 */
	@Override
	public void displayPublicChat(Player sender, String message) {
		Icon i = rankHandler.rankForLevel(sender.getRights()).getIcon();
		try {
			Style iconOnly = chatOutput.getStyledDocument().getStyle("iconOnly");
			if (iconOnly == null)
				iconOnly = chatOutput.getStyledDocument().addStyle("iconOnly", null);
			StyleConstants.setIcon(iconOnly, i);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), " ", iconOnly);
			StyleConstants.setBold(chatOutput.getInputAttributes(), true);
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), sender.getName(), chatOutput.getInputAttributes());
			chatOutput.getStyledDocument().insertString(chatOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
		}
		catch (Exception e) {
			Vars.getLogger().severe("Unable to display chat:");
			e.printStackTrace();
		}
	}

	@Override
	public void displayPrivateChat(Player sender, Player recipient, String message) {
		if (new PrivateChatPacket().send(getConnection(), sender, recipient, message)) {
			Icon senderIcon = rankHandler.rankForLevel(sender.getRights()).getIcon();
			Icon recpIcon = rankHandler.rankForLevel(recipient.getRights()).getIcon();
			try {
				Style iconOnly = pmOutput.getStyledDocument().getStyle("iconOnly");
				if (iconOnly == null)
					iconOnly = pmOutput.getStyledDocument().addStyle("iconOnly", null);
				StyleConstants.setIcon(iconOnly, senderIcon);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " ", iconOnly);
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), sender.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " to ", null);
				if (recipient.getRights() > 0) {
					StyleConstants.setIcon(iconOnly, recpIcon);
					pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), " ", iconOnly);
				}
				StyleConstants.setBold(pmOutput.getInputAttributes(), true);
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), recipient.getName(), pmOutput.getInputAttributes());
				pmOutput.getStyledDocument().insertString(pmOutput.getStyledDocument().getLength(), ": " + message + "\n\r", null);
			}
			catch (Exception e) {
				Vars.getLogger().severe("Unable to display chat:");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void displayDebug(Level level, String message) {
		Vars.getLogger().log(level, message);
	}
	
	@Override
	public void menuActionPerformed(int i, ActionEvent e) {
		switch (i) {
			case 0: //Connect with dialog
				tryExtendedConnect();
				break;
			case 1: //Connect to default
				doCommand("connect");
				break;
			case 2:
				// TODO Send the file request packet here
				break;
			case 3:
				if (JOptionPane.showConfirmDialog(ghostJFrame, "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					System.exit(0);
				break;
			case 4:
				displayReflectionManager();
				break;
		}
	}
}
