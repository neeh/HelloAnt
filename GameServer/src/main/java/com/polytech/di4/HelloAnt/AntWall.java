package com.polytech.di4.HelloAnt;

import org.json.JSONArray;
import org.json.JSONException;

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
