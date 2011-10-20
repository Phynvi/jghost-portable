package org.whired.ghost.net.reflection;

import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Simple container for {@code ArrayList<Accessor>} to be serialized and deserialized
 *
 * @author Whired
 */
public class ReflectionPacketContainer implements java.io.Serializable {

	private final static transient String SAVE_DIR = "." + System.getProperty("file.separator") + "packets" + System.getProperty("file.separator");
	public final ArrayList<Accessor> accessorChain;
	public final String packetName;

	/**
	 * Creates a new instance of ReflectionPacketContainer
	 *
	 * @param accessorChain the chain of accessors to contain
	 */
	public ReflectionPacketContainer(String packetName, ArrayList<Accessor> accessorChain) {
		this.packetName = packetName;
		this.accessorChain = accessorChain;
	}

	/**
	 * Saves the specified chain of accessors to the local disk
	 *
	 * @param accessorChain the chain of accessors to save
	 */
	public static void saveContainer(ReflectionPacketContainer c) throws java.io.IOException {
		File dir = new File(ReflectionPacketContainer.SAVE_DIR);
		if (!dir.exists())
			if (!dir.mkdir())
				throw new java.io.IOException("Unable to create save directory");
		ObjectOutputStream out = null;
		out = new ObjectOutputStream(new FileOutputStream(ReflectionPacketContainer.SAVE_DIR + c.packetName));
		out.writeObject(c);
		out.close();
	}

	/**
	 * Loads a chain of accessors based on the supplied packet name.
	 *
	 * @param packetName the name of the packet that was bound
	 * @return the chain of accessors that was loaded
	 *
	 * @throws java.io.IOException if the file could not be found
	 * @throws ClassNotFoundException when the loaded object can not  be cast to {@code ArrayList<Accessor>}
	 */
	public static ReflectionPacketContainer loadContainer(String packetName) throws java.io.IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(ReflectionPacketContainer.SAVE_DIR + packetName));
		ReflectionPacketContainer accessorChain = (ReflectionPacketContainer) in.readObject();
		in.close();
		return accessorChain;
	}

	/**
	 * Loads all containers that might be saved to the local disk
	 *
	 * @return the list of containers that were loaded
	 */
	public static ArrayList<ReflectionPacketContainer> loadAllContainers() {
		ArrayList<ReflectionPacketContainer> list = new ArrayList<ReflectionPacketContainer>();
		File dir = new File(ReflectionPacketContainer.SAVE_DIR);
		if (dir.exists()) {
			String[] files = dir.list();
			for (String file : files)
				try {
					list.add(ReflectionPacketContainer.loadContainer(file));
				}
				catch (Exception e) {
					//e.printStackTrace();
					Vars.getLogger().warning("Failed to load " + file + ": " + e.toString());
					//System.out.println("Failed to load "+file+": "+e.toString());
				}
		}
		return list;
	}

	public static void invoke(ReflectionPacketContainer container, Connection connection) {
		invoke(container.accessorChain, connection);
	}

	public static void invoke(ArrayList<Accessor> accessorChain, Connection connection) {
		connection.sendPacket(5, accessorChain, true);
	}
}
