package org.whired.ghostclient.client;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.net.event.SessionEventListener;
import org.whired.ghost.net.model.GhostFrame;
import org.whired.ghost.net.model.player.Player;
import org.whired.ghost.net.model.player.RankHandler;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghostclient.client.command.CommandHandler;
import org.whired.ghostclient.client.command.CommandMalformedException;
import org.whired.ghostclient.client.command.CommandNotFoundException;
import org.whired.ghostclient.client.module.Module;
import org.whired.ghostclient.client.module.ModuleHandler;

/**
 * A client ghost frame
 * @author Whired
 */
public abstract class ClientGhostFrame extends GhostFrame implements GhostClient {

	private CommandHandler commandHandler = new CommandHandler();
	private RankHandler rankHandler = new RankHandler();
	private ModuleHandler moduleHandler = new ModuleHandler();
	private GhostClientView view;
	private ClientPlayerList playerList = new ClientPlayerList(this) {

		HashSet<Player> players = new HashSet<Player>();
		
		@Override
		public void playerAdded(Player player) {
			players.add(player);
			System.out.println("Notify controller");
		}

		@Override
		public void playerRemoved(Player player) {
			players.remove(player);
			System.out.println("Notify controller");
		}

		@Override
		public Player[] getPlayers() {
			return this.players.toArray(new Player[players.size()]);
		}
		
	};
	
	public ClientGhostFrame() {
		super.getSessionManager().addEventListener(this);
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
	
	/**
	 * Adds a module to this frame
	 * @param module the module to add
	 */
	public void addModule(Module module) {
		module.setFrame(this);
		moduleHandler.registerModule(module);
		view.moduleAdded(module);
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
			Logger.getLogger(ClientGhostFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (CommandNotFoundException ex) {
			Logger.getLogger(ClientGhostFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
