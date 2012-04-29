package org.whired.ghostclient.client.module;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.whired.ghost.Constants;

/**
 * Loads modules
 * @author Whired
 */
public class ModuleLoader {

	/**
	 * Loads modules at the specified directory from the disk
	 * @param dir the directory that contains modules
	 * @return the collection of modules that were loaded
	 */
	public static final Module[] loadFromDisk(final String dirPath, final String[] order) {
		final HashSet<Module> modules = new HashSet<Module>();
		try {
			final File dir = new File(dirPath);
			if (dir.exists()) {
				Constants.getLogger().log(Level.INFO, "Loading modules from {0}..", dir.getPath());
				final ArrayList<File> dirs = new ArrayList<File>();
				dirs.add(dir);
				for (final File ff : dir.listFiles()) {
					if (ff.isDirectory()) {
						dirs.add(ff);
					}
				}
				for (final File f : dirs) {
					final ClassLoader cl = new URLClassLoader(new URL[] { f.toURI().toURL() });
					for (final String sdir : f.list()) {
						if (sdir.toLowerCase().endsWith(".class") && !sdir.contains("$")) {
							final String classN = sdir.substring(0, sdir.toLowerCase().indexOf(".class"));

							try {
								SwingUtilities.invokeAndWait(new Runnable() {

									@Override
									public void run() {
										try {
											final Class<?> cls = cl.loadClass(sdir.substring(0, sdir.toLowerCase().indexOf(".class")));
											final Module m = (Module) cls.newInstance();
											m.setResourcePath(f.getPath());
											modules.add(m);
											Constants.getLogger().log(Level.INFO, "Loaded module from disk: {0}", classN);
										}
										catch (final Throwable ex) {
											if (ex instanceof ClassCastException) {
												Constants.getLogger().log(Level.WARNING, classN + " is not a " + Module.class.getName());
											}
											else {
												Constants.getLogger().log(Level.WARNING, "Error while loading module from disk: " + classN + ":", ex);
											}
										}
									}
								});
							}
							catch (final InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (final InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			else {
				Constants.getLogger().log(Level.WARNING, "No modules to load -- {0} does not exist", dirPath);
			}
		}
		catch (final MalformedURLException ex) {
			Constants.getLogger().log(Level.SEVERE, "Failed to load modules from disk: ", ex);
		}
		final LinkedHashSet<Module> smod = new LinkedHashSet<Module>();
		// Add known modules in order
		for (final String s : order) {
			for (final Module m : modules.toArray(new Module[modules.size()])) {
				if (s.equals(m.getModuleName())) {
					smod.add(m);
					modules.remove(m);
					break;
				}
			}
		}
		// Add unknown modules to end
		for (final Module m : modules) {
			smod.add(m);
		}
		return smod.toArray(new Module[smod.size()]);
	}
}
