package org.whired.ghostclient.client;

import java.util.HashSet;
import java.util.Iterator;

import org.whired.ghost.Constants;
import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.player.Player;
import org.whired.ghost.player.RankHandler;
import org.whired.ghostclient.client.command.CommandHandler;
import org.whired.ghostclient.client.command.CommandMalformedException;
import org.whired.ghostclient.client.command.CommandNotFoundException;
import org.whired.ghostclient.client.module.ModuleHandler;
import org.whired.ghostclient.client.module.ModuleLoader;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * A client ghost frame
 * 
 * @author Whired
 */
public abstract class GhostClientFrame extends GhostFrame implements GhostClient {

	private CommandHandler commandHandler = new CommandHandler();
	private RankHandler rankHandler = new RankHandler();
	private ModuleHandler moduleHandler;
	private GhostClientView view;
	private final ClientPlayerList playerList = new ClientPlayerList(this) {

		HashSet<Player> players = new HashSet<Player>();

		@Override
		public void playerAdded(Player player) {
			view.playerAdded(player);
			moduleHandler.playerAdded(player);
			players.add(player);
		}

		@Override
		public void playerRemoved(Player player) {
			view.playerRemoved(player);
			moduleHandler.playerRemoved(player);
			players.remove(player);
		}

		@Override
		public Player[] getPlayers() {
			return this.players.toArray(new Player[players.size()]);
		}

		@Override
		public void playerSelected(Player player) {
			moduleHandler.playerSelected(player);
		}

		@Override
		public void removeAll() {
			Iterator<Player> it = players.iterator();
			Player next;
			while(it.hasNext()) {
				next = it.next();
				view.playerRemoved(next);
				moduleHandler.playerRemoved(next);
				it.remove();
			}
		}
	};

	public GhostClientFrame(GhostClientView view, GhostUser user) {
		this.view = view;
		super.setUser(user);
		super.getSessionManager().addEventListener(this);
		moduleHandler = new ModuleHandler(ModuleLoader.loadFromDisk(Constants.getLocalCodebase() + "modules" + Constants.FS, this.getUser().getSettings().getTabOrder()), this);
	}

	@Override
	public void packetReceived(GhostPacket packet) {
		moduleHandler.packetReceived(packet);
	}

	/**
	 * @return the command handler
	 */
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	/**
	 * @param commandHandler the command handler to set
	 */
	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	/**
	 * @return the rank handler
	 */
	@Override
	public RankHandler getRankHandler() {
		return rankHandler;
	}

	/**
	 * @param rankHandler the rank handler to set
	 */
	public void setRankHandler(RankHandler rankHandler) {
		this.rankHandler = rankHandler;
	}

	/**
	 * @return the module handler
	 */
	public ModuleHandler getModuleHandler() {
		return moduleHandler;
	}

	/**
	 * @param moduleHandler the module handler to set
	 */
	public void setModuleHandler(ModuleHandler moduleHandler) {
		this.moduleHandler = moduleHandler;
	}

	@Override
	public ClientPlayerList getPlayerList() {
		return this.playerList;
	}

	@Override
	public void setView(GhostClientView view) {
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
	public void handleCommand(String command) {
		try {
			getCommandHandler().handleInput(command);
		}
		catch (CommandMalformedException ex) {
			Constants.getLogger().warning("Command " + command + " malformed");
		}
		catch (CommandNotFoundException ex) {
			Constants.getLogger().warning("Command " + command + " not found");
		}
	}

	/*
	 * @Override public Rank getRankForPlayer(Player player) { return rankHandler.rankForLevel(player.getRights()); }
	 */
}
