package org.whired.ghost.net.packet;

/**
 * Constants for packet types
 * @author Whired
 */
public interface PacketType {
	int AUTHENTICATION = 0;
	int AUTHENTICATE_SUCCESS = 1;
	int PUBLIC_CHAT = 2;
	int SERVER_LOG = 3;
	int DEBUG_MESSAGE = 4;
	int PLAYER_CONNECTION = 5;
	int PRIVATE_CHAT = 6;
	int PLAYER_MOVEMENT = 7;
	int INVOKE_ACCESSOR = 8;
	int UPDATE_PLAYER_LIST = 9;
	int MODERATE_PLAYER = 10;
}
