package org.whired.ghostclient.client.module;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import org.whired.ghost.Vars;

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
	public static final Module[] loadFromDisk(String dirPath) {
		HashSet<Module> modules = new HashSet<Module>();
		try {
			File dir = new File(dirPath);
			if (dir.exists()) {
				ClassLoader cl;
				Vars.getLogger().log(Level.INFO, "Loading modules from {0}..", dir.getPath());
				ArrayList<File> dirs = new ArrayList<File>();
				dirs.add(dir);
				for (File ff : dir.listFiles()) {
					if (ff.isDirectory()) {
						dirs.add(ff);
					}
				}
				for (File f : dirs) {
					cl = new URLClassLoader(new URL[]{f.toURI().toURL()});
					for (String sdir : f.list()) {
						if (sdir.toLowerCase().endsWith(".class") && !sdir.contains("$")) { // Don't load anonymous classes
							String classN = sdir.substring(0, sdir.toLowerCase().indexOf(".class"));
							try {
								Class cls = cl.loadClass(sdir.substring(0, sdir.toLowerCase().indexOf(".class")));
								Module m = (Module) cls.newInstance();
								m.setResourcePath(f.getPath());
								modules.add(m);
								Vars.getLogger().log(Level.INFO, "Loaded module from disk: {0}", classN);
							}
							catch (Throwable ex) {
								Vars.getLogger().log(Level.WARNING, "Error while loading module from disk: "+classN+":", ex);
							}
						}
					}
				}
			}
			else {
				Vars.getLogger().log(Level.WARNING, "No modules to load -- {0} does not exist", dirPath);
			}
		}
		catch (MalformedURLException ex) {
			Vars.getLogger().log(Level.SEVERE, "Failed to load modules from disk: ", ex);
		}
		return modules.toArray(new Module[modules.size()]);
	}
}
