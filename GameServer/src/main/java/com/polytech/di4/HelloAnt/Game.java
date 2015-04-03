package com.polytech.di4.HelloAnt;

import java.util.ArrayList;

import org.json.JSONObject;

/**
 * Game is the class that should be overloaded to create a new game type.
 * For instance, if you want your bots to be able to play a chess game, then create a 
 * ChessGame class which implements the Game class and overloads its methods according
 * to the game specifications.
 * @class
 * @author Nicolas
 */
public abstract class Game
{
	/**
	 * Describes how the game should be played.
	 * When true, only one bot is allowed to play during a game round.
	 * That's the way chess game is played, for example.
	 * When false, all the bot have to send their actions during a game round.
	 * @note Some weird games may be very tricky to design. We recommend you put this
	 * boolean to false and then manually manage the 'curBot' identifier and the
	 * 'curRound' counter in the 'update' method
	 */
	private Boolean oneBotPerTurn;
	
	/**
	 * The list of bots in the game.
	 * If a bot was kicked during the match, it remains in this list but no longer
	 * receives server messages for this game until the match ends.
	 */
	private ArrayList<Bot> bots;
	
	/**
	 * The list of game score for each bot.
	 * Game scores are updated after a round.
	 */
	private ArrayList<Integer> gameScores;
	
	/**
	 * The list of bot enabled.
	 * When a bot is disabled, it no longer receives game state informations and should
	 * wait for the end of the game to be able to play anew.
	 * If you need to stop the reception of actions of a specific bot, switch its
	 * boolean off in this list.
	 */
	private ArrayList<Boolean> BotEnabled;
	
	/**
	 * The identifier of the bot (in the 'bots' list) which is currently playing.
	 * Only relevant when 'oneBotPerTurn' is true.
	 */
	private int curBot;
	
	/**
	 * The current round identifier of the game.
	 * @note The counter should be manually incremented in the update method
	 */
	private int curRound;
	
	/**
	 * The maximum round count of the game.
	 * Can be set to -1 for unlimited round count.
	 */
	private int maxRound;
	
	/**
	 * Gets the current round identifier of the game.
	 * @return The current round identifier
	 */
	public int getCurRound()
	{
		return curRound;
	}
	
	/**
	 * Gets the maximum round count of the game.
	 * @return The max round count
	 */
	public int getMaxRound()
	{
		return maxRound;
	}
	
	/**
	 * Initialize the game state.
	 */
	public abstract void init();
	
	/**
	 * Returns whether the current game state validates the game ending conditions.
	 * @return true is the game is finished, false otherwise
	 */
	public abstract Boolean isFinished();
	
	/**
	 * Updates the score of each bot based on its final game score.
	 * @see ELO rating system
	 */
	public abstract void computeBotScores();
	
	/**
	 * Updates the game state using bot actions.
	 */
	public abstract void update(/*ArrayList<Action> actions*/);
	
	/**
	 * Generates the content of a 'gameround' message for a specific bot.
	 * @param bot The bot that will receive the message
	 * @return The content of the 'gameround' message
	 * @see Documentation/gameround.html
	 */
	public abstract JSONObject genGameRoundMessageContent(Bot bot);
	
	/**
	 * Generates the content of a 'gamestart' message for a specific bot.
	 * @param bot The bot that will receive the message
	 * @return The content of the 'gamestart' message
	 * @see Documentation/gamestart.html
	 */
	public abstract JSONObject genGameStartMessageContent(Bot bot);
	
	/**
	 * Generates the content of a 'gameend' message for a specific bot.
	 * @param bot The bot that will receive the message
	 * @return The content of the 'gameend' message
	 * @see Documentation/gameend.html
	 */
	public abstract JSONObject genGameEndMessageContent(Bot bot);
}
