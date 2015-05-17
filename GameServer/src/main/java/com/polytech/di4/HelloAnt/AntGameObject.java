package com.polytech.di4.HelloAnt;

import org.json.JSONArray;

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
	protected Boolean movable;
	
	/**
	 * Whether the game object can share a cell with another game object.
	 */
	protected Boolean collideable;
	
	/**
	 * The handler used to effectively move the game object on the map.
	 */
	private AntGameMapCallback moveHandler;
	
	/**
	 * Creates a new game object from a column and a row identifier.
	 * @constructor
	 * @param col the column identifier of the game object.
	 * @param row the row identifier of the game object.
	 * @param movable true if the game object can be moved on the map.
	 * @param collideable false if the game object can share a cell with another one.
	 */
	public AntGameObject(int col, int row, Boolean movable, Boolean collideable)
	{
		this.col = col;
		this.row = row;
		this.movable = movable;
		this.collideable = collideable;
	}
	
	/**
	 * Creates a new game object from a cell descriptor.
	 * @constructor
	 * @param cell the cell descriptor that positions the game object on the map.
	 * @param movable true if the game object can be moved on the map.
	 * @param collideable false if the game object can share a cell with another one.
	 */
	public AntGameObject(Cell cell, Boolean movable, Boolean collideable)
	{
		col = cell.getCol();
		row = cell.getRow();
		this.movable = movable;
		this.collideable = collideable;
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
	public Boolean isMovable()
	{
		return movable;
	}
	
	/**
	 * Returns whether the game object is collideable.
	 * @return true if the game object can't share its cell with another game object.
	 */
	public Boolean isCollideable()
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
