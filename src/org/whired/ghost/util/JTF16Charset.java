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
	 * @param maxLen the maximum length to encode
	 * @return the encoded bytes
	 */
	public static byte[] encode(String string, int maxLen) {
		if (string == null)
			throw new IllegalArgumentException("string must be non-null");
		int idx = 0;
		int len = 0;
		char[] chrArr = string.toCharArray();
		for (char c : chrArr)
			if (c < 0x80)
				len++;
			else if (c < 0x3fff)
				len += 2;
			else
				len += 3;
		byte[] encoded = new byte[len < maxLen ? len : maxLen];
		for (int chr : chrArr)
			if (chr < 0x80 && encoded.length - idx > 0)
				encoded[idx++] = (byte) chr;
			else if (chr < 0x3fff && encoded.length - idx > 1) {
				encoded[idx++] = (byte) (chr | 0x80);
				encoded[idx++] = (byte) (chr >>> 7);
			}
			else if (encoded.length - idx > 2) {
				encoded[idx++] = (byte) (chr | 0x80);
				encoded[idx++] = (byte) (chr >>> 7 | 0x80);
				encoded[idx++] = (byte) (chr >>> 14);
			}
			else
				break;
		return encoded;
	}

	/**
	 * Encodes a string in JTF16
	 * 
	 * @param string the string to encode
	 * @return the encoded bytes
	 */
	public static byte[] encode(String string) {
		if (string == null)
			throw new IllegalArgumentException("string must be non-null");
		int len = 0;
		char[] chrArr = string.toCharArray();
		for (char c : chrArr)
			if (c < 0x80)
				len++;
			else if (c < 0x3fff)
				len += 2;
			else
				len += 3;
		byte[] encoded = new byte[len];
		int idx = 0;
		for (int chr : chrArr)
			if (chr < 0x80)
				encoded[idx++] = (byte) chr;
			else if (chr < 0x3fff) {
				encoded[idx++] = (byte) (chr | 0x80);
				encoded[idx++] = (byte) (chr >>> 7);
			}
			else {
				encoded[idx++] = (byte) (chr | 0x80);
				encoded[idx++] = (byte) (chr >>> 7 | 0x80);
				encoded[idx++] = (byte) (chr >>> 14);
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
			if (v1 >= 0)
				chars[count] = (char) v1;
			else if (offset != length) {
				byte v2 = encoded[offset++];
				if (v2 >= 0)
					chars[count] = (char) (v1 & 0x7f | (v2 & 0x7f) << 7);
				else if (offset != length)
					chars[count] = (char) (v1 & 0x7f | (v2 & 0x7f) << 7 | (encoded[offset++] & 0x3) << 14);
				else
					break;
			}
			else
				break;
		}
		return new String(chars, 0, count);
	}
}
