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
 * @constructor
 * @author Benjamin
 */
public class AntWall extends AntGameObject
{
	/**
	 * Creates a wall from a column and a row identifier.
	 * @constructor
	 * @param col the column identifier of the wall.
	 * @param row the row identifier of the wall.
	 */
	public AntWall(int col, int row)
	{
		super(col, row, false, true);
	}
	
	/**
	 * Creates a wall from a cell descriptor.
	 * @constructor
	 * @param cell the cell description that positions the wall on the map.
	 */
	public AntWall(Cell cell)
	{
		super(cell, false, true);
	}
	
	/**
	 * Gets a JSON representation of a wall.
	 * @see Documentation/protocol/gamestate.html
	 * @return [ "W", col, row ]
	 */
	public JSONArray toJSONArray()
	{
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, "W");
			array.put(1, col);
			array.put(2, row);
		}
		catch (JSONException e) {}
		return array;
	}
}
