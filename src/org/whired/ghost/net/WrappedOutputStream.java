package org.whired.ghost.net;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Level;

import org.whired.ghost.constants.Vars;

/**
 * Makes writing data to an {@link java.io.OutputStream} easy
 * 
 * @author Whired
 */
public class WrappedOutputStream {

	private OutputStream os = null;

	/**
	 * Creates a new wrapper for the specified output stream
	 * 
	 * @param os_ the OutputStream to wrap
	 */
	public WrappedOutputStream(OutputStream os) {
		this.os = os;
	}

	/**
	 * Writes a single byte to this stream
	 * 
	 * @param value the value of the byte to write
	 * @throws java.io.IOException if the stream is invalid.
	 */
	public void writeByte(int value) throws java.io.IOException {
		os.write(value);
	}

	/**
	 * Writes a byte[] to this stream
	 * 
	 * @param b the byte array to write
	 */
	public void writeBytes(byte[] b) throws IOException {
		os.write(b, 0, b.length);
	}

	/**
	 * Writes a boolean to this stream
	 * 
	 * @param bool the boolean to write
	 */
	public void writeBoolean(boolean bool) throws java.io.IOException {
		os.write(bool ? 1 : 0);
	}

	/**
	 * Writes a short to this stream
	 * 
	 * @param value the short to write
	 */
	public void writeShort(short value) throws IOException {
		writeBytes(ByteBuffer.allocate(2).putShort(value).array());
	}

	/**
	 * Writes an integer to this stream
	 * 
	 * @param value the integer to write
	 */
	public void writeInt(int value) throws java.io.IOException {
		writeBytes(ByteBuffer.allocate(4).putInt(value).array());
	}

	/**
	 * Writes a long to this stream
	 * 
	 * @param value the long to write
	 */
	public void writeLong(long value) throws IOException {
		writeBytes(ByteBuffer.allocate(8).putLong(value).array());
	}

	/**
	 * Writes a string to this stream. The given string will be encoded in
	 * utf-8. If the string's size is greater than 256 bytes, it will
	 * automatically be truncated.
	 * 
	 * @param str the string to write
	 */
	public void writeString(String str) throws java.io.IOException {
		byte[] strBytes = str.getBytes(Constants.UTF_8);
		if (strBytes.length > 256) {
			Vars.getLogger().warning("String size greater than 256 bytes. Truncating.");
			byte[] buf = new byte[256];
			ByteBuffer out = ByteBuffer.wrap(buf);
			CharBuffer in = CharBuffer.wrap(str.toCharArray());
			Constants.UTF_8.newEncoder().encode(in, out, true);
			str = new String(buf, 0, out.position(), Constants.UTF_8);
			strBytes = new byte[out.position()];
			System.arraycopy(buf, 0, strBytes, 0, strBytes.length);
		}
		writeByte(strBytes.length);
		writeBytes(strBytes);
	}

	/**
	 * Closes this stream
	 */
	protected void closeStream() {
		try {
			this.os.close();
			Vars.getLogger().fine("Native outputstream closed");
		}
		catch (Throwable t) {
			Vars.getLogger().log(Level.SEVERE, "Unable to close outputstream: ", t);
		}
	}
}
