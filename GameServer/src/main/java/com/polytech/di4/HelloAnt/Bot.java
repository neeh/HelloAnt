package com.polytech.di4.HelloAnt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a bot in the game server.
 * The bot can be playing a game or waiting for a game in the lobby.
 * @class
 * @author Nicolas
 */
public class Bot {
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
	 * The database handler used to manipulate bot data on the database.
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
	 * Sets the gaming mode of a bot.
	 * @param mode the new gaming mode of the bot.
	 * @see BotMode
	 */
	public void setMode(BotMode mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Returns whether the bot is currently playing in a game.
	 * A bot which is not in game is waiting for a game.
	 * @return true if the bot is in a game
	 */
	public Boolean isInGame()
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
	 * Sets the game of a bot.
	 * @param game the game the bot is in.
	 */
	public void setGame(Game game)
	{
		if (this.game == null)
		{
			this.game = game;
		}
		else
		{
			LOGGER.warn("Attempt to change the game of a bot which is already in a game");
		}
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
	 * Sets the general score of a bot.
	 * The score is also updated on the database.
	 * @param score the new score of the bot.
	 */
	public void setScore(double score)
	{
		this.score = score;
		dbHandler.updateBotScore(this);
	}
}
