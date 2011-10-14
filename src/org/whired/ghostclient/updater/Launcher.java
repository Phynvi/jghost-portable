package org.whired.ghostclient.updater;

import java.io.*;
import java.security.MessageDigest;
import javax.swing.SwingUtilities;
import org.whired.ghostclient.updater.io.HttpClient;
import org.whired.ghostclient.updater.ui.UpdaterForm;

/**
 * Downloads and launches the newest version of GHOST
 *
 * @author Whired
 */
public class Launcher implements Runnable {

	/**
	 * The name of the package
	 */
	private static final String PACKAGE_NAME = "ghostclient.jar";
	/**
	 * Where the packages are hosted
	 */
	private static final String REMOTE_CODEBASE = "https://raw.github.com/Whired/jghost-portable/master/dist/dev/"; // Stable branch
	/**
	 * Where the packages are saved
	 */
	private static final String LOCAL_CODEBASE = System.getProperty("user.home") + "/.ghost/cache/";
	/**
	 * The GUI to display output on
	 */
	private final UpdaterForm form;

	/**
	 * Creates a new launcher for the given form
	 * @param form the form to display output to
	 */
	public Launcher(UpdaterForm form) {
		this.form = form;
	}
	
	public void run() {
		form.log("Preparing system..");
		File f = new File(LOCAL_CODEBASE);
		if(!f.exists())
			f.mkdirs();
		form.log("Grabbing file version..");
		try {
			String remoteHash = getRemoteHash(REMOTE_CODEBASE + PACKAGE_NAME);
			String localHash = getLocalHash(LOCAL_CODEBASE + PACKAGE_NAME);
			boolean match = remoteHash.toLowerCase().equals(localHash.toLowerCase());
			form.log("Remote: " + remoteHash.substring(remoteHash.length()/2));
			form.log("Local: " + localHash.substring(localHash.length()/2));
			while (!match) {
				form.log("Hash mismatch.");
				form.log("Downloading new version..");
				HttpClient.saveToDisk(LOCAL_CODEBASE + PACKAGE_NAME, REMOTE_CODEBASE + PACKAGE_NAME);
				localHash = getLocalHash(LOCAL_CODEBASE + PACKAGE_NAME);
				form.log("Newest version downloaded.");
				form.log("Checking sanity..");
				match = remoteHash.toLowerCase().equals(localHash.toLowerCase());
				break;
			}
			form.log("Hashes match, GHOST is up-to-date!");
			form.log("Attempting to launch..");
			try {
				ProcessBuilder pb = new ProcessBuilder("java", "-classpath", LOCAL_CODEBASE + PACKAGE_NAME, "org.whired.ghostclient.Main");
				pb.start();
				form.log("Exiting..");
				try {
					Thread.sleep(8000);
				}
				catch (InterruptedException ex) {}
				System.exit(0);
			}
			catch (IOException e) {
				form.log("Unable to launch GHOST: " + e.toString());
			}
		}
		catch (IOException ex) {
			form.log("Unable to update: "+(ex.getMessage() == null ? "Hash not available" : ex.getMessage()));
		}
	}

	/**
	 * Gets the hash from the file at the specified url
	 * @param url the url of the file to get the hash of
	 * @return the hash that was read
	 */
	private String getRemoteHash(String url) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(HttpClient.getStream(url + ".MD5")));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line);
		return sb.toString();
	}

	/**
	 * Gets the hash from the file at the given path
	 * @param path the path to the file
	 * @return the hash of the file
	 */
	private String getLocalHash(String path) {
		BufferedInputStream bis = null;
		try {
			File file = new File(path);
			MessageDigest md = MessageDigest.getInstance("MD5");
			int bytesRead;
			byte[] buffer = new byte[1024];
			bis = new BufferedInputStream(new FileInputStream(file));
			while ((bytesRead = bis.read(buffer)) != -1)
				md.update(buffer, 0, bytesRead);
			bis.close();
			return toHexString(md.digest());
		}
		catch (Exception ex) {} // Swallow irrelevant exceptions
		finally {
			try {
				bis.close();
			}
			catch (IOException ex) {}
		}
		return null;
	}

	/**
	 * Converts an array of bytes to a hexidecimal string
	 * @param bytes the bytes to convert
	 * @return the resulting string
	 */
	public static String toHexString(byte[] bytes) {
		char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v / 16];
			hexChars[j * 2 + 1] = hexArray[v % 16];
		}
		return new String(hexChars);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				final UpdaterForm form = new UpdaterForm();
				new Thread(new Launcher(form)).start();
			}
		});
	}
}
