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
 * This class holds informations about the game a bot is currently playing.
 * It's mainly used to manage the mechanics of the reception of game actions but it also
 * contains the game score of a bot, if used.
 * Overloading this class is strongly recommended if you want to add bot infos that are
 * specific to the game you implement.
 * @class
 * @author Nicolas
 */
public class BotGameInfo
{
	/**
	 * The identifier specific to the bot for this game.
	 * Can be used to reference the bot in replay data or other stuff.
	 */
	private int botId;
	
	/**
	 * Whether the bot has played for the current round.
	 * If the bot sends a "gameactions" whereas this boolean is set to true, then the bot
	 * will receive a "Already played for this round" error message.
	 * When all the bots have played for the current round, the game the continue to the
	 * next round. It means it does not have to wait for the response delay to finish.
	 * @see Documentation/protocol/gameactions.html
	 */
	private boolean played;
	
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
	private boolean muted;
	
	/**
	 * The current game score of the bot.
	 */
	private int gameScore;
	
	/**
	 * Creates a new bot game info structure.
	 * @constructor
	 * @param botId the identifier of the bot for this game.
	 */
	public BotGameInfo(int botId)
	{
		this.botId = botId;
	}
	
	/**
	 * Initializes a bot game info structure.
	 */
	public void init()
	{
		played = true;
		gamestateTimestampMs = 0;
		muted = false;
		gameScore = 0;
	}
	
	/**
	 * Gets the identifier specific to the bot for this game.
	 * @return the identifier of the bot for this game.
	 */
	public int getId()
	{
		return botId;
	}
	
	/**
	 * Gets whether the bot has played for the current round.
	 * @return true if the bot has already played for this round, false otherwise.
	 */
	public boolean hasPlayed()
	{
		return played;
	}
	
	/**
	 * Gets the timestamp of the last sending of the game state to this bot.
	 * @return the last timestamp of the "gamestate" message sent by the server.
	 */
	public long getGamestateTimestampMs()
	{
		return gamestateTimestampMs;
	}
	
	/**
	 * Gets whether the bot is muted for the game.
	 * @return true if the bot is muted, false otherwise.
	 */
	public boolean isMuted()
	{
		return muted;
	}
	
	/**
	 * Gets the current game score of the bot.
	 * @return the current game score of the bot.
	 */
	public int getGameScore()
	{
		return gameScore;
	}
	
	/**
	 * Sets whether the bot has played for the current round.
	 * @param played true if the bot has played for this round, false otherwise.
	 */
	public void setPlayed(boolean played)
	{
		this.played = played;
	}
	
	/**
	 * Sets the timestamp of the last sending of the game state to this bot.
	 * @param gamestateTimestampMS the last timestamp of the "gamestate" message sent by
	 *        the server.
	 */
	public void setGamestateTimestampMs(long gamestateTimestampMs)
	{
		this.gamestateTimestampMs = gamestateTimestampMs;
	}
	
	/**
	 * Mutes or unmutes the bot for the game.
	 * @param muted true to mute the bot, false otherwise.
	 */
	public void setMuted(boolean muted)
	{
		this.muted = muted;
	}
	
	/**
	 * Sets the game score of the bot.
	 * @param gameScore the new game score of the bot.
	 */
	public void setGameScore(int gameScore)
	{
		this.gameScore = gameScore;
	}
	
	/**
	 * Adds some points to the game score of the bot.
	 * @param points the amount of score to give to the bot.
	 */
	public void addGameScore(int points)
	{
		gameScore += points;
	}
}
