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
 * This interface is used to communicate game-related events to the server.
 * The game manager use this interface to add games in the server, the server then runs
 * this game with a game thread and finally the game thread use it to remove itself from
 * the server, when it has finished its work.
 * @interface
 * @author Nicolas
 */
public interface GameHandler
{
	/**
	 * Notifies the server that a game has been created and should be run.
	 * @param newGame the game created to be run.
	 */
	public void addGame(Game newGame);
	
	/**
	 * Notifies the server that a game thread has terminated its execution and should be
	 * removed from the game threads list.
	 * @param gameThread the game thread to remove.
	 */
	public void removeGameThread(GameThread gameThread);
}
