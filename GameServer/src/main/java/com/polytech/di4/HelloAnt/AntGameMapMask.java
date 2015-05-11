package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.List;
/**
 * An AntGameMask represents a list of cells that defines some actions such as 
 * visibility.
 * @author Benjamin
 *
 */
public class AntGameMapMask
{
	/**
	 * The list of accessible cells within the mask.
	 */
	private List<Cell> cells;
	/**
	 * Creates a new mask with the cells in range 
	 * @param radius
	 */
	public AntGameMapMask(float radius)
	{
		int rad = (int)Math.floor(radius);
		cells=new ArrayList<Cell>();
		for (int i=-rad;i<=rad;++i)
		{
			for (int j=-rad;j<=rad;++j)
			{
				if(Math.sqrt(i*i+j*j)<=rad)
				{
					cells.add(new Cell(i,j));
				}
			}
		}
	}
	/**
	 * @return The cell list used by a mask
	 */
	public List<Cell> getCells()
	{
		return cells;
	}
}
