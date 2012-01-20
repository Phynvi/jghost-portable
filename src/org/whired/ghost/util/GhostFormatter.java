package org.whired.ghost.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.whired.ghost.Constants;

public class GhostFormatter extends Formatter {

	private final Date date = new Date();
	private final String lineSeparator = System.getProperty("line.separator");

	/**
	 * Format the given LogRecord.
	 * 
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	@Override
	public synchronized String format(LogRecord record) {
		StringBuilder sb = new StringBuilder("[");
		date.setTime(record.getMillis());
		sb.append(Constants.DATE_FORMAT.format(date));
		sb.append("] ");
		String cn = record.getSourceClassName();
		if (cn != null)
			sb.append(cn.substring(cn.lastIndexOf(".") + 1, cn.length()));
		else
			sb.append(record.getLoggerName());
		if (record.getSourceMethodName() != null) {
			sb.append(".");
			sb.append(record.getSourceMethodName());
		}
		sb.append(": ");
		String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(message);
		sb.append(lineSeparator);
		if (record.getThrown() != null)
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			}
			catch (Exception ex) {
			}
		return sb.toString();
	}
}