package org.whired.ghost.net;

import java.io.IOException;
import java.util.logging.Level;

import org.whired.ghost.constants.Vars;

/**
 * An easy-to-use OutputStream wrapper that provides methods to write to a
 * stream.
 * 
 * @author Whired
 */
public class WrappedOutputStream {

	private java.io.OutputStream os = null;

	/**
	 * Constructs the OutputStream wrapper
	 * 
	 * @param os_ the OutputStream to wrap
	 */
	public WrappedOutputStream(java.io.OutputStream os_) {
		this.os = os_;
	}

	/**
	 * Writes a byte[] to the stream
	 * 
	 * @param b the byte array to write
	 */
	public void writeBytes(byte[] b) throws IOException {
		os.write(b, 0, b.length);
	}

	/**
	 * Writes a file to the stream.
	 * 
	 * @param theFile the file to attempt to write.
	 */
	public void writeFile(String theFile) throws IOException {
		java.io.File f = new java.io.File(theFile);
		if (!f.exists()) {
			System.out.println("Could not write " + theFile + " because it does not exist.");
			writeBoolean(false);
			return;
		}
		writeBoolean(true);
		writeString(f.getName());
		writeInt((int) f.length());
		java.io.InputStream is = new java.io.FileInputStream(f);
		int size = 1024;
		int sent = 0;
		if (f.length() < size) {
			size = (int) f.length();
		}
		byte[] chunk;
		while (sent != f.length()) {
			if (f.length() - sent < size) {
				size = (int) f.length() - sent;
			}
			chunk = new byte[size];
			sent += is.read(chunk, 0, size);
			writeBytes(chunk);
		}
		try {
			is.close();
		}
		catch (IOException ioe) {
		}
	}

	/**
	 * Writes a boolean to the stream.
	 * 
	 * @param bool the boolean to write.
	 */
	public void writeBoolean(boolean bool) throws java.io.IOException {
		writeObject(bool);
	}

	/**
	 * Writes any Object to the stream.
	 * 
	 * @param obj the object to write.
	 */
	public void writeObject(Object obj) throws java.io.IOException {
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(os);
		oos.writeObject(obj);
		oos.flush();
	}

	/**
	 * Writes a byte to the stream.
	 * 
	 * @param value the value of the byte to write.
	 * @throws java.lang.IllegalArgumentException if the value is out of
	 * bounds.
	 * @throws java.io.IOException if the stream is invalid.
	 */
	public void writeByte(int value) throws java.io.IOException {
		// writeObject((byte)value);
		os.write(value);
	}

	/**
	 * Writes a plain text String to the stream.
	 * 
	 * @param str the String to write.
	 */
	public void writeString(String str) throws java.io.IOException {
		writeObject(str);
	}

	/**
	 * Writes an int to the stream.
	 * 
	 * @param i the int to write.
	 */
	public void writeInt(int i) throws java.io.IOException {
		writeObject(i);
	}

	/**
	 * Closes the stream when the session becomes invalid.
	 */
	public void closeStream() {
		try {
			this.os.close();
			Vars.getLogger().fine("Native outputstream closed");
		}
		catch (Throwable t) {
			Vars.getLogger().log(Level.SEVERE, "Unable to close outputstream: ", t);
		}
	}
}
