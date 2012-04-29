package org.whired.ghostclient.io.sql;

public class RowNotFoundException extends Exception {
	public RowNotFoundException(final String message) {
		super(message);
	}
}