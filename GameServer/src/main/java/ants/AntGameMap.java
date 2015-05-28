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

import java.util.ArrayList;
import java.util.Iterator;

import util.Cell;
import util.Move;

/**
 * This class represents the dynamic game map of an ant game. It holds all the game
 * objects and their status at the current round of a game.
 * @class
 * @author Benjamin
 */
public class AntGameMap implements AntGameMapCallback, AntGameMapView
{
	/**
	 * The number of columns of the game map.
	 */
	private int cols;
	
	/**
	 * The number of rows of the game map.
	 */
	private int rows;
	
	/**
	 * The array of cells of the map. This is a 3-dimensional array.
	 * The first dimension references map rows, the second one map columns and the last
	 * one is used to store several game objects in the same cell.
	 */
	private ArrayList<ArrayList<ArrayList<AntGameObject>>> cells;
	
	/**
	 * Creates a new game map from a column count and a row count.
	 * @constructor
	 * @param cols the number of columns of the game map.
	 * @param rows the number of rows of the game map.
	 */
	public AntGameMap(int cols, int rows)
	{
		this.cols = cols;
		this.rows = rows;
		cells = new ArrayList<ArrayList<ArrayList<AntGameObject>>>(rows);
		for (int i = 0; i < rows; i++)
		{	// Create map row.
			ArrayList<ArrayList<AntGameObject>> row =
					new ArrayList<ArrayList<AntGameObject>>(cols);
			cells.add(row);
			for (int j = 0; j < cols; j++)
			{	// Create map column.
				row.add(new ArrayList<AntGameObject>(1));
			}
		}
		
	}
	
	/**
	 * Clears the whole game map by dropping the content of the cells.
	 */
	public void clear()
	{
		Iterator<ArrayList<ArrayList<AntGameObject>>> rowIt = cells.iterator();
		while (rowIt.hasNext())
		{
			ArrayList<ArrayList<AntGameObject>> row = rowIt.next();
			Iterator<ArrayList<AntGameObject>> colIt = row.iterator();
			while (colIt.hasNext())
			{
				colIt.next().clear();
			}
		}
	}
	
	/**
	 * Adds a game object on the game map.
	 * @param gob the game object to add.
	 */
	public void addGameObject(AntGameObject gob)
	{
		cells.get(gob.getRow()).get(gob.getCol()).add(gob);
	}
	
	/**
	 * Removes a game object from the game map.
	 * @param gob the game object to remove.
	 */
	public void removeGameObject(AntGameObject gob)
	{
		cells.get(gob.getRow()).get(gob.getCol()).remove(gob);
	}
	
	/**
	 * Moves a game object on the game map in a given direction.
	 * @param gob the game object to move.
	 * @param direction where to move the game object.
	 */
	@Override
	public void moveGameObject(AntGameObject gob, Move direction)
	{
		// Remove the game object from its cell.
		removeGameObject(gob);
		// Update the cell identifier.
		switch (direction)
		{
		case NORTH:
			gob.setRow(((gob.getRow() - 1) % rows + rows) % rows);
			break;
		case SOUTH:
			gob.setRow((gob.getRow() + 1) % rows);
			break;
		case EAST:
			gob.setCol((gob.getCol() + 1) % cols);
			break;
		case WEST:
			gob.setCol(((gob.getCol() - 1) % cols + cols) % cols);
			break;
		}
		// Add the game object in its new cell.
		addGameObject(gob);
	}
	
	/**
	 * This method finds all the objects that are presents in the mask.
	 * The center of the mask is given by the column and row identifiers.
	 * The method takes the toroidal shape of the map in account.
	 * @param col the column identifier of the position.
	 * @param row the row identifier of the position.
	 * @param mask the mask to apply at this position.
	 * @return the list of game objects in the mask.
	 */
	public ArrayList<AntGameObject> applyMask(int col, int row, AntGameMapMask mask)
	{	// -------------------------------------------------------------------------------
		// CAUTION: The mask has to be smaller than the game map, else, due to the
		// toroidal nature of the map, some objects will be added twice in the array.
		// You can fix this by using a Set/HashSet instead of a simple ArrayList.
		// -------------------------------------------------------------------------------
		ArrayList<AntGameObject> gobs = new ArrayList<AntGameObject>();
		ArrayList<Cell> maskCells = mask.getCells();
		int vRow, vCol;
		for (int i = 0; i < maskCells.size(); i++)
		{
			vRow = ((maskCells.get(i).getRow() + row) % rows + rows) % rows;
			vCol = ((maskCells.get(i).getCol() + col) % cols + cols) % cols;
			gobs.addAll(cells.get(vRow).get(vCol));
		}
		return gobs;
	}
	
	/**
	 * Gets the number of columns of the game map.
	 * @return the number of columns of the map.
	 */
	public int getCols()
	{
		return cols;
	}
	
	/**
	 * Gets the number of rows of the game map.
	 * @return the number of rows of the map.
	 */
	public int getRows()
	{
		return rows;
	}
	
	/**
	 * Gets the list of game objects present in a specific cell of the game map.
	 * The method takes the toroidal shape of the map in account.
	 * @param col the column identifier of the cell.
	 * @param row the row identifier of the cell.
	 * @return the content of the cell.
	 */
	public ArrayList<AntGameObject> getGameObjectsAt(int col, int row)
	{
		return cells.get((row % rows + rows) % rows).get((col % cols + cols) % cols);
	}
	
	/**
	 * Gets the list of game objects that are in the same cell than a given game object.
	 * The method takes the toroidal shape of the map in account.
	 * @param gob the input game object.
	 * @return the content of the cell.
	 */
	public ArrayList<AntGameObject> getGameObjectsAt(AntGameObject gob)
	{
		return getGameObjectsAt(gob.getCol(), gob.getRow());
	}
	
	/**
	 * Gets the alive ant in a given cell of the game map.
	 * @param col the column identifier of the cell.
	 * @param row the row identifier of the cell.
	 * @return the alive ant found in this cell, null if no ant found.
	 */
	public Ant getAntAt(int col, int row)
	{
		AntGameObject gob = null;
		// Get all the game objects in the cell.
		ArrayList<AntGameObject> gobs = getGameObjectsAt(col, row);
		Iterator<AntGameObject> gobIt = gobs.iterator();
		while (gobIt.hasNext())
		{	// Search an alive ant in the list.
			gob = gobIt.next();
			if (gob instanceof Ant && ((Ant) gob).isDead() == false) return (Ant) gob;
		}
		return null;
	}
	
	/**
	 * Prints the game map on the console.
	 */
	public void _DEBUG_print_map()
	{
		Iterator<ArrayList<ArrayList<AntGameObject>>> rowIt = cells.iterator();
		while (rowIt.hasNext())
		{
			String rowString = "";
			Iterator<ArrayList<AntGameObject>> colIt = rowIt.next().iterator();
			while (colIt.hasNext())
			{
				Iterator<AntGameObject> gobIt = colIt.next().iterator();
				String c = ".";
				while (gobIt.hasNext())
				{
					AntGameObject gob = gobIt.next();
					if (gob instanceof AntWall) { c = "#"; break; }
					else if (gob instanceof Ant) { c = "a"; break; }
					else if (gob instanceof AntHill) { c = "@"; break; }
					else if (gob instanceof AntFoodSpawn) { c = "*"; break; }
				}
				rowString += c;
			}
			System.out.println(rowString);
		}
	}
}
