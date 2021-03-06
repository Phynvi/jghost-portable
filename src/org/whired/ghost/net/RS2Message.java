package org.whired.ghost.net;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Utility class for converting messages
 * @author Whired
 */
public class RS2Message {
	private static final char[] XLATE_TABLE = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']',/* new: */'>', '<', '^', '/', '_' };

	/**
	 * Gets the message as a byte array
	 * @param charSize
	 */
	public static byte[] getPackedMessage(String message, int charSize) {
		if (message.length() > 80) {
			message = message.substring(0, 80);
		}

		// PI(?) encoding
		if (charSize == 1) {
			final ByteBuffer buffer = ByteBuffer.allocate(message.length());
			for (char c : message.toCharArray()) {
				for (int i = 0; i < XLATE_TABLE.length; i++) {
					if (XLATE_TABLE[i] == c) {
						buffer.put((byte) i);
						break;
					}
				}
			}
			return buffer.array();
		}
		// JAG317 encoding
		else if (charSize == 2) {
			final LinkedList<Byte> packed = new LinkedList<Byte>();
			if (message.length() > 80) {
				message = message.substring(0, 80);
			}
			message = message.toLowerCase();
			int carryOverNibble = -1;
			int ofs = 0;
			for (int idx = 0; idx < message.length(); idx++) {
				final char c = message.charAt(idx);
				int tableIdx = 0;
				for (int i = 0; i < XLATE_TABLE.length; i++) {
					if (c == (byte) XLATE_TABLE[i]) {
						tableIdx = i;
						break;
					}
				}
				if (tableIdx > 12) {
					tableIdx += 195;
				}
				if (carryOverNibble == -1) {
					if (tableIdx < 13) {
						carryOverNibble = tableIdx;
					}
					else {
						packed.add(ofs++, (byte) tableIdx);
					}
				}
				else if (tableIdx < 13) {
					packed.add(ofs++, (byte) ((carryOverNibble << 4) + tableIdx));
					carryOverNibble = -1;
				}
				else {
					packed.add(ofs++, (byte) ((carryOverNibble << 4) + (tableIdx >> 4)));
					carryOverNibble = tableIdx & 0xf;
				}
			}
			if (carryOverNibble != -1) {
				packed.add(ofs++, (byte) (carryOverNibble << 4));
			}
			Byte[] packedArray = new Byte[packed.size()];
			final byte[] primPacked = new byte[packed.size()];
			packedArray = packed.toArray(packedArray);
			for (int i = 0; i < packedArray.length; i++) {
				primPacked[i] = packedArray[i].byteValue();
			}
			return primPacked;
		}
		else {
			throw new RuntimeException("Unknown encoding length: " + charSize);
		}
	}

	/**
	 * Unpacks and formats chat that was sent on the specified connection
	 * @param charSize
	 * @return the unpacked and formatted chat
	 */
	public static String unpackMessage(final byte[] chatText, final int chatLength, int charSize) {
		// PI(?) encoding
		if (charSize == 1) {
			StringBuilder builder = new StringBuilder();
			int charIndex = 0;
			for (int i = 0; i < chatLength; i++) {
				charIndex = chatText[i];
				if (charIndex < XLATE_TABLE.length) {
					builder.append(XLATE_TABLE[charIndex]);
				}
			}
			return builder.toString();
		}
		// JAG317 encoding
		else if (charSize == 2) {
			final char decodeBuf[] = new char[4096];
			int idx = 0, highNibble = -1;
			for (int i = 0; i < chatLength * 2; i++) {
				final int val = chatText[i / 2] >> 4 - 4 * (i % 2) & 0xf;
				if (highNibble == -1) {
					if (val < 13) {
						decodeBuf[idx++] = XLATE_TABLE[val];
					}
					else {
						highNibble = val;
					}
				}
				else {
					decodeBuf[idx++] = XLATE_TABLE[(highNibble << 4) + val - 195];
					highNibble = -1;
				}
			}
			return new String(decodeBuf, 0, idx);
		}
		else {
			throw new RuntimeException("Unknown encoding length: " + charSize);
		}

	}
}
