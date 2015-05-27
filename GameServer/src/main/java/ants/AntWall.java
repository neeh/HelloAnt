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

import org.json.JSONArray;
import org.json.JSONException;

import util.Cell;

/**
 * A wall is an unmovable, unpierceable game object placed on the map.
 * @class
 * @author Benjamin
 */
public class AntWall extends AntGameObject
{
	/**
	 * Creates a wall from a column and a row identifier.
	 * @constructor
	 * @param moveHandler the handler used to move the wall.
	 * @param col the initial column identifier of the wall.
	 * @param row the initial row identifier of the wall.
	 */
	public AntWall(AntGameMapCallback moveHandler, int col, int row)
	{
		super(moveHandler, col, row, false, true);
	}
	
	/**
	 * Creates a wall from a cell descriptor.
	 * @constructor
	 * @param moveHandler the handler used to move the wall.
	 * @param cell the cell description that positions the wall on the map.
	 */
	public AntWall(AntGameMapCallback moveHandler, Cell cell)
	{
		super(moveHandler, cell, false, true);
	}
	
	/**
	 * Gets a JSON representation of a wall.
	 * @see Documentation/protocol/gamestate.html
	 * @return [ "W", row, col ]
	 */
	public JSONArray toJSONArray()
	{
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, "W");
			array.put(1, row);
			array.put(2, col);
		}
		catch (JSONException e) {}
		return array;
	}
}
