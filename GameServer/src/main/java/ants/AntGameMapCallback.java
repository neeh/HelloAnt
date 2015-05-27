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

package ants;

import util.Move;

/**
 * This interface provides a method that can be called by a game object to move itself on
 * the game map.
 * @interface
 * @author Nicolas
 */
public interface AntGameMapCallback
{
	/**
	 * Moves a game object on the game map in a given direction.
	 * @param gob the game object to move.
	 * @param direction where to move the game object.
	 */
	public void moveGameObject(AntGameObject gob, Move dir);
}
