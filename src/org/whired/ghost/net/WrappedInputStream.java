package org.whired.ghost.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.whired.ghost.player.Player;
import org.whired.ghost.util.JTF16Charset;

/**
 * Makes reading data from an {@link java.io.InputStream} easy
 * @author Whired
 */
public class WrappedInputStream {
	/** The InputStream to wrap */
	private final InputStream is;

	/**
	 * Creates a new wrapper for the specified input stream
	 * @param is the InputStream to wrap
	 */
	public WrappedInputStream(final InputStream is) {
		this.is = is;
	}

	/**
	 * Reads a single byte from this stream
	 * @return the value of the byte that was read
	 * @throws IOException when the end of the stream is reached, or the stream cannot be read from
	 */
	public int readByte() throws IOException {
		final int x = is.read();
		if (x == -1) {
			throw new IOException("End of stream");
		}
		return x;
	}

	/**
	 * Reads bytes from this stream
	 * @param length the number of bytes to read
	 * @return the bytes that were read
	 * @throws IOException when the end of the stream is reached, or the stream cannot be read from
	 */
	public byte[] readBytes(final int length) throws IOException {
		final byte[] buf = new byte[length];
		int read = 0;
		while ((read += is.read(buf)) < length) {
			;
		}
		return buf;
	}

	/**
	 * Reads a boolean from this stream
	 * @return the boolean that was read
	 */
	public boolean readBoolean() throws java.io.IOException {
		return is.read() >= 1;
	}

	/**
	 * Reads a short from this stream
	 * @return the short that was read
	 */
	public short readShort() throws IOException {
		return ByteBuffer.wrap(readBytes(2)).getShort();
	}

	/**
	 * Reads an integer from this stream
	 * @return the integer that was read
	 */
	public int readInt() throws java.io.IOException {
		return ByteBuffer.wrap(readBytes(4)).getInt();
	}

	/**
	 * Reads a long from this stream
	 * @return the long that was read
	 */
	public long readLong() throws IOException {
		return ByteBuffer.wrap(readBytes(8)).getLong();
	}

	/**
	 * Reads a string from this stream
	 * @return the string that was read
	 */
	public String readString() throws java.io.IOException {
		final String s = JTF16Charset.decode(readBytes(is.read()));
		return s;
	}

	/**
	 * Reads a player from this stream
	 * @return the player that was read
	 */
	public Player readPlayer() throws IOException {
		return new Player(readString(), readByte());
	}

	/**
	 * Reads a collection of players from this stream
	 * @return the players that were read
	 */
	public Player[] readPlayers() throws IOException {
		final Player[] plrs = new Player[readShort()];
		for (int i = 0; i < plrs.length; i++) {
			plrs[i] = readPlayer();
		}
		return plrs;
	}
}
