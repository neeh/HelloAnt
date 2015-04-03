package com.polytech.di4.HelloAnt;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
	 * The nickname of the bot.
	 * @see Documentation/protocol/nickspecs.html
	 */
	private String nick;
	
	/**
	 * The socket used to communicate with the bot.
	 * @see Socket
	 */
	private Socket socket;
	
	/**
	 * The PrintWriter object used to send messages to the bot.
	 * The object is cached here to avoid creating a PrintWriter every time we need to 
	 * send a message to the bot.
	 * @see PrintWriter
	 */
	private PrintWriter printWriter;
	
	/**
	 * The gaming mode of the bot.
	 * @see BotMode
	 */
	private BotMode mode;
	
	/**
	 * The game in which the bot is currently in.
	 * null if bot isn't in game.
	 */
	private Game game;
	
	/**
	 * The general score of the bot.
	 * The bot score is updated after a match.
	 */
	private double score;
	
	/**
	 * Creates a new bot.
	 * @constructor
	 * @param nick the nickname of the bot.
	 * @param socket the socket used to communicate with the bot.
	 * @param mode the gaming mode chosen by the bot.
	 * @param score the current game score of the bot.
	 */
	public Bot(String nick, Socket socket, BotMode mode, double score)
	{
		this.nick = nick;
		
		this.socket = socket;
		printWriter = null;
		
		this.mode = mode;
		this.game = null;
		this.score = score;
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
	 * Sets the nickname of a bot.
	 * @param nick the new nickname of the bot.
	 */
	public void setNick(String nick)
	{
		this.nick = nick;
	}

	/**
	 * Gets the socket of a bot.
	 * @return the socket used to communicate with the bot.
	 */
	public Socket getSocket()
	{
		return socket;
	}
	
	/**
	 * Sets the socket of a bot and updates its print writer.
	 * @param socket the new socket used to communicate with the bot.
	 */
	public void setSocket(Socket socket)
	{
		this.socket = socket;
		if (socket != null)
		{
			try
			{
				printWriter = new PrintWriter(socket.getOutputStream());
			}
			catch (IOException e)
			{
				LOGGER.error("Cannot create PrintWriter for bot " + nick);
			}
		}
		else
		{
			printWriter = null;
		}
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
	public setBotMode(BotMode mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Returns whether the bot is currently playing.
	 * A bot which is not in game is waiting for a game.
	 * @return true if the bot is in a game
	 */
	public Boolean isInGame()
	{
		return game != null;
	}
	
	/**
	 * Gets the game in which the bot is playing.
	 * @return the game in which the bot is playing or null if the bot isn't in a game.
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
			LOGGER.error("Attemp to enroll the bot '" + nick + "' which is already in "
					+ "game");
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
	 * @param score the new score of the bot.
	 */
	public void setScore(double score)
	{
		this.score = score;
	}
	
	/**
	 * Send a message to a bot using its socket (via the cached PrintWriter objet).
	 * @param message the message to send to the bot.
	 */
	public void send(String message)
	{
		printWriter.println(message);
	}
}
