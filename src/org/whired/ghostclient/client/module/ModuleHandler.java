package org.whired.ghostclient.client.module;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.whired.ghost.Vars;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghostclient.client.ClientGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;

/**
 * Handles modules
 * @author Whired
 */
public class ModuleHandler extends GhostEventAdapter {

	HashSet<Module> modules = new HashSet<Module>();
	private final ClientGhostFrame frame;

	public ModuleHandler(final ClientGhostFrame frame) {
		this.frame = frame;
		try {
			File dir = new File(Vars.LOCAL_CODEBASE + "modules/extmap");
			if (dir.exists()) {
				URL[] url = new URL[]{dir.toURI().toURL()};
				ClassLoader cl = new URLClassLoader(url);
				Logger.getLogger(ModuleHandler.class.getName()).log(Level.INFO, "Loading modules from {0}..", dir.getPath());
				ArrayList<File> dirs = new ArrayList<File>();
				dirs.add(dir);
				for (File ff : dir.listFiles()) {
					if (ff.isDirectory()) {
						dirs.add(ff);
					}
				}
				for (File f : dirs) {
					for (String sdir : f.list()) {
						Logger.getLogger(ModuleHandler.class.getName()).log(Level.INFO, "Found file: {0}", sdir);
						if (sdir.toLowerCase().endsWith(".class") && !sdir.contains("$")) { // Don't load anonymous classes
							Logger.getLogger(ModuleHandler.class.getName()).log(Level.INFO, "Found class: {0}", sdir);
							try {
								String classN = sdir.substring(0, sdir.toLowerCase().indexOf(".class"));
								Class cls = cl.loadClass(classN);
								registerModule((Module) cls.newInstance(), dir.getPath());
								Logger.getLogger(ModuleHandler.class.getName()).log(Level.INFO, "Module registered: {0}", classN);
							}
							catch (Throwable ex) {
								//Logger.getLogger(ModuleHandler.class.getName()).log(Level.SEVERE, "Unable to dynamically load module " + sdir, ex);
								ex.printStackTrace();
							}
						}
					}
				}
			}
			else {
				Logger.getLogger(ModuleHandler.class.getName()).log(Level.INFO, "No modules to load.", dir.getPath());
			}
		}
		catch (MalformedURLException ex) {
			Logger.getLogger(ModuleHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Registers a module to this handler
	 * @param module 
	 */
	public final void registerModule(final Module module, final String resourceDir) {
		module.setFrame(frame);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				frame.getView().moduleAdded(module);
				module.load(resourceDir);
			}
		});
		modules.add(module);
	}

	/**
	 * Unregisters a module from this handler
	 * @param module 
	 */
	protected void unregisterModule(Module module) {
		modules.remove(module);
	}

	/**
	 * Gets a module by its name
	 * @param name the name of the module
	 * @return the {@link org.whired.ghostclient.ui.module.Module} for the
	 * specified name, or {@code null} if the name was not matched
	 */
	public Module moduleForName(String name) {
		for (Module m : modules) {
			if (m.getModuleName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	@Override
	public void packetReceived(GhostPacket packet) {
		for (Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.packetReceived(packet);
			}
		}
	}

	@Override
	public void playerAdded(Player player) {
		for (Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.playerAdded(player);
			}
		}
	}

	@Override
	public void playerRemoved(Player player) {
		for (Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.playerRemoved(player);
			}
		}
	}

	@Override
	public void privateMessageLogged(Player from, Player to, String message) {
		for (Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.privateMessageLogged(from, to, message);
			}
		}
	}

	@Override
	public void publicMessageLogged(Player from, String message) {
		for (Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.publicMessageLogged(from, message);
			}
		}
	}
}
