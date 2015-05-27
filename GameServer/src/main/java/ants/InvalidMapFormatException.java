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

/**
 * This class represents the exception for an invalid map format.
 * The exception is raised when a map file is being parsed and the parsing failed.
 * @see Documentation/specifications/mapformat.html
 * @class
 * @author Nicolas
 */
public class InvalidMapFormatException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * The message describing why the exception was raised.
	 */
	private String message;
	
	/**
	 * Creates a new exception for an invalid map format.
	 * @constructor
	 * @see Documentation/specifications/mapformat.html
	 * @param message the message describing the exception.
	 */
	public InvalidMapFormatException(String message)
	{
		this.message = message;
	}
	
	/**
	 * Gets the message of the exception explaining why the map format is not valid.
	 * @return the message describing the exception.
	 */
	public String getMessage()
	{
		return message;
	}
}
