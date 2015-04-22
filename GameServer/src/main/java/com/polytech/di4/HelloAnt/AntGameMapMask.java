package com.polytech.di4.HelloAnt;

import java.util.List;
/**
 * An AntGameMask represents a list of cells that defines some actions such as 
 * visibility.
 * @author Benjamin
 *
 */
public class AntGameMapMask
{
	private List<cell> cells;
	/**
	 * Creates a new mask with a list of cells
	 * @param someCells
	 */
	public AntGameMapMask(List<cell> someCells)
	{
		cells = someCells;
	}
	/**
	 * @return The cell list used by a mask
	 */
	public cells getCells()
	{
		return cells;
	}
}
