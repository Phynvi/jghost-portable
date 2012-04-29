package org.whired.ghost.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class GhostFormatter extends Formatter {

	private final Date date = new Date();
	private final String lineSeparator = System.getProperty("line.separator");
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M k:mm");

	/**
	 * Format the given LogRecord.
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	@Override
	public String format(final LogRecord record) {
		final StringBuilder sb = new StringBuilder("[");
		date.setTime(record.getMillis());
		sb.append(DATE_FORMAT.format(date));
		sb.append("] ");
		String cn = record.getSourceClassName();
		if (cn.contains("$")) {
			cn = cn.substring(0, cn.indexOf("$"));
		}
		if (cn != null) {
			sb.append(cn.substring(cn.lastIndexOf(".") + 1, cn.length()));
		}
		else {
			sb.append(record.getLoggerName());
		}
		if (record.getSourceMethodName() != null) {
			sb.append(".");
			sb.append(record.getSourceMethodName());
		}

		if (record.getLevel().intValue() > Level.INFO.intValue()) {
			sb.append("_").append(record.getLevel().getLocalizedName());
		}

		sb.append(": ");
		sb.append(formatMessage(record));
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try {
				final StringWriter sw = new StringWriter();
				final PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			}
			catch (final Exception ex) {
			}
		}
		return sb.toString();
	}
}