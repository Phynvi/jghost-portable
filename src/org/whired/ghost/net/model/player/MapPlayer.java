/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.whired.ghost.net.model.player;

import java.awt.Point;
import org.whired.rsmap.graphics.PaintObserver;

/**
 *
 * @author whired
 */
public class MapPlayer extends Player
{
	private int absX;
	private int absY;
	private final PaintObserver observer;

	public MapPlayer(String name, Rank rank, int absX, int absY, PaintObserver observer)
	{
		super(name, rank);
		this.absX = absX;
		this.absY = absY;
		this.observer = observer;
	}

	public void moveTo(int absX, int absY)
	{
		synchronized (this)
		{
			this.absX = absX;
			this.absY = absY;
		}
		observer.requestRedraw();
	}

	public synchronized Point getLocation()
	{
		return new Point(absX, absY);
	}
}
