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
