package org.whired.rsmap.io;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
import org.whired.rsmap.io.ByteBuffer;

// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
public class CacheLoader {

	public CacheLoader(byte abyte0[]) {
		method75(abyte0);
	}

	public void method75(byte abyte0[]) {
		ByteBuffer class1_sub1_sub2 = new ByteBuffer(abyte0);
		int i = class1_sub1_sub2.getShortInt();
		int j = class1_sub1_sub2.getShortInt();
		if (j != i) {
			byte abyte1[] = new byte[i];
			Class4.method73(abyte1, i, abyte0, j, 6);
			aByteArray93 = abyte1;
			class1_sub1_sub2 = new ByteBuffer(aByteArray93);
			aBoolean99 = true;
		}
		else {
			aByteArray93 = abyte0;
			aBoolean99 = false;
		}
		nodesLoaded = class1_sub1_sub2.getShort();
		anIntArray95 = new int[nodesLoaded];
		anIntArray96 = new int[nodesLoaded];
		anIntArray97 = new int[nodesLoaded];
		anIntArray98 = new int[nodesLoaded];
		int k = class1_sub1_sub2.currentOffset + nodesLoaded * 10;
		for (int l = 0; l < nodesLoaded; l++) {
			anIntArray95[l] = class1_sub1_sub2.getInt();
			anIntArray96[l] = class1_sub1_sub2.getShortInt();
			anIntArray97[l] = class1_sub1_sub2.getShortInt();
			anIntArray98[l] = k;

			k += anIntArray97[l];
		}
	}

	/**
	 * Loads a sub node by name
	 * 
	 * @param nodeName the name of the node
	 * @return the bytes that were loaded
	 */
	public byte[] loadNode(String nodeName) {
		byte[] out = null;
		int i = 0;
		nodeName = nodeName.toUpperCase();
		for (int j = 0; j < nodeName.length(); j++) {
			i = (i * 61 + nodeName.charAt(j)) - 32;
		}
		for (int k = 0; k < nodesLoaded; k++) {
			if (anIntArray95[k] == i) {
				if (out == null) {
					out = new byte[anIntArray96[k]];
				}
				if (!aBoolean99) {
					Class4.method73(out, anIntArray96[k], aByteArray93, anIntArray97[k], anIntArray98[k]);
				}
				else {
					for (int l = 0; l < anIntArray96[k]; l++) {
						out[l] = aByteArray93[anIntArray98[k] + l];
					}

				}
				return out;
			}
		}

		return null;
	}

	public byte aByteArray93[];
	public int nodesLoaded;
	public int anIntArray95[];
	public int anIntArray96[];
	public int anIntArray97[];
	public int anIntArray98[];
	public boolean aBoolean99;
}
