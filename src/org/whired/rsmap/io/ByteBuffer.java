package org.whired.rsmap.io;

public class ByteBuffer
{
	public int getInt()
	{
		currentOffset += 4;
		return ((buffer[currentOffset - 4] & 0xff) << 24)
			   + ((buffer[currentOffset - 3] & 0xff) << 16)
			   + ((buffer[currentOffset - 2] & 0xff) << 8)
			   + (buffer[currentOffset - 1] & 0xff);
	}

	public int getUnsignedByte()
	{
		return buffer[currentOffset++] & 0xff;
	}

	public byte getByte()
	{
		return buffer[currentOffset++];
	}

	public int getShort()
	{
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8)
			   + (buffer[currentOffset - 1] & 0xff);
	}

	public String getString()
	{
		int i = currentOffset;
		while (buffer[currentOffset++] != 10);
		return new String(buffer, i, currentOffset - i - 1);
	}

	public int getShortInt()
	{
		currentOffset += 3;
		return ((buffer[currentOffset - 3] & 0xff) << 16)
			   + ((buffer[currentOffset - 2] & 0xff) << 8)
			   + (buffer[currentOffset - 1] & 0xff);
	}

	public ByteBuffer()
	{
	}

	public ByteBuffer(byte abyte0[])
	{
		buffer = abyte0;
		currentOffset = 0;
	}
	public byte buffer[];
	public int currentOffset;
}
