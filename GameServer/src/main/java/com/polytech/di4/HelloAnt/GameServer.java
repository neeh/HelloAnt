package com.polytech.di4.HelloAnt;

import java.util.ArrayList;

/**
 * The game server is the main class of the project. It manages all the interactions
 * between the clients and the games.
 * @class
 * @author Nicolas
 */
public class GameServer implements TCPClientCommunicatorCallback
{
	private TCPClientListener listener;
	private Thread listenerThread;
	// private GameManager gamemgr;
	private ArrayList<TCPClientCommunicator> clients;
	private ArrayList<Bot> bots;
	
	/**
	 * The list of games currently played.
	 */
	@SuppressWarnings("unused")
	private ArrayList<Game> games;
	
	/**
	 * Creates a new game server. There should be only one game server in the application,
	 * That's why its constructor is declared as private.
	 * @param port the port to listen for client interactions.
	 */
	public GameServer(int port) {
		// Setup the database.
		DBInterface.init("dbants", "root", "");
        clients = new ArrayList<TCPClientCommunicator>();
        // Launch the listener that will receive client.
        listener = new TCPClientListener(this);
        listener.setPort(port);
        listenerThread = new Thread(listener);
        listenerThread.start();
	}
    
	/**
	 * Adds a client into the game server lobby.
	 * This method is the callback of the client listener.
	 * @param client the client to add
	 */
	@Override
	public void newClient(TCPClientCommunicator newClient)
	{
		clients.add(newClient);
    	System.out.println(clients.size()); // Oops!
	}

	@Override
	public void botConnected(Bot newBot)
	{
		System.out.println(newBot.getNick() + " connected (mode "+newBot.getMode().toString()+")");
	}

	@Override
	public void botDisconnected(Bot oldBot)
	{
		System.out.println(oldBot.getNick() + " DISCONNECTED !");
	}
}
