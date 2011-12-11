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
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.reflection.ReflectionPacketContainer;

/**
 * Contains the logic for driving the UI
 *
 * @author Whired
 */
public class GhostFrameImpl extends ClientGhostFrame {
	
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
				ReflectionPacketContainer.invoke(packet, getSessionManager().getConnection());
			}
		});
		boundPacketPanel.add(button);
	}
	
	@Override
	public void restartButActionPerformed(ActionEvent evt) {
		getSessionManager().getConnection().sendPacket(88);
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
		if (new PrivateChatPacket().send(getSessionManager().getConnection(), sender, recipient, message)) {
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
