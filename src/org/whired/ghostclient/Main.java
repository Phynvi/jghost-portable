package org.whired.ghostclient;

import org.whired.ghostclient.ui.GhostFrameImpl;
import org.whired.ghostclient.ui.GhostUserImpl;

public class Main
{
	public static void main(String args[]) 
	{
		new GhostUserImpl(new GhostFrameImpl());
	}
}
