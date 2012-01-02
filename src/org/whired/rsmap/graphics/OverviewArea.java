package org.whired.rsmap.graphics;

public class OverviewArea
{
	private final RSCanvas canvas;

	public OverviewArea(RSCanvas canvas, int width, int height)
	{
		area = new int[width * height];
		System.out.println("Overview area size: "+width+", "+height);
		areaWidth = width;
		areaHeight = height;
		this.canvas = canvas;
		//area = canvas.pixels;
	}

	public void validateAndDrawArea(int x, int y)
	{
		int k = x + y * canvas.getWidth();
		int l = 0;
		int i1 = areaHeight;
		int j1 = areaWidth;
		int k1 = canvas.getWidth() - j1;
		int l1 = 0;
		if (y < canvas.startY)
		{
			int i2 = canvas.startY - y;
			i1 -= i2;
			y = canvas.startY;
			l += i2 * j1;
			k += i2 * canvas.getWidth();
		}
		if (y + i1 > canvas.endY)
		{
			i1 -= (y + i1) - canvas.endY;
		}
		if (x < canvas.startX)
		{
			int j2 = canvas.startX - x;
			j1 -= j2;
			x = canvas.startX;
			l += j2;
			k += j2;
			l1 += j2;
			k1 += j2;
		}
		if (x + j1 > canvas.endX)
		{
			int k2 = (x + j1) - canvas.endX;
			j1 -= k2;
			l1 += k2;
			k1 += k2;
		}
		if(j1 > 0 && i1 > 0)
		{
			drawArea(l, k, j1, i1, k1, l1);
		}
	}

	private void drawArea(int i, int j, int k, int l, int i1, int j1)
	{
		int k1 = -(k >> 2);
		k = -(k & 3);
		for (int l1 = -l; l1 < 0; l1++)
		{
			for (int i2 = k1; i2 < 0; i2++)
			{
				canvas.pixels[j++] = area[i++];
				canvas.pixels[j++] = area[i++];
				canvas.pixels[j++] = area[i++];
				canvas.pixels[j++] = area[i++];
			}

			for (int j2 = k; j2 < 0; j2++)
			{
				canvas.pixels[j++] = area[i++];
			}

			j += i1;
			i += j1;
		}
	}
	public int area[];
	public int areaWidth;
	public int areaHeight;
	public int anInt205;
	public int anInt206;
	public int anInt207;
	public int anInt208;
}
