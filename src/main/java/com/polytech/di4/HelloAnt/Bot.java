package com.polytech.di4.HelloAnt;

import java.net.Socket;

public class Bot {

	private String pseudo;
	private Socket socket;
	private double score;
	
	/**
	 * Creates a new Bot.
	 * @constructor
	 * @param pseudo the pseudonym of the bot
	 * @param socket the socket used to communicate with the bot
	 * @param score  the current game score of the bot
	 */
	public Bot(String pseudo, Socket socket, double score)
	{
		super();
		this.pseudo = pseudo;
		this.socket = socket;
		this.score = score;
	}
	
	
	
}
