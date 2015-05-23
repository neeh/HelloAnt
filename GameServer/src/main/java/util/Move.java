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
		switch (direction)
		{
		case "N": return NORTH;
		case "S": return SOUTH;
		case "E": return EAST;
		case "W": return WEST;
		}
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
