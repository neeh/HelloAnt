package com.polytech.di4.HelloAnt;

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
