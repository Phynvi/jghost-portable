package org.whired.ghost.net;

import java.io.IOException;
import org.whired.ghost.Vars;
import org.whired.ghost.net.Connection.DisconnectCallback;

/**
 * An easy wrapper to read data from an InputStream.
 * @author Whired
 */
public class WrappedInputStream {

	/**
	 * Constructs the InputStream wrapper.
	 * @param is the InputStream to wrap.
	 */
	public WrappedInputStream(java.io.InputStream is) {
		this.is = is;
	}
	/**
	 * The InputStream to wrap.
	 */
	private java.io.InputStream is = null;
	/**
	 * The SessionManager that is managing this stream
	 */
	private SessionManager manager = null;
	/**
	 * Ensures that timeouts do not occur during inactivity.
	 */
	public boolean expectingNewPacket = false;
	/** Whether or not to enforce a timeout system; true by default. */
	public boolean enforceTimeout = true;

	/**
	 * Reads a boolean from the stream.
	 * @return bool the boolean that was read.
	 */
	public boolean readBoolean() throws java.io.IOException, ClassNotFoundException {
		return ((Boolean) readObject()).booleanValue();
	}

	/**
	 * Reads a String from the stream.
	 * @return the String that was read.
	 */
	public String readString() throws java.io.IOException, ClassNotFoundException {
		return (String) readObject();
	}

	/**
	 * Reads an int from the stream.
	 * @return the int that was read.
	 */
	public int readInt() throws java.io.IOException, ClassNotFoundException {
		return ((Integer) readObject()).intValue();
	}

	/**
	 * Reads and saves a file sent from stream.
	 * @param saveDir Specifies the directory to save the file to.
	 */
	public void readFile(String saveDir) throws java.io.IOException, ClassNotFoundException {
		String fileName = (String) readObject();
		int size = readInt();
		byte[] buffer;
		if (size < 1000) {
			buffer = new byte[size];
		}
		else {
			buffer = new byte[1000];
		}
		int received = 0;
		int writeIndex = 0;
		int temp = 0;
		java.io.FileOutputStream fs = null;
		try {
			fs = new java.io.FileOutputStream(new java.io.File(saveDir + fileName));
		}
		catch (Exception noSave) {
			System.out.println("Unable to save file to " + saveDir + fileName + ":");
			noSave.printStackTrace();
			disposeData(size);
			return;
		}
		try {
			while (received != size) {
				if (size - received < buffer.length) {
					buffer = new byte[size - received];
				}
				writeIndex = received;
				temp = is.read(buffer, 0, buffer.length);
				if (temp != -1) {
					received += temp;
				}
				else {
					continue;// Continue, break, wipe..I don't know.
				}
				fs.write(buffer, 0, received - writeIndex);
				fs.flush();
			}
			fs.close();
		}
		catch (Exception IOE) {
			System.out.println("Error writing file:");
			IOE.printStackTrace();
			disposeData(size - received);
		}
	}

	public int read = 0;
	
	/**
	 * Reads any Object from the stream.
	 * @return the Object that was read.
	 */
	public Object readObject() throws java.io.IOException, ClassNotFoundException {
		try {
			Object object = null;
			object = new java.io.ObjectInputStream(is){  public byte readByte() throws IOException{ byte b = super.readByte(); read +=b; return b; }  }.readObject();
			System.out.println("Read "+read+" bytes.");
			read = 0;
			return object;
		}
		catch (java.io.IOException e) {
			disconnectCallback.disconnected(e.toString());
			throw e;
		}
		catch (ClassNotFoundException e) {
			disconnectCallback.disconnected(e.toString());
			throw e;
		}
	}

	public int readByte() throws java.io.IOException, ClassNotFoundException {
		System.out.print("ReadByte: ");
		int x = is.read();
		if(x == -1) {
			disconnectCallback.disconnected("End of stream");
			throw new java.io.IOException("End of stream");
		}
		return x;
	}

	/**
	 * Disposes any information that can no longer be used.
	 * @param length specifies the number of bytes to dispose.
	 */
	private void disposeData(int length) {
		try {
			long skipped = 0;
			while (skipped != length) {
				skipped += is.skip(length);
			}
			System.out.println("Successfully disposed " + skipped + "/" + length + " bytes of unusable data.");
		}
		catch (Exception ni) {
			disconnectCallback.disconnected("Stream malformed");
		}
	}

	private DisconnectCallback disconnectCallback;
	
	/**
	 * Sets the SessionManager for this stream
	 */
	protected void setDisconnectCallback(DisconnectCallback c) {
		this.disconnectCallback = c;
	}

	/**
	 * Closes the stream when the session becomes invalid.
	 */
	private void closeStream() {
		try {
			this.is.close();
			Vars.getLogger().fine("Native inputstream closed.");
		}
		catch (Exception e) {
			Vars.getLogger().warning("Unable to close inputstream:");
			e.printStackTrace();
		}
	}

	/**
	 * Work to do before destroying this object.
	 */
	public void destruct() {
		closeStream();
		Vars.getLogger().fine("Destruct complete.");
	}
}
