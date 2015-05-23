package ants;

import java.util.ArrayList;

import util.Cell;

/**
 * This class represents a mask or a pattern that can be applied an a board map to select
 * specific cells of the game map. It's a list of cells that shapes the pattern.
 * @class
 * @author Benjamin
 */
public class AntGameMapMask
{
	/**
	 * The list of cells included in the mask.
	 */
	private ArrayList<Cell> cells;
	
	/**
	 * The squared radius of the circular map mask.
	 */
	private float radius2;
	
	/**
	 * Creates a circular mask from a radius value.
	 * @param radius2 the squared radius of the circle.
	 */
	public AntGameMapMask(float radius2)
	{
		this.radius2 = radius2;
		cells = new ArrayList<Cell>();
		int bound = (int) Math.floor(Math.sqrt(radius2));
		for (int i = -bound; i <= bound; i++)
		{
			for (int j = -bound; j <= bound; j++)
			{
				if (i * i + j * j <= radius2)
				{
					cells.add(new Cell(i, j));
				}
			}
		}
	}
	
	/**
	 * Gets the cells of the mask.
	 * @return the list of cells used by the mask.
	 */
	public ArrayList<Cell> getCells()
	{
		return cells;
	}
	
	/**
	 * Gets the squared radius of a circular map mask.
	 * @return the squared radius of the mask.
	 */
	public float getRadius2()
	{
		return radius2;
	}
}
