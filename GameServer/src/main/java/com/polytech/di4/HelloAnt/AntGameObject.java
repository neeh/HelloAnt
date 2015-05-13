package com.polytech.di4.HelloAnt;
/**
 * This abstract class allows to instantiate any object needed by the game rules.
 * Every AntGameObject has a position (row,column) and a boolean which permits to move the object or not.
 * @class
 * @author Benjamin
 *
 */
public class AntGameObject
{
	protected int row;
	protected int col;
	protected static boolean movable;
	protected static boolean colideable;
	private AntGameMapCallback moveHandler;
	/**
	 * Constructor which instantiate  the GameObject with it's situation on the map
	 * @param column
	 * @param row
	 */
	public AntGameObject(int column, int row)
	{
		this.row = row;
		this.col = column;
	}
	/**
	 * 
	 * @return true is the Game Object can be on the same cell with another colideable object
	 */
	public boolean isColideable()
	{
		return colideable;
	}
	/**
	 * @return true if it is possible to move the object
	 */
	public boolean isMovable()
	{
		return movable;
	}
	/**
	 * @return the row of the current position of the object on the map
	 */
	public int getRow()
	{
		return row;
	}
	/**
	 * Change the row of the object
	 * @param row
	 */
	public void setRow(int row)
	{
		this.row = row;
	}
	/**
	 * @return the column of the current position of the object on the map
	 */
	public int getColumn()
	{
		return col;
	}
	/**
	 * Change the column of an object
	 * @param column
	 */
	public void setColumn(int column)
	{
		this.col = column;
	}
	/**
	 * Move the object to the given direction
	 * @param direction
	 */
	public void move(Move direction)
	{
		if (movable)
		{
			moveHandler.moveGameObject(this, direction);
		}
		
	}
	}
