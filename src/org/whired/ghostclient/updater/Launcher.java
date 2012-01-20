package org.whired.ghostclient.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingUtilities;

import org.whired.ghost.Constants;
import org.whired.ghostclient.io.HttpClient;
import org.whired.ghostclient.updater.ui.UpdaterFrame;

/**
 * Downloads and launches the newest version of GHOST
 * 
 * @author Whired
 */
public class Launcher implements Runnable {

	/**
	 * The name of the jar package
	 */
	private static final String JAR_PACKAGE_NAME = "ghostclient.jar";
	/**
	 * The name of the module package
	 */
	private static final String MODULE_PACKAGE_NAME = "modules.zip";
	/**
	 * Where the packages are hosted
	 */
	private static final String REMOTE_CODEBASE = "https://raw.github.com/Whired/jghost-portable/master/dist/dev/"; // Dev
																								// branch
	/**
	 * Where the packages are saved
	 */
	private static final String LOCAL_CODEBASE = Constants.getLocalCodebase();
	/**
	 * The entry point of the main application
	 */
	private final String ENTRY_POINT = "org.whired.ghostclient.Main";
	/**
	 * The GUI to display output on
	 */
	private final UpdaterFrame form;

	/**
	 * Creates a new launcher for the given form
	 * 
	 * @param form the form to display output to
	 */
	public Launcher(UpdaterFrame form) {
		this.form = form;
	}

	@Override
	public void run() {
		form.log("Checking for client update..");
		try {
			checkAndDownload(LOCAL_CODEBASE + JAR_PACKAGE_NAME, REMOTE_CODEBASE + JAR_PACKAGE_NAME);
			form.log("Client updated!");
		}
		catch (IOException e1) {
			form.log("Unable to update client.");
			e1.printStackTrace();
			launchGhost();
			return;
		}
		catch (UpdateNotFoundException e) {
			form.log("No update found.");
		}

		if (form.downloadModules()) {
			form.log("Checking for module updates..");
			File zipped;
			try {
				zipped = checkAndDownload(LOCAL_CODEBASE + MODULE_PACKAGE_NAME, REMOTE_CODEBASE + MODULE_PACKAGE_NAME);
			}
			catch (IOException e) {
				form.log("Unable to update modules.");
				e.printStackTrace();
				launchGhost();
				return;
			}
			catch (UpdateNotFoundException e) {
				form.log("No update found.");
				launchGhost();
				return;
			}
			form.log("Modules updated! Unpacking..");
			File f = new File(LOCAL_CODEBASE + "modules" + Constants.FS);
			if (!f.exists())
				f.mkdirs();
			try {
				ZipFile zipFile = new ZipFile(zipped);
				Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
				while (enumeration.hasMoreElements()) {
					ZipEntry zipEntry = enumeration.nextElement();

					if (zipEntry.isDirectory())
						new File(f.getAbsolutePath() + Constants.FS + zipEntry.getName()).mkdir();
					else {
						File tf = new File(f.getAbsolutePath() + Constants.FS + zipEntry.getName());
						if (!tf.getParentFile().exists())
							tf.getParentFile().mkdirs();

						BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
						int size;
						byte[] buffer = new byte[2048];
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath() + Constants.FS + zipEntry.getName()), buffer.length);
						while ((size = bis.read(buffer, 0, buffer.length)) != -1)
							bos.write(buffer, 0, size);
						bos.flush();
						bos.close();
						bis.close();
					}
				}
				zipFile.close();
			}
			catch (Throwable t) {
				form.log("Unable to unpack modules.");
				t.printStackTrace();
				launchGhost();
				return;
			}
		}
		form.log("Update fully successful.");
		launchGhost();
	}

	/**
	 * Checks for a new version and downloads, ensuring that the download succeeded
	 * 
	 * @throws IOException if the file can not be downloaded
	 * @throws UpdateNotFoundException when no update is found
	 */
	private File checkAndDownload(String destFile, String remoteUrl) throws IOException, UpdateNotFoundException {
		String remoteHash = getRemoteHash(remoteUrl);
		String localHash = getLocalHash(destFile);
		boolean match = localHash != null && remoteHash.toLowerCase().equals(localHash.toLowerCase());
		if (match)
			throw new UpdateNotFoundException();
		File file = null;
		while (!match) {
			form.log("Update found.");
			form.log("Downloading new version..");
			file = HttpClient.saveToDisk(destFile, remoteUrl);
			form.log("Newest version downloaded.");
			form.log("Checking integrity..");
			localHash = getLocalHash(destFile);
			match = remoteHash.toLowerCase().equals(localHash.toLowerCase());
		}
		return file;
	}

	private void launchGhost() {
		try {
			form.log("Launching GHOST..");
			new ProcessBuilder("java", "-classpath", LOCAL_CODEBASE + JAR_PACKAGE_NAME, ENTRY_POINT).start();
			form.log("Exiting..");
			try {
				Thread.sleep(8000);
			}
			catch (InterruptedException ex) {
			}
			System.exit(0);
		}
		catch (IOException e) {
			form.log("Unable to launch GHOST: " + e.toString());
		}
	}

	/**
	 * Gets the hash from the file at the specified url
	 * 
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
	 * 
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
		catch (Exception ex) {
		} // Swallow irrelevant exceptions
		finally {
			try {
				if (bis != null)
					bis.close();
			}
			catch (IOException ex) {
			}
		}
		return null;
	}

	/**
	 * Converts an array of bytes to a hexidecimal string
	 * 
	 * @param bytes the bytes to convert
	 * @return the resulting string
	 */
	public static String toHexString(byte[] bytes) {
		char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v / 16];
			hexChars[j * 2 + 1] = hexArray[v % 16];
		}
		return new String(hexChars);
	}

	private class UpdateNotFoundException extends Exception {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final UpdaterFrame form = new UpdaterFrame();
				form.setOnUpdate(new Runnable() {
					@Override
					public void run() {
						new Thread(new Launcher(form)).start();
					}
				});
				form.setOnLaunch(new Runnable() {
					@Override
					public void run() {
						new Thread(new Runnable() {
							@Override
							public void run() {
								new Launcher(form).launchGhost();
							}
						}).start();
					}
				});
			}
		});
	}
}
