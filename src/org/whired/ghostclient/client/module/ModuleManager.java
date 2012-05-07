package org.whired.ghostclient.client.module;

import java.util.HashSet;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.whired.ghost.Constants;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghostclient.client.LocalGhostFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;

/**
 * Handles modules
 * @author Whired
 */
public class ModuleManager extends GhostEventAdapter {

	HashSet<Module> modules = new HashSet<Module>();
	private final LocalGhostFrame frame;

	public ModuleManager(final Module[] initialModules, final LocalGhostFrame frame) {
		this.frame = frame;
		for (final Module m : initialModules) {
			registerModule(m);
		}
	}

	public ModuleManager(final LocalGhostFrame frame) {
		this.frame = frame;
	}

	/**
	 * Registers a module to this manager
	 * @param module
	 */
	public final void registerModule(final Module module) {
		module.setFrame(frame);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						frame.getView().moduleAdded(module);
					}
					catch (final Throwable t) {
						Constants.getLogger().log(Level.WARNING, "Error while registering module: ", t);
					}
				}
			});
		}
		catch (final Throwable e) {
			e.printStackTrace();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					frame.getView().moduleAdded(module);
					Constants.getLogger().log(Level.INFO, "Registering and initializing module " + module.getModuleName());
					module.load();
				}
				catch (final Throwable t) {
					Constants.getLogger().log(Level.WARNING, "Error while registering module: ", t);
				}
			}
		});
		modules.add(module);
	}

	/**
	 * Unregisters a module from this manager
	 * @param module
	 */
	protected void unregisterModule(final Module module) {
		modules.remove(module);
	}

	/**
	 * Gets a module by its name
	 * @param name the name of the module
	 * @return the {@link org.whired.ghostclient.ui.module.Module} for the specified name, or {@code null} if the name was not matched
	 */
	public Module moduleForName(final String name) {
		for (final Module m : modules) {
			if (m.getModuleName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	@Override
	public void playerAdded(final GhostPlayer player) {
		for (final Module m : modules) {
			final GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.playerAdded(player);
			}
		}
	}

	@Override
	public void playerRemoved(final GhostPlayer player) {
		for (final Module m : modules) {
			final GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.playerRemoved(player);
			}
		}
	}

	@Override
	public void privateMessageLogged(final GhostPlayer from, final GhostPlayer to, final String message) {
		for (final Module m : modules) {
			final GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.privateMessageLogged(from, to, message);
			}
		}
	}

	@Override
	public void publicMessageLogged(final GhostPlayer from, final String message) {
		for (final Module m : modules) {
			final GhostEventAdapter a = m.getEventListener();
			if (a != null) {
				a.publicMessageLogged(from, message);
			}
		}
	}
}
