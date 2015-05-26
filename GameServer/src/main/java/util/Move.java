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
 * Represents a generic direction in a basic board game.
 * @class
 * @author Benjamin
 */
public enum Move
{
	NORTH,
	SOUTH,
	EAST,
	WEST;
	
	/**
	 * Gets a move direction from a string.
	 * @param direction the string representing a direction.
	 * @return the move direction associated with the string.
	 */
	public static Move fromString(String direction)
	{
		if      ("N".equalsIgnoreCase(direction)) return NORTH;
		else if ("S".equalsIgnoreCase(direction)) return SOUTH;
		else if ("E".equalsIgnoreCase(direction)) return EAST;
		else if ("W".equalsIgnoreCase(direction)) return WEST;
		// Returning null here can cause problems.
		// EAST could be the default move.
		return EAST;
	}
	
	/**
	 * Gets a string from a move direction.
	 * @param direction the input move direction.
	 * @return the string representing this direction.
	 */
	public static String toString(Move direction)
	{
		switch (direction)
		{
		case NORTH: return "N";
		case SOUTH: return "S";
		case EAST: return "E";
		case WEST: return "W";
		default: return "-";
		}
	}
}
