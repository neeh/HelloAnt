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
	 * @return the exception.
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
