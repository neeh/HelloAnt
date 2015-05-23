package basis;

/**
 * Represents an error that occurred during the login of a bot on the database side.
 * Provides an error code which determines why the exception was raised.
 * @class
 * @author JMN
 */
public class BotLoginException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * The error code associated with this exception.
	 */
	private int errorCode;
	
	/**
	 * Creates a new bot login exception.
	 * @constructor
	 * @param errorCode the error code associated with this exception.
	 */
	public BotLoginException(int errorCode)
	{
		this.errorCode = errorCode;
	}
	
	/**
	 * Gets the error code associated with this exception.
	 * 101: token does not exist.
	 * 102: the bot with this token is already logged in.
	 * @see Documentation/protocol/login.html
	 * @return the error code of the bot login exception.
	 */
	public int getErrorCode()
	{
		return errorCode;
	}
}
