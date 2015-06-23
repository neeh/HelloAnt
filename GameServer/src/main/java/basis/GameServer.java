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
	 * Whether the server is in the process of closing or not.
	 */
	protected boolean closing;
	
	/**
	 * Creates a new game server.
	 * @param port the port to listen for client interactions.
	 * @throws IllegalArgumentException if the port is already used.
	 */
	public GameServer(int port) throws IllegalArgumentException
	{
		closing = false;
		// Setup the database.
		DBManager.init("dbants", "root", "");
		// Create client and game threads list
		clients = new ArrayList<TCPClientCommunicator>();
		gameThreads = new ArrayList<GameThread>();
		// Create the listener that will receive client.
		listener = new TCPClientListener(port, this);
	}
	
	/**
	 * Closes the server and every associated thread.
	 */
	public void close()
	{
		closing = true;
		listener.stop();
		for (GameThread gameThread : gameThreads)
		{
			gameThread.interrupt();
		}
		while (!clients.isEmpty())
		{
			clients.get(0).close("Server is closing");
		}
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
		LOGGER.info("client joined");
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
		LOGGER.info("client left");
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
		LOGGER.info("bot logged in (nick: " + newBot.getNick() + ", mode: "
				+ newBot.getMode().toString().toLowerCase() + ")");
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
		LOGGER.info("bot logged out (nick: " + oldBot.getNick() + ")");
	}
	
	/**
	 * Adds a game on the game server and creates a thread to run it.
	 * @param game the instance of the game to add.
	 */
	@Override
	public void addGame(Game game)
	{
		// Remove bots from the lobby.
		Iterator<Bot> botIt = game.getBotIterator();
		while (botIt.hasNext())
		{
			gameManager.removeBot(botIt.next());
		}
		// Create the game thread.
		GameThread gameThread = new GameThread(game, this);
		gameThreads.add(gameThread);
		// Start the game.
		gameThread.start();
		// Log game informations.
		Iterator<TCPClientCommunicator> clientIt = clients.iterator();
		TCPClientCommunicator client;
		int clientsInGame = 0;
		int gameCount = gameThreads.size();
		while (clientIt.hasNext())
		{
			client = clientIt.next();
			if (client.isBotLoggedIn() && client.getBot().isInGame())
			{
				clientsInGame++;
			}
		}
		LOGGER.info("game created (" + clientsInGame + " client"
				+ (clientsInGame > 1 ? "s are" : " is") + " in " + gameCount + " game"
				+ (gameCount > 1 ? "s)" : ")"));
	}
	
	/**
	 * Terminates a game thread on the game server.
	 * @param gameThread the game thread to remove.
	 */
	public void removeGameThread(GameThread gameThread)
	{
		gameThreads.remove(gameThread);
		// If the server is closing, don't reinsert the bots.
		if (closing)
		{
			return;
		}
		// Reinsert bots in the game manager lobby.
		Iterator<Bot> botIt = gameThread.getGame().getBotIterator();
		while (botIt.hasNext())
		{
			Bot bot = botIt.next();
			// He is not in a game anymore
			bot.setGame(null);
			if (!bot.isFake())
			{	// If it's a real bot, add it to the lobby.
				if (bot.getCommunicator().isBotLoggedIn())
				{	// TRICKY FIX /!\
					// -------------------------------------------------------------------
					// Sometimes a bot can suddenly quit/logout during a game. But the
					// game still needs the data of the bot to run the game normally, so
					// the game keeps the communicator and the bot instance of a bot that
					// is no longer logged in.
					// Therefore, I had to check that the bot is still logged in.
					// -------------------------------------------------------------------
					gameManager.addBot(bot);
				}
			}
		}
		// Log game informations.
		Iterator<TCPClientCommunicator> clientIt = clients.iterator();
		TCPClientCommunicator client;
		int clientsInGame = 0;
		int gameCount = gameThreads.size();
		while (clientIt.hasNext())
		{
			client = clientIt.next();
			if (client.isBotLoggedIn() && client.getBot().isInGame())
			{
				clientsInGame++;
			}
		}
		LOGGER.info("game terminated (" + clientsInGame + " client"
				+ (clientsInGame > 1 ? "s are" : " is") + " in " + gameCount + " game"
				+ (gameCount > 1 ? "s)" : ")"));
	}
}
