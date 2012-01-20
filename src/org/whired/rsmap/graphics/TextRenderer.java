package org.whired.rsmap.graphics;

import org.whired.rsmap.io.ByteBuffer;
import org.whired.rsmap.io.CacheLoader;

public class TextRenderer {
	public int getTextWidth(String text) {
		if (text == null)
			return 0;
		int i = 0;
		for (int j = 0; j < text.length(); j++)
			i += charScreenWidths[text.charAt(j)];

		return i;
	}

	public void render(String s, int x, int y, int k) {
		if (s == null)
			return;
		y -= maxCharHeight;
		for (int l = 0; l < s.length(); l++) {
			char c = s.charAt(l);
			if (c != ' ')
				attemptRender(charPixels[c], x + charXOffsets[c], y + charYOffsets[c], charWidths[c], charHeights[c], k);
			x += charScreenWidths[c];
		}

	}

	private final RSCanvas canvas;

	public TextRenderer(RSCanvas canvas, CacheLoader cacheLoader, String font) {
		this.canvas = canvas;
		charPixels = new byte[256][];
		charWidths = new int[256];
		charHeights = new int[256];
		charXOffsets = new int[256];
		charYOffsets = new int[256];
		charScreenWidths = new int[256];
		maxCharHeight = 0;
		ByteBuffer nodeBuffer = new ByteBuffer(cacheLoader.loadNode(font + ".dat"));
		ByteBuffer indexBuffer = new ByteBuffer(cacheLoader.loadNode("index.dat"));
		indexBuffer.currentOffset = nodeBuffer.getShort() + 4;
		int bytesToSkip = indexBuffer.getUnsignedByte();
		if (bytesToSkip > 0)
			indexBuffer.currentOffset += 3 * (bytesToSkip - 1);
		for (int curChar = 0; curChar < 256; curChar++) {
			charXOffsets[curChar] = indexBuffer.getUnsignedByte();
			charYOffsets[curChar] = indexBuffer.getUnsignedByte();
			int curCharWidth = charWidths[curChar] = indexBuffer.getShort();
			int curCharHeight = charHeights[curChar] = indexBuffer.getShort();
			int readMode = indexBuffer.getUnsignedByte();
			int curCharPixels = curCharWidth * curCharHeight;
			charPixels[curChar] = new byte[curCharPixels];

			// Read all px at once
			if (readMode == 0)
				for (int idx = 0; idx < curCharPixels; idx++)
					charPixels[curChar][idx] = nodeBuffer.getByte();
			else if (readMode == 1)
				for (int widthIdx = 0; widthIdx < curCharWidth; widthIdx++)
					for (int heightIdx = 0; heightIdx < curCharHeight; heightIdx++)
						charPixels[curChar][widthIdx + heightIdx * curCharWidth] = nodeBuffer.getByte();

			// Not sure why char index has to be < 128, maybe LC?
			if (curCharHeight > maxCharHeight && curChar < 128)
				maxCharHeight = curCharHeight;

			// Is there use in an array of all 1s?
			charXOffsets[curChar] = 1;
			charScreenWidths[curChar] = curCharWidth + 2;

			int j2 = 0;
			for (int l2 = curCharHeight / 7; l2 < curCharHeight; l2++)
				j2 += charPixels[curChar][l2 * curCharWidth];

			if (j2 <= curCharHeight / 7) {
				charScreenWidths[curChar]--;
				charXOffsets[curChar] = 0;
			}
			j2 = 0;
			for (int i3 = curCharHeight / 7; i3 < curCharHeight; i3++)
				j2 += charPixels[curChar][curCharWidth - 1 + i3 * curCharWidth];

			if (j2 <= curCharHeight / 7)
				charScreenWidths[curChar]--;
		}
		charScreenWidths[32] = charScreenWidths[105];
	}

	public void renderText(String text, int i, int j, int k) {
		render(text, i - getTextWidth(text) / 2, j, k);
	}

	public void attemptRender(byte abyte0[], int i, int j, int k, int l, int i1) {
		int j1 = i + j * canvas.getWidth();
		int k1 = canvas.getWidth() - k;
		int l1 = 0;
		int i2 = 0;
		if (j < canvas.startY) {
			int j2 = canvas.startY - j;
			l -= j2;
			j = canvas.startY;
			i2 += j2 * k;
			j1 += j2 * canvas.getWidth();
		}
		if (j + l >= canvas.endY)
			l -= j + l - canvas.endY + 1;
		if (i < canvas.startX) {
			int k2 = canvas.startX - i;
			k -= k2;
			i = canvas.startX;
			i2 += k2;
			j1 += k2;
			l1 += k2;
			k1 += k2;
		}
		if (i + k >= canvas.endX) {
			int l2 = i + k - canvas.endX + 1;
			k -= l2;
			l1 += l2;
			k1 += l2;
		}
		if (k <= 0 || l <= 0)
			return;
		else {
			renderText(abyte0, i1, i2, j1, k, l, k1, l1);
			return;
		}
	}

	public void renderText(byte abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				if (abyte0[j++] != 0)
					canvas.pixels[k++] = i;
				else
					k++;
				if (abyte0[j++] != 0)
					canvas.pixels[k++] = i;
				else
					k++;
				if (abyte0[j++] != 0)
					canvas.pixels[k++] = i;
				else
					k++;
				if (abyte0[j++] != 0)
					canvas.pixels[k++] = i;
				else
					k++;
			}

			for (int k2 = l; k2 < 0; k2++)
				if (abyte0[j++] != 0)
					canvas.pixels[k++] = i;
				else
					k++;

			k += j1;
			j += k1;
		}

	}

	public byte charPixels[][];
	public int charWidths[];
	public int charHeights[];
	public int charXOffsets[];
	public int charYOffsets[];
	public int charScreenWidths[];
	public int maxCharHeight;
}
