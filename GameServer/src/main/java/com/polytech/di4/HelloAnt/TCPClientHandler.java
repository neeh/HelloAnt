package com.polytech.di4.HelloAnt;

public interface TCPClientHandler
{
	/**
	 * Notifies the server that a client just connected on the server.
	 * @param newClient the client to add on the game server.
	 */
	public void handleClientConnected(TCPClientCommunicator newClient);
	
	/**
	 * Notifies the server that a client just disconnected from the server.
	 * @param oldClient the client to remove from the game server.
	 */
	public void handleClientDisconnected(TCPClientCommunicator oldClient);
	
	/**
	 * Notifies the game server that a client just logged as a bot.
	 * @param newBot the bot which logged in.
	 */
	public void handleBotLogin(Bot newBot);
	
	/**
	 * Notifies the game server a client just logged out its bot.
	 * @param oldBot the bot which logged out.
	 */
	public void handleBotLogout(Bot oldBot);
}
