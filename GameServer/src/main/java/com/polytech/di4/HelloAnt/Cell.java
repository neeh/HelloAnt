package com.polytech.di4.HelloAnt;

/**
 * This class represents a cell of a map.
 * A map is a table composed of (rows * cols) cells.
 * @author Nicolas
 */
public class Cell
{
	/**
	 * The column identifier of the cell (0-based.)
	 */
	private int col;
	
	/**
	 * The row identifier of the cell (0-based.)
	 */
	private int row;
	
	/**
	 * Creates a new cell of a map.
	 * @constructor
	 * @param col the column identifier of the cell.
	 * @param row the row identifier of the cell.
	 * @return the cell (col, row) identified in a map.
	 */
	public Cell(int col, int row)
	{
		this.col = col;
		this.row = row;
	}
	
	/**
	 * Gets the column identifier of the cell.
	 * @return the column id of the cell (0-based.)
	 */
	public int getCol()
	{
		return col;
	}
	
	/**
	 * Gets the row identifier of the cell.
	 * @return the row id of the cell (0-based.)
	 */
	public int getRow()
	{
		return row;
	}
}
