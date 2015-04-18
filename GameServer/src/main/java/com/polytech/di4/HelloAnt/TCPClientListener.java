package com.polytech.di4.HelloAnt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client listener is responsible for accepting incoming TCP connections and
 * creating, for each of those, a communicator that will enable the server to exchange
 * with them.
 * This class can be instantiated multiple times to listen on several network ports.
 * @class
 * @author Nicolas
 */
public class TCPClientListener implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClientListener.class);
	
	/**
	 * The server socket used to accept incoming TCP communications.
	 * @see ServerSocket
	 */
	private ServerSocket serverSocket;
	
	/**
	 * The network port on which to listen connections.
	 */
	private int port;
	
	/**
	 * The thread in charge of running the client listener mechanics.
	 * The client listener is autonomous because it runs its own thread.
	 * @see Thread
	 */
	private Thread listenerThread;
	
	/**
	 * The client handler that is passed to created communicators and which enables a
	 * client to call the server back when specific events occur.
	 */
	private TCPClientHandler handler;
	
	/**
	 * Creates a new TCP client listener which listens the network for incoming TCP
	 * connections.
	 * @constructor
	 * @param port the network port on which to listen.
	 * @param handler the client handler for communicators that will be created.
	 * @warning the port is supposed to be a 16-bit unsigned integer.
	 */
	public TCPClientListener(int port, TCPClientHandler handler)
	{
		this.port = port;
		this.handler = handler;
		try
		{
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(port));
			// Create the thread
			listenerThread = new Thread(this);
			listenerThread.start();
		}
		catch (IOException e)
		{
			// TODO: Remove this print.
			e.printStackTrace();
			LOGGER.error("GRAVE: The client listener cannot start!\n" + e.getMessage());
		}
	}
	
	/**
	 * Runs the client listening mechanics which is to listen for incoming TCP
	 * connections, create communicators and send those back to the game server.
	 */
	public void run()
	{
		while (true)
		{
			try
			{	// Accept incoming TCP connection. (blocking)
				Socket socket = serverSocket.accept();
				// A connection is accepted, create a communicator for it.
				new TCPClientCommunicator(socket, handler);
			}
			catch (IOException e)
			{
				LOGGER.error("Cannot accept a connection on the game server\n"
						+ e.getMessage());
			}
		}
	}
	
	/**
	 * Gets the port on which the listener is accepting clients.
	 * @return the port of the server.
	 */
	public int getPort()
	{
		return port;
	}
}
