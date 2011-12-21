package org.whired.ghostclient.client.module;

import java.util.HashSet;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghostclient.client.event.GhostEventAdapter;

/**
 * Handles modules
 * @author Whired
 */
public class ModuleHandler extends GhostEventAdapter {

	HashSet<Module> modules = new HashSet<Module>();
	
	/**
	 * Registers a module to this handler
	 * @param module 
	 */
	public void registerModule(Module module) {
		modules.add(module);
	}
	
	/**
	 * Unregisters a module from this handler
	 * @param module 
	 */
	public void unregisterModule(Module module) {
		modules.remove(module);
	}
	
	/**
	 * Gets a module by its name
	 * @param name the name of the module
	 * @return the {@link org.whired.ghostclient.ui.module.Module} for the
	 * specified name, or {@code null} if the name was not matched
	 */
	public Module moduleForName(String name) {
		for(Module m : modules)
			if(m.getModuleName().equals(name))
				return m;
		return null;
	}
	
	@Override
	public void packetReceived(GhostPacket packet) {
		for(Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if(a != null)
				a.packetReceived(packet);
		}
	}

	@Override
	public void playerAdded(Player player) {
		for(Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if(a != null)
				a.playerAdded(player);
		}
	}

	@Override
	public void playerRemoved(Player player) {
		for(Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if(a != null)
				a.playerRemoved(player);
		}
	}

	@Override
	public void privateMessageLogged(Player from, Player to, String message) {
		for(Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if(a != null)
				a.privateMessageLogged(from, to, message);
		}
	}

	@Override
	public void publicMessageLogged(Player from, String message) {
		for(Module m : modules) {
			GhostEventAdapter a = m.getEventListener();
			if(a != null)
				a.publicMessageLogged(from, message);
		}
	}
}
