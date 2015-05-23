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
	 * Creates a new game thread from a game instance.
	 * @constructor
	 * @param game The game to run with the game thread.
	 */
	public GameThread(Game game)
	{
		this.game = game;
	}
	
	/**
	 * Runs the mechanics of a generic game.
	 */
	public void run()
	{
		game.init();
		// Send the "gamestart" message.
		//game.sendGameStartInfo();
		// Wait for load time.
		//Thread.sleep(game.getLoadTimeMs());
		while (game.isFinished() == false)
		{	// Mute bot(s) that has not played.
			//game.muteNonPlayerBots();
			// Send the current game state to bots.
			//game.sendGameState();
			/*while (game.areBotReady() == false || timer < game.getResponseDelayMs())
			{	// Wait for all bots to give actions OR response delay overcame
				Thread.sleep(1);
			}*/
			// Update the game.
			game.update();
		}
		// Compute new scores.
		game.computeBotScores();
		// Send the "gameend" message.
		//game.sendGameEndInfos();
		//game.finalize();
	}
}
