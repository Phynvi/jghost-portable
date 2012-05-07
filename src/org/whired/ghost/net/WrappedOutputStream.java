package org.whired.ghost.net;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.whired.ghost.Constants;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.util.JTF16Charset;

/**
 * Makes writing data to an {@link java.io.OutputStream} easy
 * @author Whired
 */
public class WrappedOutputStream {

	private OutputStream os = null;

	/**
	 * Creates a new wrapper for the specified output stream
	 * @param os_ the OutputStream to wrap
	 */
	public WrappedOutputStream(final OutputStream os) {
		this.os = os;
	}

	/**
	 * Writes a single byte to this stream
	 * @param value the value of the byte to write
	 * @throws java.io.IOException if the stream is invalid.
	 */
	public void writeByte(final int value) throws java.io.IOException {
		if (value > Byte.MAX_VALUE) {
			Constants.getLogger().warning("Byte size too large. Truncating.");
		}
		os.write(value);
	}

	/**
	 * Writes a byte[] to this stream
	 * @param b the byte array to write
	 */
	public void writeBytes(final byte[] b) throws IOException {
		os.write(b, 0, b.length);
	}

	/**
	 * Writes a boolean to this stream
	 * @param bool the boolean to write
	 */
	public void writeBoolean(final boolean bool) throws java.io.IOException {
		os.write(bool ? 1 : 0);
	}

	/**
	 * Writes a short to this stream
	 * @param value the short to write
	 */
	public void writeShort(final int value) throws IOException {
		if (value > Short.MAX_VALUE) {
			Constants.getLogger().warning("Short size too large. Truncating.");
		}
		writeBytes(ByteBuffer.allocate(2).putShort(new Integer(value).shortValue()).array());
	}

	/**
	 * Writes an integer to this stream
	 * @param value the integer to write
	 */
	public void writeInt(final int value) throws java.io.IOException {
		if (value > Integer.MAX_VALUE) {
			Constants.getLogger().warning("Int size too large. Truncating.");
		}
		writeBytes(ByteBuffer.allocate(4).putInt(value).array());
	}

	/**
	 * Writes a long to this stream
	 * @param value the long to write
	 */
	public void writeLong(final long value) throws IOException {
		writeBytes(ByteBuffer.allocate(8).putLong(value).array());
	}

	/**
	 * Writes a string to this stream. The given string will be encoded in utf-8. If the string's size is greater than 256 bytes, it will automatically be truncated.
	 * @param str the string to write
	 */
	public void writeString(final String str) throws java.io.IOException {
		if (str.length() > 255) {
			Constants.getLogger().warning("String size greater than 255 bytes. Truncating.");
		}
		final byte[] encoded = JTF16Charset.encode(str, 255);
		writeByte(encoded.length);
		writeBytes(encoded);
	}

	/**
	 * Writes a player to this stream
	 * @param plr they player to write
	 */
	public void writePlayer(final GhostPlayer plr) throws IOException {
		writeString(plr.getName());
		writeByte(plr.getRights());
	}

	/**
	 * Writes an array of players to this stream
	 * @param plrs the players to write
	 */
	public void writePlayers(final GhostPlayer[] plrs) throws IOException {
		writeShort(plrs.length);
		for (final GhostPlayer p : plrs) {
			writePlayer(p);
		}
	}
}
