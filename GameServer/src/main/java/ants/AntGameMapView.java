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

import java.util.List;

/**
 * This interface provides a method that can be called to view the game objects of the
 * map. The ant fake communicator uses it to see where it can move its bot.
 * @interface
 * @author Nicolas
 */
public interface AntGameMapView
{
	/**
	 * Gets the list of game objects present in a specific cell of the game map.
	 * The method takes the toroidal shape of the map in account.
	 * @param col the column identifier of the cell.
	 * @param row the row identifier of the cell.
	 * @return the content of the cell.
	 */
	public List<AntGameObject> getGameObjectsAt(int col, int row);
}
