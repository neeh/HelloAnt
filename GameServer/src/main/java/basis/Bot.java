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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a bot in the game server.
 * The bot can be playing a game or waiting for a game in the lobby.
 * @class
 * @author Nicolas
 */
public class Bot
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	
	/**
	 * The communicator used to communicate with the bot.
	 */
	private TCPClientCommunicator com;
	
	/**
	 * The nickname of the bot.
	 * The string has to respect some conventions.
	 * @see Documentation/protocol/nickspecs.html
	 */
	private String nick;
	
	/**
	 * The gaming mode of the bot.
	 * Defines how the bot will join a game.
	 * @see BotMode
	 */
	private BotMode mode;
	
	/**
	 * The game in which the bot is currently in.
	 * Is null when the bot isn't in game.
	 * @see Game
	 */
	private Game game;
	
	/**
	 * The general score of the bot.
	 * The bot score is updated after a game.
	 */
	private double score;
	
	/**
	 * The priority of the bot in the game manager lobby.
	 */
	private int priority;
	
	/**
	 * The database handler used to manipulate bot data on the database.
	 * Is null for fake bots.
	 */
	private BotDBCallback dbHandler;
	
	/**
	 * Creates a new bot.
	 * @constructor
	 * @param com the communicator used to communicate with the bot.
	 * @param nick the nickname of the bot.
	 * @param mode the gaming mode chosen by the bot.
	 * @param score the current game score of the bot.
	 * @param dbHandler the interface used to call back the database.
	 */
	public Bot(TCPClientCommunicator com, String nick, BotMode mode, double score,
			BotDBCallback dbHandler)
	{
		this.com = com;
		this.nick = nick;
		this.mode = mode;
		this.game = null;
		this.score = score;
		this.dbHandler = dbHandler;
	}
	
	/**
	 * Gets the communicator used to communicate with the bot.
	 * @return the client communicator.
	 */
	public TCPClientCommunicator getCommunicator()
	{
		return com;
	}
	
	/**
	 * Returns whether the bot is a fake bot.
	 * A fake bot is a bot created by the server to play with real bots.
	 * @return true if the bot is a fake, false otherwise.
	 */
	public boolean isFake()
	{
		return com instanceof FakeCommunicator;
	}
	
	/**
	 * Gets the nickname of a bot.
	 * @return the nickname of the bot.
	 */
	public String getNick()
	{
		return nick;
	}
	
	/**
	 * Gets the gaming mode of a bot.
	 * @return the gaming mode of the bot.
	 * @see BotMode
	 */
	public BotMode getMode()
	{
		return mode;
	}
	
	/**
	 * Returns whether the bot is currently playing in a game.
	 * A bot which is not in game is waiting for a game.
	 * @return true if the bot is in a game
	 */
	public boolean isInGame()
	{
		return (game != null);
	}
	
	/**
	 * Gets the game in which the bot is currently playing.
	 * @return the game in which the bot is playing OR null if the bot isn't in a game.
	 */
	public Game getGame()
	{
		return game;
	}
	
	/**
	 * Gets the general score of a bot.
	 * @return the score of the bot.
	 */
	public double getScore()
	{
		return score;
	}
	
	/**
	 * Gets the priority of a bot.
	 * @return the priority of the bot.
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 * Sets the gaming mode of a bot.
	 * @param mode the new gaming mode of the bot.
	 * @see BotMode
	 */
	public void setMode(BotMode mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Sets the game of a bot.
	 * @param game the game the bot is in.
	 */
	public void setGame(Game game)
	{
		if (game == null || this.game == null)
		{
			this.game = game;
		}
		else
		{
			LOGGER.warn("Attempt to change the game of a bot which is already in a game");
		}
	}
	
	/**
	 * Sets the general score of a bot.
	 * The score is also updated on the database.
	 * @param score the new score of the bot.
	 */
	public void setScore(double score)
	{
		if (this.mode != BotMode.TRAINING && dbHandler != null)
		{	// If the handler is null, it probably means that the bot does not want its
			// score to be updated in the database.
			// Actually, I use it to prevent the score of a fake bot from being updated
			// (see GameThread.java, AntGame.java in ants pkg)
			// Poor fix but... It works! neeh-
			this.score = score;
			dbHandler.updateBotScore(this);
		}
	}
	
	/**
	 * Increment the priority of a bot in the game lobby.
	 */
	public void incPriority()
	{
		priority++;
	}
	
	/**
	 * Decrement the priority of a bot in the game lobby.
	 */
	public void decPriority()
	{
		if (priority > 0) priority--;
	}
	
	/** 
	 * Reset to 0 the priority of a bot.
	 */
	public void resetPriority()
	{
		priority = 0;
	}
}
