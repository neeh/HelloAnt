package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Game is the class that should be overloaded to create a new game type.
 * For instance, if you want your bots to be able to play a chess game, then create a 
 * ChessGame class which implements the Game class and overloads its methods according
 * to the game specifications.
 * @class
 * @author Nicolas
 */
@SuppressWarnings("unused")
public abstract class Game
{
	/**
	 * Describes how the game should be played.
	 * When true, only one bot is allowed to play during a game round.
	 * That's the way chess game is played, for example.
	 * When false, all the bot have to send their actions during a game round.
	 * @note some weird games may be very tricky to design. We recommend you put this
	 * boolean to false and then manually manage the 'curBot' identifier and the
	 * 'curRound' counter in the 'update' method in this case.
	 * @deprecated
	 */
	private Boolean oneBotPerRound;
	
	/**
	 * The list of bots in the game.
	 * If a bot was kicked during the match, it remains in this list but no longer
	 * receives server messages for this game until the match ends.
	 */
	private ArrayList<Bot> bots;
	
	/**
	 * The informations of every bots in this game.
	 */
	private Map<Bot, BotGameInfo> botInfos;
	
	/**
	 * The list of game score for each bot.
	 * Game scores are updated after a round.
	 * @deprecated
	 */
	private ArrayList<Integer> gameScores;
	
	/**
	 * The list of bot enabled.
	 * When a bot is disabled, it no longer receives game state informations and should
	 * wait for the end of the game to be able to play anew.
	 * If you need to stop the reception of actions of a specific bot, switch its
	 * boolean off in this list.
	 * @deprecated
	 */
	private ArrayList<Boolean> BotEnabled;
	
	/**
	 * The delay of response a bot should respect to send its "gameactions" message after
	 * a "gamestate" message from the server. (in milliseconds)
	 */
	private int responseDelayMs;
	
	/**
	 * The identifier of the bot (in the 'bots' list) which is currently playing.
	 * Only relevant when 'oneBotPerRound' is true.
	 * @deprecated
	 */
	private int curBot;
	
	/**
	 * The current round identifier of the game.
	 * @note the counter should be manually incremented in the update method.
	 */
	private int curRound;
	
	/**
	 * The maximum round count of the game.
	 * Can be set to -1 for unlimited round count.
	 */
	private int maxRound;
	
	/**
	 * Gets the current round identifier of the game.
	 * @return the current round identifier.
	 */
	public int getCurRound()
	{
		return curRound;
	}
	
	/**
	 * Gets the maximum round count of the game.
	 * @return the max round count.
	 */
	public int getMaxRound()
	{
		return maxRound;
	}
	
	/**
	 * Returns how the game should be played.
	 * @return true if only one bot can play per round, false otherwise.
	 * @deprecated
	 */
	public Boolean isOneBotPerRound()
	{
		return oneBotPerRound;
	}
	
	/**
	 * Initialize the game state.
	 */
	public abstract void init();
	
	/**
	 * Returns whether the current game state validates the game ending conditions.
	 * @return true is the game is finished, false otherwise.
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
	public abstract void update();
	
	/**
	 * Receives actions of the bots for the current round.
	 * Returns the error ID associated with the message: 103 when the bot is playing
	 * whereas it's not its turn, 102 when the bot was muted for the game, 0 otherwise.
	 * @see Documentation/protocol/gameactions.html
	 * @param bot the bot who give the actions.
	 * @param content the content of the "gameactions" message.
	 * @return the error ID of the "gameactions" message.
	 * @throws JSONException if the content is not correctly formed.
	 */
	public int receiveActions(Bot bot, JSONObject content) throws JSONException {
		BotGameInfo info = botInfos.get(bot);
		// Check if the bot is not muted.
		if (info.isMuted() == false) {
			// Check if the bot has not already played for this round.
			if (info.hasPlayed() == false) {
				// Check if the bot has not exceeded the response time limit.
				if (System.currentTimeMillis() - info.getGamestateTimestampMs() <=
					responseDelayMs) {
					executeActions(bot, content);
				}
				info.setPlayed(true);
				info.setMuted(true);
				bot.getCommunicator().sendGameMuteMessage(genGameMuteMessageContent("Too "
						+ "late"));
			}
			return 103;
		}
		return 102;
	}
	
	/**
	 * Executes the actions of a bot. You should implement this method to update your game
	 * state according to the actions a the bot.
	 * @see Documentation/protocol/gameactions.html
	 * @param bot the bot whive gave the actions.
	 * @param actions the content of the "gameactions" message.
	 * @throws JSONException if the actions object is not correctly formed.
	 */
	public abstract void executeActions(Bot bot, JSONObject actions) throws JSONException;
	
	/**
	 * Generates the content of a "gamestate" message for a specific bot.
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestate" message.
	 * @see Documentation/protocol/gamestate.html
	 */
	public abstract JSONObject genGameStateMessageContent(Bot bot);
	
	/**
	 * Generates the content of a "gamestart" message for a specific bot.
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestart" message.
	 * @see Documentation/protocol/gamestart.html
	 */
	public abstract JSONObject genGameStartMessageContent(Bot bot);
	
	/**
	 * Generates the content of a "gameend" message for a specific bot.
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gameend" message.
	 * @see Documentation/protocol/gameend.html
	 */
	public abstract JSONObject genGameEndMessageContent(Bot bot);
	
	/**
	 * Generates the content of a "gamemute" message.
	 * @param reason the reason(s) of the mute.
	 * @return the content of the "gamemute" message.
	 * @see Documentation/protocol/gamemute.html
	 */
	private JSONObject genGameMuteMessageContent(String reason) {
		JSONObject content = new JSONObject();
		try {
			content.put("reason", reason);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return content;
	}
}
