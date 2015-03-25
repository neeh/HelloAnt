package com.polytech.di4.HelloAnt;

import java.net.Socket;

public class Bot {

	private String pseudo;
	private Socket socket;
	private double score;
	
	public Bot(String pseudo, Socket socket, double score)
	{
		super();
		this.pseudo = pseudo;
		this.socket = socket;
		this.score = score;
	}
	
	
	
}
