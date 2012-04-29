package org.whired.rsmap.io;

// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 8/7/2008 2:02:56 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   FileOperations.java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileOperations {

	public FileOperations() {
	}

	public static final byte[] ReadFile(final InputStream is) throws FileNotFoundException, IOException {
		return getBytes(is);
	}

	public static byte[] getBytes(final InputStream is) throws IOException {

		int len;
		int size = 1024;
		byte[] buf;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		}
		else {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			while ((len = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, len);
			}
			buf = bos.toByteArray();
		}
		return buf;
	}

	public static final void WriteFile(final String s, final byte abyte0[]) {
		try {
			new File(new File(s).getParent()).mkdirs();
			final FileOutputStream fileoutputstream = new FileOutputStream(s);
			fileoutputstream.write(abyte0, 0, abyte0.length);
			fileoutputstream.close();
		}
		catch (final Throwable throwable) {
			System.out.println("Write Error: " + s);
		}
	}

	public static boolean FileExists(final String file) {
		final File f = new File(file);
		return f.exists();
	}
}
