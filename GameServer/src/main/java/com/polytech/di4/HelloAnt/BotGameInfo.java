package com.polytech.di4.HelloAnt;

/**
 * This class holds informations about the game a bot is currently playing.
 * It's mainly used to manage the mechanics of the reception of game actions but it also
 * contains the game score of a bot, if used.
 * @class
 * @author Nicolas
 */
public class BotGameInfo
{
	/**
	 * Whether the bot has played for the current round.
	 * If the bot sends a "gameactions" whereas this boolean is set to true, then the bot
	 * will receive a "Already played for this round" error message.
	 * When all the bots have played for the current round, the game the continue to the
	 * next round. It means it does not have to wait for the response delay to finish.
	 * @see Documentation/protocol/gameactions.html
	 */
	private Boolean played;
	
	/**
	 * The timestamp of the sending of the current game state. (in milliseconds)
	 * Should be tested when the server receives a "gameactions" message from this bot.
	 * If the delay is exceeded, then the server sends a "gamemute" message to the bot so
	 * the bot cannot play until the end of the game.
	 */
	private long gamestateTimestampMs;
	
	/**
	 * Whether the bot was muted in its game.
	 */
	private Boolean muted;
	
	/**
	 * The current game score of the bot.
	 */
	private int gameScore;
	
	/**
	 * Creates a new game state holder for a bot.
	 * @constructor
	 * @param gameScore the initial game score of the bot.
	 */
	public BotGameInfo(int gameScore) {
		played = true;
		gamestateTimestampMs = 0;
		muted = false;
		this.gameScore = gameScore;
	}
	
	/**
	 * Gets whether the bot has played for the current round.
	 * @return true if the bot has already played for this round, false otherwise.
	 */
	public Boolean hasPlayed() {
		return played;
	}
	
	/**
	 * Gets the timestamp of the last sending of the game state to this bot.
	 * @return the last timestamp of the "gamestate" message sent by the server.
	 */
	public long getGamestateTimestampMs() {
		return gamestateTimestampMs;
	}
	
	/**
	 * Gets whether the bot is muted for the game.
	 * @return true if the bot is muted, false otherwise.
	 */
	public Boolean isMuted() {
		return muted;
	}
	
	/**
	 * Gets the current game score of the bot.
	 * @return the current game score of the bot.
	 */
	public int getGameScore() {
		return gameScore;
	}
	
	/**
	 * Sets whether the bot has played for the current round.
	 * @param played true if the bot has played for this round, false otherwise.
	 */
	public void setPlayed(Boolean played) {
		this.played = played;
	}
	
	/**
	 * Sets the timestamp of the last sending of the game state to this bot.
	 * @param gamestateTimestampMS the last timestamp of the "gamestate" message sent by
	 *        the server.
	 */
	public void setGamestateTimestampMs(long gamestateTimestampMs) {
		this.gamestateTimestampMs = gamestateTimestampMs;
	}
	
	/**
	 * Mutes or unmutes the bot for the game.
	 * @param muted true to mute the bot, false otherwise.
	 */
	public void setMuted(Boolean muted) {
		this.muted = muted;
	}
	
	/**
	 * Sets the game score of the bot.
	 * @param gameScore the new game score of the bot.
	 */
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
