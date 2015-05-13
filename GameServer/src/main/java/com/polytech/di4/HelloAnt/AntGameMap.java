package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.List;
/**
 * This class describes the maps of the AntGame. It contains all the objects needed to
 * play a match. For each cell of the map, there is a list of all the objects on the
 * cell.
 * @author Benjamin
 *
 */
public class AntGameMap  implements AntGameMapCallback
{
	private int cols;
	private int rows;
	private List<List<List<AntGameObject>>> cells;
	/**
	 * Creates the AntGameMap 
	 * @param columns
	 * @param rows
	 */
	public AntGameMap(int columns, int rows)
	{
		this.cols = columns;
		this.rows = rows;
		this.cells = new ArrayList<List<List<AntGameObject>>>();
		for (int i = 0; i < columns;++i)
		{
			cells.add(new ArrayList<List<AntGameObject>>());
			for (int  j = 0; j < columns;++j)
			{
				cells.get(i).add(new ArrayList<AntGameObject>());
			}
		}
		
	}
	/**
	 * This function creates a playable map of the AntGame with walls, AntHills and 
	 * and foodspawns.
	 */
	public void init()
	{
		
	}
	/**
	 * 
	 * @param row
	 * @param col
	 * @return all the game objects that are on the selected cell
	 */
	public List<AntGameObject> getGameObjectsAt(int row, int col)
	{
		
		return cells.get((row % rows + rows) % rows ).get((col % cols + cols) % cols);
	}
	/**
	 * Add a new game object on the cell defined by the object's attributes (col and row)
	 * @param obj
	 * @param col
	 * @param row
	 */
	public void addGameObject (AntGameObject obj)
	{
		if (obj.getRow() < rows && obj.getColumn() < cols)
			cells.get(obj.getRow()).get(obj.getColumn()).add(obj);
	}
	/**
	 * This function finds all the objects that are presents in the mask.
	 * The center  of the mask is given with the row and the column.
	 * @param row
	 * @param col
	 * @param theMask 
	 * @return The list of all the objects in the mask.
	 */
	public List<AntGameObject> applyMask(int row, int col,AntGameMapMask theMask)
	{
		List<AntGameObject> list = new ArrayList<AntGameObject>();
		int vRow, vCol;
		for (int i = 0; i < theMask.getCells().size();++i)
		{
			vRow = ((theMask.getCells().get(i).getRow() + row) % rows + rows) % rows;
			vCol = ((theMask.getCells().get(i).getCol() + col) % cols + cols) % cols;
			list.addAll(cells.get(vRow).get(vCol));
		}
		return list;
	}
	/**
	 * @return the number of rows of the map
	 */
	public int getRows()
	{
		return rows;
	}
	/**
	 * @return the number of columns of the map
	 */
	public int getCols()
	{
		return cols;
	}
	@Override
	public void moveGameObject(AntGameObject object, Move dir)
	{
		if (dir ==  Move.NORTH)
		{
			object.setRow(((object.getRow() - 1) % rows + rows) % rows);
		}
		if (dir ==  Move.SOUTH)
		{
			object.setRow((object.getRow() + 1) % rows);
		}
		if (dir ==  Move.EAST)
		{
			object.setColumn((object.getColumn() + 1) % cols);
		}
		if (dir ==  Move.WEST)
		{
			object.setColumn(((object.getColumn() - 1) % cols + cols) % cols);
		}
	}

}
