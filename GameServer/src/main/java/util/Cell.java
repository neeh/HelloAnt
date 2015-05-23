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

package util;

/**
 * This class represents a cell of a map.
 * A map is a table composed of (rows * cols) cells.
 * @class
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
