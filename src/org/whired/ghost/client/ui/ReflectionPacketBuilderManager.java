package org.whired.ghost.client.ui;

import java.awt.Frame;
import org.whired.ghost.net.Connection;
import org.whired.ghost.net.reflection.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

// This class will manage the ReflectionPacketBuilder dialogs.
// They are getting confused and need to be handled from a different layer
public class ReflectionPacketBuilderManager
{

	/** The Frame to display dialogs on top of */
	private final Frame owner;
	/** The Connection to use while responding */
	private final Connection connection;
	private ReflectionPacketBuilder focusedDialog; // TODO rm
	private PacketLoader loader;

	public ReflectionPacketBuilderManager(final Frame owner, final Connection connection)
	{
		this.owner = owner;
		this.connection = connection;
	}

	public void performReflection()
	{
		ArrayList<Accessor> accessorChain = this.getAccessorChain("Choose an Accessor", "org.hyperion.rs2.model.World", true);
		ReflectionPacketContainer.invoke(accessorChain, connection);
		while (true)
		{
			if (JOptionPane.showConfirmDialog(owner, "Bind packet for later use?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
			{
				String packetName = "";
				while (true)
				{
					packetName = JOptionPane.showInputDialog(owner, "Enter name for packet:", "Bind Packet", JOptionPane.PLAIN_MESSAGE).replaceAll("[^A-Za-z]", "");
					if (packetName.isEmpty())
					{
						JOptionPane.showMessageDialog(owner, "The name entered was invalid.", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					else
					{
						break;
					}
				}
				try
				{
					ReflectionPacketContainer container = new ReflectionPacketContainer(packetName, accessorChain);
					ReflectionPacketContainer.saveContainer(container);
					if (loader != null)
					{
						loader.loadPacket(container);
					}
					break;
				}
				catch (Exception e)
				{
					if (JOptionPane.showConfirmDialog(owner, "Unable to save packet: " + e.getMessage() + System.getProperty("line.separator") + System.getProperty("line.separator") + "Retry?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
					{
						break;
					}
				}
			}
			else
			{
				break;
			}
		}
	}

	public ArrayList<Accessor> getAccessorChain(String title, String typeMatch, boolean staticOnly)
	{
		ArrayList<Accessor> accessorChain = new ArrayList<Accessor>();
		boolean finished = false;
		while (!finished)
		{
			focusedDialog = new ReflectionPacketBuilder(title, this.owner);
			System.out.println("Sending packet 3 for " + typeMatch);
			connection.sendPacket(3, typeMatch, staticOnly);
			focusedDialog.setVisible(true);
			finished = focusedDialog.fromDoneButton;
			Accessor a = focusedDialog.getSelectedAccessor();
			accessorChain.add(a);
			if (a.isMethod())
			{
				Method meth = (Method) a;
				if (meth.getParamTypeNames().length > 0)
				{
					for (String s : meth.getParamTypeNames())
					{
						meth.addArgument(getAccessorChain("Choose a value for argument (type '" + s + "')", "org.hyperion.rs2.model.World", true)); //TODO REMOVE HARDCODE
					}
				}
				finished = finished || a.getType().equals("void");
				//System.out.println("Finished?: "+finished);
			}
			typeMatch = a.getType();
			staticOnly = false;
		}
		return accessorChain;
	}

	public void setList(ArrayList<Accessor> list)
	{
		focusedDialog.setList(list);
	}

	/**
	 * Sets the loader for created packets.
	 * 
	 * @param packetLoader the loader to notify when a packet should be loaded
	 */
	public void setPacketLoader(final PacketLoader packetLoader)
	{
		this.loader = packetLoader;
	}
}
