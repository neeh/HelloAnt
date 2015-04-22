package com.polytech.di4.HelloAnt;

import java.util.List;
/**
 * This class describes the maps of the AntGame. It contains all the objects needed to
 * play a match. For each cell of the map, there is a list of all the objects on the
 * cell.
 * @author Benjamin
 *
 */
public class AntGameMap  
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
	public List<AntGameObject> getGameObjectAt(int row, int col)
	{
		return cells.get(row).get(col);
	}
	/**
	 * Add a new game object on the selected cell
	 * @param obj
	 * @param col
	 * @param row
	 */
	public void addGameObject (AntGameObject obj, int col, int row)
	{
		cells.get(row).get(col).add(obj);
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

}
