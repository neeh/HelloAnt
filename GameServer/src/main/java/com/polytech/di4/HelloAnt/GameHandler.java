package com.polytech.di4.HelloAnt;

public interface GameHandler
{
	/**
	 * Notifies that a game has been created and should be run
	 * @param newGame the game created to be run
	 */
	public void handleGameCreated(Game newGame);
}
