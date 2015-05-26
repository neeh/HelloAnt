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

import util.Cell;
import util.Move;

/**
 * Represents a game object specific to the game of ants. A game object is an entity used
 * to implement the way the game works. A game object is positioned on a game map.
 * Some game objects can be moved.
 * @class
 * @author Benjamin
 */
public class AntGameObject
{
	/**
	 * The column identifier of the game object.
	 */
	protected int col;
	
	/**
	 * The row identifier of the game object.
	 */
	protected int row;
	
	/**
	 * Whether the game object can be moved on the map.
	 */
	protected boolean movable;
	
	/**
	 * Whether the game object can share a cell with another game object.
	 */
	protected boolean collideable;
	
	/**
	 * The handler used to effectively move the game object on the map.
	 */
	private AntGameMapCallback moveHandler;
	
	/**
	 * Creates a new game object from a column and a row identifier.
	 * @constructor
	 * @param moveHandler the handler used to effectively move the game object on the map.
	 * @param col the column identifier of the game object.
	 * @param row the row identifier of the game object.
	 * @param movable true if the game object can be moved on the map.
	 * @param collideable false if the game object can share a cell with another one.
	 */
	public AntGameObject(AntGameMapCallback moveHandler, int col, int row, boolean movable, boolean collideable)
	{
		this.moveHandler = moveHandler;
		this.col = col;
		this.row = row;
		this.movable = movable;
		this.collideable = collideable;
	}
	
	/**
	 * Creates a new game object from a cell descriptor.
	 * @constructor
	 * @param moveHandler the handler used to effectively move the game object on the map.
	 * @param cell the cell descriptor that positions the game object on the map.
	 * @param movable true if the game object can be moved on the map.
	 * @param collideable false if the game object can share a cell with another one.
	 */
	public AntGameObject(AntGameMapCallback moveHandler, Cell cell, boolean movable, boolean collideable)
	{
		this(moveHandler, cell.getCol(), cell.getRow(), movable, collideable);
	}
	
	/**
	 * Moves the game object on the map to the given direction.
	 * @param direction where to move the game object.
	 */
	public void move(Move direction)
	{
		if (movable) moveHandler.moveGameObject(this, direction);
	}
	
	/**
	 * Gets the column identifier of the game object.
	 * @return the column identifier of the game object.
	 */
	public int getCol()
	{
		return col;
	}
	
	/**
	 * Gets the row identifier of the game object.
	 * @return the row identifier of the game object.
	 */
	public int getRow()
	{
		return row;
	}
	
	/**
	 * Returns whether a game object can be moved on the map.
	 * @return true if it is possible to move the game object, false otherwise.
	 */
	public boolean isMovable()
	{
		return movable;
	}
	
	/**
	 * Returns whether the game object is collideable.
	 * @return true if the game object can't share its cell with another game object.
	 */
	public boolean isCollideable()
	{
		return collideable;
	}
	
	/**
	 * Gets a JSON representation of the game object.
	 * @see Documentation/protocol/gamestate.html
	 * @return the game object as a JSON array.
	 */
	public JSONArray toJSONArray()
	{
		return null;
	}
	
	/**
	 * Sets the column identifier of the game object.
	 * @param col the column identifier of the game object.
	 */
	public void setCol(int col)
	{
		this.col = col;
	}
	
	/**
	 * Sets the row identifier of the game object.
	 * @param row the row identifier of the game object.
	 */
	public void setRow(int row)
	{
		this.row = row;
	}
}
