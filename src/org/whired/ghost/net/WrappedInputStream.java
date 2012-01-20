package org.whired.ghost.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.whired.ghost.Constants;
import org.whired.ghost.net.Connection.DisconnectCallback;
import org.whired.ghost.util.JTF16Charset;

/**
 * Makes reading data from an {@link java.io.InputStream} easy
 * 
 * @author Whired
 */
public class WrappedInputStream {
	/** The InputStream to wrap */
	private InputStream is = null;
	/** Ensures that timeouts do not occur during inactivity */
	public boolean expectingNewPacket = false;
	/** Whether or not to enforce an idle timeout */
	public boolean enforceTimeout = true;
	/** The callback to notify when this stream is disconnected */
	private DisconnectCallback disconnectCallback;

	/**
	 * Creates a new wrapper for the specified input stream
	 * 
	 * @param is the InputStream to wrap
	 */
	public WrappedInputStream(InputStream is) {
		this.is = is;
	}

	/**
	 * Reads a single byte from this stream
	 * 
	 * @return the value of the byte that was read
	 * @throws IOException when the end of the stream is reached, or the stream cannot be read from
	 */
	public int readByte() throws IOException {
		int x = is.read();
		if (x == -1) {
			disconnectCallback.disconnected("End of stream");
			throw new IOException("End of stream");
		}
		return x;
	}

	/**
	 * Reads bytes from this stream
	 * 
	 * @param length the number of bytes to read
	 * @return the bytes that were read
	 * @throws IOException when the end of the stream is reached, or the stream cannot be read from
	 */
	public byte[] readBytes(int length) throws IOException {
		byte[] buf = new byte[length];
		int read = 0;
		while ((read += is.read(buf)) < length)
			;
		return buf;
	}

	/**
	 * Reads a boolean from this stream
	 * 
	 * @return bool the boolean that was read
	 */
	public boolean readBoolean() throws java.io.IOException {
		return is.read() >= 1;
	}

	/**
	 * Reads a short from this stream
	 * 
	 * @return the short that was read
	 */
	public short readShort() throws IOException {
		return ByteBuffer.wrap(readBytes(2)).getShort();
	}

	/**
	 * Reads an integer from this stream
	 * 
	 * @return the integer that was read
	 */
	public int readInt() throws java.io.IOException {
		return ByteBuffer.wrap(readBytes(4)).getInt();
	}

	/**
	 * Reads a long from this stream
	 * 
	 * @return the long that was read
	 */
	public long readLong() throws IOException {
		return ByteBuffer.wrap(readBytes(8)).getLong();
	}

	/**
	 * Reads a string from this stream
	 * 
	 * @return the string that was read
	 */
	public String readString() throws java.io.IOException {
		String s = JTF16Charset.decode(readBytes(is.read()));
		Constants.getLogger().info("Received str: " + s);
		return s;
	}

	/** Sets the SessionManager for this stream */
	protected void setDisconnectCallback(DisconnectCallback c) {
		this.disconnectCallback = c;
	}

	/** Closes this stream when the session becomes invalid */
	private void closeStream() {
		try {
			Constants.getLogger().fine("Native inputstream closed.");
		}
		catch (Exception e) {
			Constants.getLogger().warning("Unable to close inputstream:");
			e.printStackTrace();
		}
	}

	/** Work to do before destroying this object */
	public void destruct() {
		closeStream();
		Constants.getLogger().fine("Destruct complete.");
	}
}
