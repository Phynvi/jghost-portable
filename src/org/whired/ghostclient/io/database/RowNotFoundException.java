package org.whired.ghostclient.io.database;

public class RowNotFoundException extends Exception {
	public RowNotFoundException(String message) {
		super(message);
	}
}