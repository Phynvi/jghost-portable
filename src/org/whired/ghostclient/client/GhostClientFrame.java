package org.whired.ghostclient.client;

import java.util.HashSet;
import java.util.Iterator;

import org.whired.ghost.Constants;
import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.RankManager;
import org.whired.ghostclient.client.command.CommandMalformedException;
import org.whired.ghostclient.client.command.CommandManager;
import org.whired.ghostclient.client.command.CommandNotFoundException;
import org.whired.ghostclient.client.module.ModuleLoader;
import org.whired.ghostclient.client.module.ModuleManager;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * A client ghost frame
 * @author Whired
 */
public abstract class GhostClientFrame extends GhostFrame implements GhostClient {

	private CommandManager commandManager = new CommandManager();
	private RankManager rankManager = new RankManager();
	private ModuleManager moduleManager;
	private GhostClientView view;
	private final ClientPlayerList playerList = new ClientPlayerList(this) {

		HashSet<Player> players = new HashSet<Player>();

		@Override
		public synchronized void playerAdded(final Player player) {
			view.playerAdded(player);
			moduleManager.playerAdded(player);
			players.add(player);
		}

		@Override
		public synchronized void playerRemoved(final Player player) {
			view.playerRemoved(player);
			moduleManager.playerRemoved(player);
			players.remove(player);
		}

		@Override
		public synchronized Player[] getPlayers() {
			return this.players.toArray(new Player[players.size()]);
		}

		@Override
		public void playerSelected(final Player player) {
			moduleManager.playerSelected(player);
		}

		@Override
		public synchronized void removeAll() {
			final Iterator<Player> it = players.iterator();
			Player next;
			while (it.hasNext()) {
				next = it.next();
				view.playerRemoved(next);
				moduleManager.playerRemoved(next);
				it.remove();
			}
		}
	};

	public GhostClientFrame(final GhostClientView view, final GhostUser user, final RankManager rankManager) {
		this.view = view;
		super.setUser(user);
		super.getSessionManager().addEventListener(this);
		this.rankManager = rankManager;
		moduleManager = new ModuleManager(ModuleLoader.loadFromDisk(Constants.getLocalCodebase() + "modules" + Constants.FS, this.getUser().getSettings().getTabOrder()), this);
	}

	public GhostClientFrame(final GhostClientView view, final GhostUser user) {
		this.view = view;
		super.setUser(user);
		super.getSessionManager().addEventListener(this);
		moduleManager = new ModuleManager(ModuleLoader.loadFromDisk(Constants.getLocalCodebase() + "modules" + Constants.FS, this.getUser().getSettings().getTabOrder()), this);
	}

	@Override
	public void packetReceived(final GhostPacket packet) {
		moduleManager.packetReceived(packet);
	}

	/**
	 * @return the command manager
	 */
	public CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * @param commandManager the command manager to set
	 */
	public void setCommandManager(final CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	/**
	 * @return the rank manager
	 */
	@Override
	public RankManager getRankManager() {
		return rankManager;
	}

	/**
	 * @param rankManager the rank manager to set
	 */
	public void setRankManager(final RankManager rankManager) {
		this.rankManager = rankManager;
	}

	/**
	 * @return the module manager
	 */
	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	/**
	 * @param moduleManager the module manager to set
	 */
	public void setModuleManager(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public ClientPlayerList getPlayerList() {
		return this.playerList;
	}

	@Override
	public void setView(final GhostClientView view) {
		this.view = view;
	}

	/**
	 * @return the view
	 */
	@Override
	public GhostClientView getView() {
		return view;
	}

	@Override
	public void handleCommand(final String command) {
		try {
			getCommandManager().handleInput(command);
		}
		catch (final CommandMalformedException ex) {
			Constants.getLogger().warning("Command " + command + " malformed");
		}
		catch (final CommandNotFoundException ex) {
			Constants.getLogger().warning("Command " + command + " not found");
		}
	}
}
