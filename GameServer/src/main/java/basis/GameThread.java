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

/**
 * This class is responsible for running a the mechanics of a game implemented in the game
 * server.
 * @class
 * @author Nicolas
 */
public class GameThread extends Thread
{
	/**
	 * The game which is runned by the game thread.
	 */
	private Game game;
	
	/**
	 * The handler used to notify the server when the thread has terminated its work.
	 */
	private GameHandler gameHandler;
	
	/**
	 * Creates a new game thread from a game instance.
	 * @constructor
	 * @param game the game to run with the game thread.
	 * @param gameHandler the handler used to notify the server when the work is done.
	 */
	public GameThread(Game game, GameHandler gameHandler)
	{
		this.game = game;
		this.gameHandler = gameHandler;
	}
	
	/**
	 * Runs the mechanic of a generic game.
	 */
	public void run()
	{
		game.init();
		// Send the "gamestart" message.
		game.sendGameStart();
		// Wait for load time.
		try
		{
			Thread.sleep(game.getLoadTimeMs());
		}
		catch (InterruptedException e) {}
		while (game.isFinished() == false)
		{
			// Send the current game state to bots.
			game.sendGameState();
			long ms = System.currentTimeMillis();
			while (game.isReady() == false &&
					System.currentTimeMillis() - ms < game.getResponseTimeMs())
			{	// Wait for all bots to give actions OR response delay overcame
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e){}
			}
			// Mute bot(s) that has not played.
			game.muteNonPlayerBots();
			// Update the game.
			game.update();
		}
		// Compute new scores.
		game.computeBotScores();
		// Send the "gameend" message.
		game.sendGameEnd();
		//game.finalize();
		gameHandler.removeGameThread(this);
	}
	
	/**
	 * Gets the game runned by this game thread.
	 * @return the game the thread is running.
	 */
	public Game getGame()
	{
		return game;
	}
}
