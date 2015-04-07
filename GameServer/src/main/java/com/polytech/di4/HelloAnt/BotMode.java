package com.polytech.di4.HelloAnt;

/**
 * The gaming mode of a bot.
 * It describes how the bot wants to play his game (how fast? against other bots?)
 * @enum
 * @author Nicolas
 */
public enum BotMode
{
	// The default gaming mode.
	// Simply wait for a game to be created by the game manager.
	REGULAR,
	// Training mode means the bot wants to play immediately against a computer.
	// This one is mainly used for debug purposes.
	TRAINING;
}
