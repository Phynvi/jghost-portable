package org.whired.ghost.net;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.whired.ghost.Vars;
import org.whired.ghost.net.packet.GhostAuthenticationPacket;
import org.whired.ghost.net.packet.PacketType;
import org.whired.ghost.net.packet.UnhandledPacket;

/**
 * @author Whired
 *
 * A friendly middle layer
 * between the connection protocols and the stream wrappers.
 * Keeps as much of the more advanced code as hidden as
 * possible.
 */
public class Connection implements SessionManager
{

	/** The WrappedInputStream to be utilized. */
	private final WrappedInputStream inputStream;
	/** The WrappedOutputStream to be utilized. */
	private final WrappedOutputStream outputStream;
	/** The Receivable to delegate events to. */
	private Receivable receivable = null;
	/** The password that must be received before client is accepted. */
	private String password = "";
	/** Specifies whether or not client must send password. */
	private boolean expectingPassword = false;
	/** Specifies whether or not the session is still valid */
	protected boolean sessionCorrupt = false;
	/** The session manager for this connection */
	protected final SessionManager manager;

	/**
	 * Creates a new connection with the specified streams and listeners
	 *
	 * @param inputStream the stream to read information from
	 * @param outputStream the stream to send information to
	 * @param receivable the Receivable to delegate events to
	 * @param manager the session manager for this connection
	 */
	public Connection(WrappedInputStream inputStream, WrappedOutputStream outputStream, Receivable receivable, SessionManager manager)
	{
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.receivable = receivable;
		this.manager = manager;
		setInternalManager();
		Vars.getLogger().fine("Running");
	}

	private void setInternalManager()
	{
		this.inputStream.setManager(this);
	}

	/**
	 * Creates a new connection with the specified streams and listeners
	 *
	 * @param inputStream the stream to read information from
	 * @param outputStream the stream to send information to
	 * @param receivable the receivable to delegate events to
	 */
	public Connection(WrappedInputStream inputStream, WrappedOutputStream outputStream, Receivable receivable)
	{
		this(inputStream, outputStream, receivable, null);
	}
	// TODO organize datamembers
	/** The Thread to interrupt when listening should stop */
	private Thread listenerThread = null;

	/**
	 * Listens for and handles all incoming packets
	 */
	protected void startReceiving()
	{
		Vars.getLogger().fine("Initiating new thread for packet listening..");
		listenerThread = new Thread(new Runnable()
		{

			public void run()
			{
				Vars.getLogger().fine("Listening for incoming packets on " + Thread.currentThread().getName());
				while (true)
				{
					try
					{
						inputStream.expectingNewPacket = true;
						handlePacket(inputStream.readByte(), inputStream.readByte());
						//handlePacket(inputStream.readUnsignedByte(), inputStream.readUnsignedByte());
						inputStream.expectingNewPacket = false;
					}
					catch (Exception real)
					{
						terminationRequested("Connection reset");
						break;
					}
				}
				Vars.getLogger().fine("Thread " + Thread.currentThread().getName() + " is exiting.");
			}
		}, "Packet Receiver");
		listenerThread.start();
	}

	private void handlePacket(int packetId, int length)
	{
		if (expectingPassword)
		{
			if (packetId == PacketType.AUTHENTICATION)
			{
				GhostAuthenticationPacket ap = new GhostAuthenticationPacket(this);
				ap.receive();
				if (ap.password.equals(password))
				{
					Vars.getLogger().fine("Password matched, client accepted.");
					expectingPassword = false;
				}
				else
				{
					terminationRequested("Password incorrect");
					//expectingPassword = false;
				}
			}
			else
			{
				terminationRequested("Password expected, but not received");
				//expectingPassword = false;
			}
		}
		else
		{
			if (packetId == 4)
			{
				try
				{
					System.out.println((String) inputStream.readObject());
					Exception e;
					if ((e = (Exception) inputStream.readObject()) != null)
					{
						Vars.getLogger().warning(e.toString());
						e.printStackTrace();
					}
				}
				catch (Exception ex)
				{
					Logger.getLogger(Connection.class.getName()).log(Level.WARNING, null, ex);
				}
			}
			else
			{
				Vars.getLogger().fine("Notifying receivable that external packet " + packetId + " has been received.");
				if(!receivable.handlePacket(packetId, length, this))
				{
					new UnhandledPacket(this, length).receive();
					Vars.getLogger().fine("Flushed unhandled packet " + packetId);
				}
			}
		}
	}

	/**
	 * Sends a packet with the specified id and payload
	 * <p>
	 * An example usage of this method might look like this:
	 * <pre><code>
	 * sendPacket(77, player.getName(), player.getIP());
	 * // Or
	 * sendPacket(15, player.getName(), player.getSkillLevel(0), player.getSkillLevel(1), player.getSkillLevel(2));
	 * </code></pre>
	 *
	 * Alternatively, {@link org.whired.ghost.net.packet.GhostPacket} can be extended to make this process easier.
	 * </p>
	 * @param packetId the id of the packet to send
	 * @param data the data (in correct order, separated by commas) to send
	 */
	public void sendPacket(int packetId, Object... data)
	{
		try
		{
			Vars.getLogger().fine("Sending packet " + packetId + ", which consists of " + data.length + " objects.");
			outputStream.writeByte(packetId);
			outputStream.writeByte(data.length);
			for (Object obj : data)
			{
				outputStream.writeObject(obj);
			}
		}
		catch (Exception e)
		{
			Vars.getLogger().warning("Unable to send packet " + packetId + ": " + e.toString());
		}
	}

	/**
	 * Closes and destructs the OutputStream
	 */
	private void removeOutputStream()
	{
		Vars.getLogger().fine("Removing outputStream. Thread: " + Thread.currentThread().getName());
		this.outputStream.closeStream();
	}

	/**
	 * Closes and destructs the InputStream
	 */
	private void removeInputStream()
	{
		Vars.getLogger().fine("Removing inputStream. Thread: " + Thread.currentThread().getName());
		this.inputStream.destruct();
	}

	/**
	 * Notifies the Connection that the specified password must be received in order for the
	 * session to continue.
	 *
	 * @param newPass the password that must be matched
	 */
	protected void setPassword(String newPass)
	{
		this.password = newPass;
		this.expectingPassword = true;
		Vars.getLogger().info("Password request acknowledged. Password is now expected.");
	}
	private boolean terminationRequested = false;

	/**
	 * Called then the session must be terminated.
	 */
	public void terminationRequested(String reason)
	{
		if (!terminationRequested)
		{
			sendPacket(4, "Terminating. Reason: "+reason);
			Vars.getLogger().info("Termination requested [Reason: " + (reason != null ? reason + "]" : "unspecified]") + " - Target: " + this);
			synchronized (this)
			{
				Vars.getLogger().fine("Lock acquired.");
				removeOutputStream();
				removeInputStream();
				terminationRequested = true;
				sessionCorrupt = true;
				Vars.getLogger().fine("Notifying");
				notify();
				Vars.getLogger().fine("Notified");
				if(manager != null)
				{
					manager.terminationRequested("Connection cleanup");
				}
			}
		}
		else
		{
			Vars.getLogger().fine("Termination request received, but termination is already occurring.");
		}
	}

	public void setEnforceTimeout(boolean b)
	{
		this.inputStream.enforceTimeout = b;
	}

	/**
	 * Gets the <code>WrappedInputStream</code> associated with this connection
	 *
	 * @return the stream to read data from
	 */
	public WrappedInputStream getInputStream()
	{
		return this.inputStream;
	}

	/**
	 * Gets the <code>WrappedOutputStream</code> associated with this connection
	 *
	 * @return the stream to send data to
	 */
	public WrappedOutputStream getOutputStream()
	{
		return this.outputStream;
	}
}
