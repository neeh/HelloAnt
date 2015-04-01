package com.polytech.di4.HelloAnt;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Bot class.
 * @class
 * @author Nicolas
 */
public class Bot
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	private String pseudo;
	private Socket socket;
	private PrintWriter printWriter;
	private double score;
	
	/**
	 * Creates a new bot.
	 * @constructor
	 * @param pseudo the pseudonym of the bot
	 * @param socket the socket used to communicate with the bot
	 * @param score  the current game score of the bot
	 */
	public Bot(String pseudo, Socket socket, double score)
	{
		this.pseudo = pseudo;
		this.socket = socket;
		printWriter = null;
		this.score = score;
	}

	/**
	 * Gets the pseudonym of a bot.
	 * @return the pseudonym of the bot
	 */
	public String getPseudo()
	{
		return pseudo;
	}

	/**
	 * Sets the pseudonym of a bot.
	 * @param pseudo the new pseudonym of the bot
	 */
	public void setPseudo(String pseudo)
	{
		this.pseudo = pseudo;
	}

	/**
	 * Gets the socket of a bot.
	 * @return the socket used to communicate with the bot
	 */
	public Socket getSocket()
	{
		return socket;
	}

	/**
	 * Sets the socket of a bot and updates its print writer.
	 * @param socket the new socker used to communicate with the bot
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
				LOGGER.error("Cannot create PrintWriter for bot " + pseudo);
			}
		}
		else
		{
			printWriter = null;
		}
	}

	/**
	 * Gets the score of a bot.
	 * @return the game score of the bot
	 */
	public double getScore()
	{
		return score;
	}

	/**
	 * Sets the score of a bot.
	 * @param score the new game score of the bot
	 */
	public void setScore(double score)
	{
		this.score = score;
	}
	
	/**
	 * Send a message to a bot using its bot socket.
	 * @param message the message to send to the bot
	 */
	public void send(String message)
	{
		printWriter.println(message);
	}
	
}
