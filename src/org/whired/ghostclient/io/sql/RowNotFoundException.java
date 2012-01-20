package org.whired.ghostclient.io.sql;

public class RowNotFoundException extends Exception {
	public RowNotFoundException(String message) {
		super(message);
	}
}