package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game server is the main class of the project. It manages all the interactions
 * between the clients and the games.
 * @class
 * @author Nicolas
 */
public class GameServer implements TCPClientHandler, GameHandler
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);
	
	/**
	 * The client listener used to listen for incoming TCP connections and creating
	 * communicators to exchange with these clients.
	 */
	@SuppressWarnings("unused")
	private TCPClientListener listener;
	
	/**
	 * The game manager used to create games when bots are available for a match.
	 */
	protected GameManager gameManager;
	
	/**
	 * The list of clients who are communicating with the game server.
	 */
	private ArrayList<TCPClientCommunicator> clients;
	
	/**
	 * The list of games being played.
	 */
	//private ArrayList<GameThread> gameThreads;
	
	/**
	 * Creates a new game server.
	 * @param port the port to listen for client interactions.
	 */
	public GameServer(int port)
	{
		// Setup the database.
		DBInterface.init("dbants", "root", "");
		clients = new ArrayList<TCPClientCommunicator>();
		// Create the listener that will receive client.
		listener = new TCPClientListener(port, this);
	}
	
	/**
	 * Handles a client who just connected on the server.
	 * Connected clients are added to the game server lobby.
	 * @param client the client to add on the game server.
	 */
	@Override
	public void handleClientConnected(TCPClientCommunicator newClient)
	{
		clients.add(newClient);
		System.out.println(System.currentTimeMillis() + ": client joined");
	}
	
	/**
	 * Handles a client who just disconnected from the server.
	 * Disconnected clients are removed from the game server lobby.
	 * @param oldClient the client to remove from the game server.
	 */
	@Override
	public void handleClientDisconnected(TCPClientCommunicator oldClient)
	{
		clients.remove(oldClient);
		System.out.println(System.currentTimeMillis() + ": client left");
	}
	
	/**
	 * Handles a bot that just logged in.
	 * @param newBot the bot that logged in.
	 */
	@Override
	public void handleBotLogin(Bot newBot)
	{
		if (newBot.getMode() == BotMode.TRAINING)
		{	// Create a game for the bot.
			// TODO
		}
		else
		{	// Add the bot into the lobby.
			gameManager.addBot(newBot);
		}
		System.out.println(System.currentTimeMillis() + ": Bot " + newBot.getNick()
				+ " logged in (" + newBot.getMode().toString().toLowerCase() + " mode)");
	}
	
	/**
	 * Handles a bot that just logged out.
	 * @param oldBot the bot that logged out.
	 */
	@Override
	public void handleBotLogout(Bot oldBot)
	{
		// Attempt to remove the bot from the lobby.
		gameManager.removeBot(oldBot);
		System.out.println(System.currentTimeMillis() + ": Bot " + oldBot.getNick()
				+ " logged out");
	}
	
	/**
	 * Handles a game that was just created.
	 * @param game the created game.
	 */
	@Override
	public void handleGameCreated(Game game)
	{
		//Thread gameThread = new GameThread(game);
		//gameThreads.add(gameThread);
		//gameThread.start();
		System.out.println(System.currentTimeMillis() + ": Game created");
	}
	
	/**
	 * Handles a game that was just terminated.
	 * @param game the terminated game.
	 */
	public void handleGameTerminated(Game game)
	{
		// TODO
	}
}
