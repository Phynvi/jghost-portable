package org.whired.ghostclient.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import org.whired.ghost.Constants;
import org.whired.ghost.net.GhostFrame;
import org.whired.ghost.net.packet.BodylessPacket;
import org.whired.ghost.net.packet.DebugPacket;
import org.whired.ghost.net.packet.GhostPacket;
import org.whired.ghost.net.packet.PacketHandler;
import org.whired.ghost.net.packet.PacketListener;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.PlayerConnectionPacket;
import org.whired.ghost.net.packet.PlayerListUpdatePacket;
import org.whired.ghost.net.packet.PlayerMovementPacket;
import org.whired.ghost.net.packet.PrivateChatPacket;
import org.whired.ghost.net.packet.PublicChatPacket;
import org.whired.ghost.player.GhostPlayer;
import org.whired.ghost.player.RankManager;
import org.whired.ghostclient.client.command.CommandMalformedException;
import org.whired.ghostclient.client.command.CommandManager;
import org.whired.ghostclient.client.command.CommandNotFoundException;
import org.whired.ghostclient.client.module.ModuleLoader;
import org.whired.ghostclient.client.module.ModuleManager;
import org.whired.ghostclient.client.user.GhostUser;

/**
 * A local ghost frame
 * @author Whired
 */
public abstract class LocalGhostFrame extends GhostFrame implements GhostClient {

	private PacketHandler packetHandler = new PacketHandler();
	private CommandManager commandManager = new CommandManager();
	private RankManager rankManager = new RankManager();
	private ModuleManager moduleManager;
	private GhostClientView view;
	/**
	 * The user of this frame
	 */
	private GhostUser ghostUser;
	private final ClientPlayerList playerList = new ClientPlayerList(this) {

		HashSet<GhostPlayer> players = new HashSet<GhostPlayer>();

		@Override
		public synchronized void playerAdded(final GhostPlayer player) {
			if (players.add(player)) {
				view.playerAdded(player);
				moduleManager.playerAdded(player);
			}
		}

		@Override
		public synchronized void playerRemoved(final GhostPlayer player) {
			if (players.remove(player)) {
				view.playerRemoved(player);
				moduleManager.playerRemoved(player);
			}
		}

		@Override
		public synchronized GhostPlayer[] getPlayers() {
			return this.players.toArray(new GhostPlayer[players.size()]);
		}

		@Override
		public void playerSelected(final GhostPlayer player) {
			moduleManager.playerSelected(player);
		}

		@Override
		public synchronized void removeAll() {
			final Iterator<GhostPlayer> it = players.iterator();
			GhostPlayer next;
			while (it.hasNext()) {
				next = it.next();
				view.playerRemoved(next);
				moduleManager.playerRemoved(next);
				it.remove();
			}
		}
	};

	public LocalGhostFrame(final GhostClientView view, final GhostUser user, final RankManager rankManager) {
		this.view = view;
		setUser(user);
		registerDefaultPackets();
		super.getSessionManager().addEventListener(this);
		this.rankManager = rankManager;
		moduleManager = new ModuleManager(ModuleLoader.loadFromDisk(Constants.getLocalCodebase() + "modules" + Constants.FS, this.getUser().getSettings().getTabOrder()), this);

	}

	public LocalGhostFrame(final GhostClientView view, final GhostUser user) {
		this.view = view;
		setUser(user);
		registerDefaultPackets();
		super.getSessionManager().addEventListener(this);
		moduleManager = new ModuleManager(ModuleLoader.loadFromDisk(Constants.getLocalCodebase() + "modules" + Constants.FS, this.getUser().getSettings().getTabOrder()), this);
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

	/**
	 * Sets the user of this frame
	 * @param user the user to set
	 */
	public void setUser(final GhostUser user) {
		this.ghostUser = user;
	}

	/**
	 * Gets the user of this frame
	 * @return the user if one exists, otherwise null
	 */
	public GhostUser getUser() {
		return this.ghostUser;
	}

	private final void registerDefaultPackets() {
		packetHandler.registerPacket(new BodylessPacket(PacketType.AUTHENTICATE_SUCCESS), new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				getSessionManager().sessionOpened();
				Constants.getLogger().info("Successfully connected, requesting player list");
				try {
					getSessionManager().getConnection().getOutputStream().writeByte(PacketType.UPDATE_PLAYER_LIST);
				}
				catch (IOException e) {
					getSessionManager().removeConnection("Unable to request player list");
				}
			}
		});
		packetHandler.registerPacket(new PlayerListUpdatePacket(), new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				final PlayerListUpdatePacket plp = (PlayerListUpdatePacket) packet;
				for (final GhostPlayer p : plp.onlinePlayers) {
					getPlayerList().addPlayer(p);
				}
			}
		});
		packetHandler.registerPacket(new PublicChatPacket(), new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				final PublicChatPacket pc = (PublicChatPacket) packet;
				displayPublicChat(pc.sender, pc.message);
			}
		});
		packetHandler.registerPacket(new PlayerConnectionPacket(), new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				final PlayerConnectionPacket pcp = (PlayerConnectionPacket) packet;
				if (pcp.connectionType == PlayerConnectionPacket.CONNECTING) {
					getPlayerList().addPlayer(pcp.player);
				}
				else if (pcp.connectionType == PlayerConnectionPacket.DISCONNECTING) {
					getPlayerList().removePlayer(pcp.player);
				}
			}
		});
		packetHandler.registerPacket(new PrivateChatPacket(), new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				final PrivateChatPacket prc = (PrivateChatPacket) packet;
				displayPrivateChat(prc.sender, prc.recipient, prc.message);
			}
		});
		packetHandler.registerPacket(new DebugPacket(), new PacketListener() {
			@Override
			public void packetReceived(GhostPacket packet) {
				final DebugPacket dpacket = (DebugPacket) packet;
				Constants.getLogger().log(Level.parse(Integer.toString(dpacket.level)), "[REMOTE] " + dpacket.message);
			}
		});
		packetHandler.registerPacket(new PlayerMovementPacket());
	}

	/**
	 * @return the packetHandler
	 */
	public PacketHandler getPacketHandler() {
		return packetHandler;
	}

	/**
	 * @param packetHandler the packetHandler to set
	 */
	public void setPacketHandler(final PacketHandler packetHandler) {
		this.packetHandler = packetHandler;
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
