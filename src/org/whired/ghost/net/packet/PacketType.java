package org.whired.ghost.net.packet;

/**
 * Constants for packet types
 * 
 * @author Whired
 */
public abstract class PacketType {
	public static final int AUTHENTICATION = 0;
	public static final int AUTHENTICATE_SUCCESS = 1;
	public static final int PUBLIC_CHAT = 2;
	public static final int SERVER_LOG = 3;
	public static final int DEBUG_MESSAGE = 4;
	public static final int PRIVATE_CHAT = 6;
	public static final int PLAYER_MOVEMENT = 7;
	public static final int INVOKE_ACCESSOR = 8;
}
