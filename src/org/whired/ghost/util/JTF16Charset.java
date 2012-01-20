package org.whired.ghost.util;

/**
 * Charset for JTF16
 * 
 * @author Killer99
 * @author Whired
 */
public class JTF16Charset {

	/**
	 * Encodes a string in JTF16
	 * 
	 * @param string the string to encode
	 * @return the encoded bytes
	 */
	public static byte[] encode(String string) {
		return encode(string, -1);
	}

	/**
	 * Encodes a string in JTF16
	 * 
	 * @param string the string to encode
	 * @param maxLength the maximum length to encode; Non-positive values will be interpreted as no limit
	 * @return the encoded bytes
	 */
	public static byte[] encode(String string, int maxLength) {
		int length = maxLength < 1 ? string.length() : maxLength > string.length() ? string.length() : maxLength;
		int offset = 0;
		byte[] encoded = new byte[length];
		for (int i = 0; i != length; ++i) {
			int chr = string.charAt(i);
			if (chr < 0x80) {
				if (length - offset == 0) {
					return encoded;
				}
				encoded[offset++] = (byte) chr;
			}
			else if (chr < 0x3fff) {
				if (length - offset < 2) {
					return encoded;
				}

				encoded[offset++] = (byte) (chr | 0x80);
				encoded[offset++] = (byte) (chr >>> 7);
			}
			else {
				if (length - offset < 3) {
					return encoded;
				}
				encoded[offset++] = (byte) (chr | 0x80);
				encoded[offset++] = (byte) (chr >>> 7 | 0x80);
				encoded[offset++] = (byte) (chr >>> 14);
			}
		}
		return encoded;
	}

	/**
	 * Decodes a string encoded in JTF16
	 * 
	 * @param encoded the encoded bytes
	 * @return the decoded string
	 */
	public static String decode(byte[] encoded) {
		int offset = 0;
		int length = encoded.length;
		char[] chars = new char[length];
		int count = 0;
		for (length += offset; offset != length; ++count) {
			byte v1 = encoded[offset++];
			if (v1 >= 0) {
				chars[count] = (char) v1;
			}
			else if (offset != length) {
				byte v2 = encoded[offset++];
				if (v2 >= 0) {
					chars[count] = (char) (v1 & 0x7f | (v2 & 0x7f) << 7);
				}
				else if (offset != length) {
					chars[count] = (char) (v1 & 0x7f | (v2 & 0x7f) << 7 | (encoded[offset++] & 0x3) << 14);
				}
				else {
					break;
				}
			}
			else {
				break;
			}
		}
		return new String(chars, 0, count);
	}
}
