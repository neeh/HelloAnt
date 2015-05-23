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
