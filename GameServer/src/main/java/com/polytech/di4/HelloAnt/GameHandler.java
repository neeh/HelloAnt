package com.polytech.di4.HelloAnt;

/**
 * This interface is used by the game manager to call back the server when new games are
 * created.
 * @interface
 * @author Nicolas
 */
public interface GameHandler
{
	/**
	 * Notifies the server that a game has been created and should be run.
	 * @param newGame the game created to be run.
	 */
	public void handleGameCreated(Game newGame);
}
