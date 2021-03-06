package org.whired.ghostclient.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Acts as a client for HTTP
 * @author Whired
 */
public class HttpClient {

	/**
	 * Gets the stream after following redirects
	 * @param url the url of the document to download
	 * @return the utf-8 source
	 * @throws IOException when the source cannot be downloaded
	 */
	public static InputStream getStream(final String url) throws IOException {
		int failures = 0;
		int stat = -1;
		while (failures < 10) {
			try {
				URLConnection c = new URL(url).openConnection();
				c.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.794.0 Safari/535.1");
				c.setConnectTimeout(8000);
				c.setReadTimeout(8000);
				boolean redir = false;
				int redirects = 0;
				InputStream in = null;
				do {
					if (c instanceof HttpURLConnection) {
						final HttpURLConnection http = (HttpURLConnection) c;
						http.setInstanceFollowRedirects(false);
						http.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.794.0 Safari/535.1");
						http.setConnectTimeout(8000);
						http.setReadTimeout(8000);
						stat = http.getResponseCode();
						in = c.getInputStream();
						redir = false;
						if (stat >= 300 && stat <= 307 && stat != 306 && stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
							final URL base = http.getURL();
							final String loc = http.getHeaderField("Location");
							URL target = null;
							if (loc != null) {
								target = new URL(base, loc);
							}
							http.disconnect();
							if (target == null || !(target.getProtocol().equals("http") || target.getProtocol().equals("https")) || redirects >= 5) {
								throw new SecurityException("illegal URL redirect");
							}
							redir = true;
							c = target.openConnection();
							c.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.794.0 Safari/535.1");
							c.setConnectTimeout(8000);
							c.setReadTimeout(8000);
							redirects++;
						}
					}
				}
				while (redir);
				return in;
			}
			catch (final Exception e) {
				if (e instanceof UnknownHostException || e instanceof NoRouteToHostException) {
					if (!ping("http://www.google.com")) {
						System.out.println("No Internet connection, retrying download in 30 seconds.");
						// internet is probably disconnected, try forever
						try {
							Thread.sleep(30000);
						}
						catch (final InterruptedException ie) {
						}
						continue;
					}
					else {
						break;
					}
				}
				// If it's a 404 or 500, it's probably not going to exist
				// even after 10 tries
				// If the connection timed out, don't even try again
				if (stat != 404 && stat != 503 && stat != 500 || e instanceof SocketTimeoutException) {
					failures++;
				}
				else {
					break;
				}
			}
		}
		throw new IOException();
	}

	/**
	 * Gets the status while following http redirects
	 * @param url the url of the status to check
	 * @return the status that was returned
	 */
	public static int getStatus(final String url) throws IOException {
		int stat = -1;
		URLConnection c = new URL(url).openConnection();
		c.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.794.0 Safari/535.1");
		c.setConnectTimeout(8000);
		c.setReadTimeout(8000);
		boolean redir = false;
		int redirects = 0;
		do {
			if (c instanceof HttpURLConnection) {
				final HttpURLConnection http = (HttpURLConnection) c;
				http.setRequestMethod("HEAD");
				http.setInstanceFollowRedirects(false);
				http.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.794.0 Safari/535.1");
				http.setConnectTimeout(8000);
				http.setReadTimeout(8000);
				stat = http.getResponseCode();
				redir = false;
				if (stat >= 300 && stat <= 307 && stat != 306 && stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
					final URL base = http.getURL();
					final String loc = http.getHeaderField("Location");
					URL target = null;
					if (loc != null) {
						target = new URL(base, loc);
					}
					http.disconnect();
					if (target == null || !(target.getProtocol().equals("http") || target.getProtocol().equals("https")) || redirects >= 5) {
						throw new SecurityException("illegal URL redirect");
					}
					redir = true;
					c = target.openConnection();
					redirects++;
				}
			}
		}
		while (redir);
		return stat;
	}

	public static File saveToDisk(final String path, final String url) throws MalformedURLException, FileNotFoundException, IOException {
		final BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
		final FileOutputStream fos = new java.io.FileOutputStream(path);
		final BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
		final byte data[] = new byte[1024];
		int read;
		while ((read = in.read(data)) != -1) {
			bout.write(data, 0, read);
		}
		bout.close();
		in.close();
		return new File(path);
	}

	public static boolean ping(final String url) {
		int stat = -1;
		try {
			HttpURLConnection.setFollowRedirects(false);
			final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setRequestMethod("HEAD");
			stat = con.getResponseCode();
			con.disconnect();
			System.out.println("Ping status: " + stat);
			return stat != -1;
		}
		catch (final Exception ee) {
			System.out.println("Ping error: " + ee.toString());
		}
		return false;
	}
}
