package org.whired.ghostclient.io.database;

import java.sql.ResultSet;

public abstract class SqlAdapter {
	public void resultsRetrieved(ResultSet rs) {
	}

	public void rowCountRetrieved(int count) {
	}

	public void containsRowRetrieved(boolean contains) {
	}

	public void rowRetreived(Object[] columnValues) {
	}
}
