/* 
 * This source file is part of HelloAnt.
 * 
 * Coyright(C) 2015 Nicolas Monmarché
 * 
 * HelloAnt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HelloAnt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HelloAnt.  If not, see <http://www.gnu.org/licenses/>.
 */

package basis;

import java.util.ArrayList;
import java.util.Iterator;

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
	protected ArrayList<GameThread> gameThreads;
	
	/**
	 * Creates a new game server.
	 * @param port the port to listen for client interactions.
	 */
	public GameServer(int port)
	{
		// Setup the database.
		DBManager.init("dbants", "root", "");
		// Create client and game threads list
		clients = new ArrayList<TCPClientCommunicator>();
		gameThreads = new ArrayList<GameThread>();
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
		// In the generic game server, we don't care about gaming mode.
		gameManager.addBot(newBot);
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
	 * Adds a game on the game server and creates a thread to run it.
	 * @param game the instance of the game to add.
	 */
	@Override
	public void addGame(Game game)
	{
		GameThread gameThread = new GameThread(game, this);
		gameThreads.add(gameThread);
		// Start the game.
		gameThread.start();
		System.out.println(System.currentTimeMillis() + ": Game created");
	}
	
	/**
	 * Removes a game thread from the game server.
	 * @param gameThread the game thread to remove.
	 */
	public void removeGameThread(GameThread gameThread)
	{
		gameThreads.remove(gameThread);
		// Reinsert bots in the game manager lobby.
		Iterator<Bot> botIt = gameThread.getGame().getBotIterator();
		while (botIt.hasNext())
		{
			Bot bot = botIt.next();
			if (bot.isFake() == false)
			{	// If it's a real bot, add it to the lobby.
				gameManager.addBot(bot);
			}
		}
		System.out.println(System.currentTimeMillis() + ": Game terminated");
	}
}
