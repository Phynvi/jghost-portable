package org.whired.ghostclient.io.sql;

import java.sql.ResultSet;

public abstract class SqlAdapter {
	public void resultsRetrieved(final ResultSet rs) {
	}

	public void rowCountRetrieved(final int count) {
	}

	public void containsRowRetrieved(final boolean contains) {
	}

	public void rowRetreived(final Object[] columnValues) {
	}
}
