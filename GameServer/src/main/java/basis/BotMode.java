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
 * The gaming mode of a bot.
 * It describes how the bot wants to play his game (how fast? against other bots?)
 * @enum
 * @author Nico, JMN
 */
public enum BotMode
{
	// The default gaming mode.
	// Simply wait for a game to be created by the game manager.
	REGULAR,
	// Training mode means the bot wants to play immediately against a computer.
	// This one is mainly used for debug purposes.
	TRAINING;
	
	/**
	 * Gets a bot mode from a string.
	 * @param value the character string of the bot mode.
	 * @return the bot mode associated with this value string.
	 */
	public static BotMode fromString(String value)
	{
		value = value.toLowerCase();
		/*
		if("training".startsWith(value))
			return TRAINING;
		return REGULAR;
		Can give strange results ...
		*/
		if ("training".equalsIgnoreCase(value)) return TRAINING;
		return REGULAR;
	}
}
