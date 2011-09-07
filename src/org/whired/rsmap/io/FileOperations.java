package org.whired.rsmap.io;

// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 8/7/2008 2:02:56 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   FileOperations.java

import java.io.*;

public class FileOperations
{
	public FileOperations()
	{
	}

	public static final byte[] ReadFile(String s) throws FileNotFoundException, IOException
	{
		File file = new File(s);
		int i = (int) file.length();
		byte abyte0[] = new byte[i];
		DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(s)));
		datainputstream.readFully(abyte0, 0, i);
		datainputstream.close();
		return abyte0;
	}

	public static final void WriteFile(String s, byte abyte0[])
	{
		try
		{
			(new File((new File(s)).getParent())).mkdirs();
			FileOutputStream fileoutputstream = new FileOutputStream(s);
			fileoutputstream.write(abyte0, 0, abyte0.length);
			fileoutputstream.close();
		}
		catch (Throwable throwable)
		{
			System.out.println("Write Error: " + s);
		}
	}

	public static boolean FileExists(String file)
	{
		File f = new File(file);
		return f.exists();
	}
}
