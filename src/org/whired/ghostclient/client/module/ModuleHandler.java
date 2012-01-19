package org.whired.ghostclient.client.module;

import java.util.HashSet;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.whired.ghost.constants.Vars;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghostclient.client.GhostClientFrame;
import org.whired.ghostclient.client.event.GhostEventAdapter;

/**
 * Handles modules
 * 
 * @author Whired
 */
public class ModuleHandler extends GhostEventAdapter {

	HashSet<Module> modules = new HashSet<Module>();
	private final GhostClientFrame frame;

	public ModuleHandler(Module[] initialModules, final GhostClientFrame frame) {
		this.frame = frame;
		for (Module m : initialModules) {
			registerModule(m);
		}
	}

	public ModuleHandler(final GhostClientFrame frame) {
		this.frame = frame;
	}

	/**
	 * Registers a module to this handler
	 * 
	 * @param module
	 */
	public final void registerModule(final Module module) {
		module.setFrame(frame);
		frame.getView().moduleAdded(module);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Vars.getLogger().log(Level.INFO, "Registering and initializing module " + module.getModuleName());

					module.load();
				}
				catch (Throwable t) {
					Vars.getLogger().log(Level.WARNING, "Error while registering module: ", t);
				}
			}
		});
		modules.add(module);
	}

	/**
	 * Unregisters a module from this handler
	 * 
	 * @param module
	 */
	protected void unregisterModule(Module module) {
		modules.remove(module);
	}

	/**
	 * Gets a module by its name
	 * 
	 * @param name the name of the module
	 * @return the {@link org.whired.ghostclient.ui.module.Module} for the
	 *         specified name, or {@code null} if the name was not matched
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
