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

import java.util.ArrayList;
import java.util.Iterator;
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
public abstract class Game
{
	/**
	 * The list of bots in the game.
	 * If a bot was kicked during the match, it remains in this list but no longer
	 * receives server messages for this game until the match ends.
	 */
	protected ArrayList<Bot> bots;
	
	/**
	 * The informations relative to bots required for the game.
	 */
	protected Map<Bot, BotGameInfo> botInfos;
	
	/**
	 * Whether all bots that should play during the current round have sent their game
	 * actions. This boolean can be tested to stop waiting for game actions when the game
	 * won't receive those anymore.
	 */
	private boolean ready;
	
	/**
	 * The time the server has to wait after "gamestart" message. (in milliseconds)
	 */
	protected int loadTimeMs;
	
	/**
	 * The time of response a bot should respect to send its "gameactions" message after a
	 * "gamestate" message from the server. (in milliseconds)
	 */
	protected int responseTimeMs;
	
	/**
	 * The current round identifier of the game.
	 * @note the counter should be manually incremented in the update method.
	 */
	protected int curRound;
	
	/**
	 * The maximum round count of the game.
	 * Can be set to -1 for unlimited round count.
	 */
	protected int maxRound;
	
	/**
	 * Initializes the game state.
	 */
	public abstract void init();
	
	/**
	 * Terminates the game and cleans it.
	 */
	public abstract void terminate();
	
	/**
	 * Returns whether the current game state matches the game ending conditions.
	 * @return true if the game is finished, false otherwise.
	 */
	public boolean isFinished()
	{
		// A trivial ending condition is the maximum number of round reached.
		if (maxRound > 0 && curRound == maxRound) return true;
		return false;
	}
	
	/**
	 * Updates the score of each bot based on its final game score.
	 * You can overload this method if you want a more specific score calculation.
	 * @see ELO rating system
	 */
	public void computeBotScores()
	{
		// the number of bots.
		int n = bots.size();
		int sumGameScores = 0;
		// Get the sum of all bot game scores.
		for (Iterator<Bot> i = bots.iterator(); i.hasNext(); )
		{
			Bot bot = i.next();
			BotGameInfo info = botInfos.get(bot);
			sumGameScores += info.getGameScore();
		}
		// Update the general score of a bot.
		for (Iterator<Bot> i = bots.iterator(); i.hasNext(); )
		{
			Bot bot = i.next();
			BotGameInfo info = botInfos.get(bot);
			bot.setScore(bot.getScore() + 200 * (n * (info.getGameScore() / sumGameScores)
					- 1));
		}
	}
	
	/**
	 * Updates the game state.
	 */
	public abstract void update();
	
	/**
	 * Mutes a bot in this game and sends it a "gamemute" message.
	 * @see Documentation/protocol/gamemute.html
	 * @param bot the bot to mute.
	 * @param reason a message explaining why the bot was muted.
	 */
	public void muteBot(Bot bot, String reason)
	{
		BotGameInfo botInfo = botInfos.get(bot);
		botInfo.setGamestateTimestampMs(0);
		botInfo.setPlayed(true);
		botInfo.setMuted(true);
		bot.getCommunicator().sendGameMute(genGameMuteMessageContent(reason));
	}
	
	/**
	 * Mutes the bots that did not send their actions during the current round.
	 * @see Documentation/protocol/gamemute.html
	 */
	public void muteNonPlayerBots()
	{
		for (Map.Entry<Bot, BotGameInfo> info : botInfos.entrySet())
		{	// For each bot, test whether it played.
			Bot bot = info.getKey();
			BotGameInfo botInfo = info.getValue();
			if (bot.getCommunicator().isBotLoggedIn() && botInfo.hasPlayed() == false && botInfo.isMuted() == false)
			{	// The bot has not played for this round, mute it.
				muteBot(bot, "You did not send your game actions");
			}
		}
	}
	
	/**
	 * Executes the actions of a bot. You should implement this method to update your game
	 * state according to the actions a the bot.
	 * @see Documentation/protocol/gameactions.html
	 * @param bot the bot which gave the actions.
	 * @param content the content of the "gameactions" message.
	 * @throws JSONException if the actions object is not correctly formed.
	 */
	protected abstract void executeActions(Bot bot, JSONObject content) throws
		JSONException;
	
	/**
	 * Receives the actions of a bot for the current round.
	 * Returns the error ID associated with the message: 104 if the bot was too late to
	 * give its actions, 103 when the bot is playing whereas it's not its turn, 102 when
	 * the bot was muted for the game, 0 otherwise.
	 * @see Documentation/protocol/gameactions.html
	 * @param bot the bot who gave the actions.
	 * @param content the content of the "gameactions" message.
	 * @return the error ID of the "gameactions" message.
	 * @throws JSONException if the content is not correctly formed.
	 */
	public int receiveActions(Bot bot, JSONObject content) throws JSONException
	{
		int error;
		// Get the current state of the bot.
		BotGameInfo botInfo = botInfos.get(bot);
		// Check if the bot is not muted.
		if (botInfo.isMuted() == false)
		{
			// Check if the bot has not already played for this round.
			if (botInfo.hasPlayed() == false)
			{
				// Check if the bot has not exceeded the response time limit.
				if (System.currentTimeMillis() - botInfo.getGamestateTimestampMs() <=
					responseTimeMs)
				{
					botInfo.setPlayed(true);
					executeActions(bot, content);
					// If all the bots have played for this round, change the ready state
					// of the game.
					boolean ok = true;
					for (Map.Entry<Bot, BotGameInfo> info : botInfos.entrySet())
					{	// For each bot, test whether it played.
						if (info.getValue().hasPlayed() == false)
						{	// A bot has not played yet.
							ok = false;
						}
					}
					// I added a boolean 'ok' to avoid modifying 'ready' directly.
					if (ok == true) ready = true;
					error = 0;
				}
				else
				{
					muteBot(bot, "Too late");
					// Too late
					error = 104;
				}
			}
			else
			{	// Already played
				error = 103;
			}
		}
		else
		{	// Muted
			error = 102;
		}
		return error;
	}
	
	/**
	 * Sends the current game state to a bot. A bot which receives a "gamestate" message
	 * is supposed to return its game action within the imposed response delay.
	 * @see Documentation/protocol/gamestate.html
	 * @param bot the bot that will receive the "gamestate" message.
	 * @param canPlay whether the bot can play for this round. The bot is then supposed to
	 *        play the game by returning a "gameactions" message within the alloted
	 *        response time.
	 */
	private void sendGameState(Bot bot, boolean canPlay)
	{
		// Get the current state of the bot.
		BotGameInfo info = botInfos.get(bot);
		if (canPlay == true && info.isMuted() == false)
		{	// Memorize the timestamp of the sending.
			info.setGamestateTimestampMs(System.currentTimeMillis());
			// Wait for the bot to play during this round.
			info.setPlayed(false); // should be set to false BEFORE!
		}
		// Send the cooked message.
		bot.getCommunicator().sendGameState(genGameStateMessageContent(bot));
	}
	
	/**
	 * Sends the current game state to the bot(s).
	 * By default, the method is implemented to send the game state and wait for game
	 * actions to every bot playing in game.
	 */
	public void sendGameState()
	{
		ready = false;
		Iterator<Bot> botIt = bots.iterator();
		while (botIt.hasNext())
		{	// For each bot, send the game state and wait for actions.
			Bot bot = botIt.next();
			if (bot.getCommunicator().isBotLoggedIn())
				sendGameState(bot, true);
		}
	}
	
	/**
	 * Sends a "gamestart" message to all the bots in the game.
	 */
	public void sendGameStart()
	{
		Iterator<Bot> botIt = bots.iterator();
		while (botIt.hasNext())
		{	// For each bot, send the game start message.
			Bot bot = botIt.next();
			bot.getCommunicator().sendGameStart(genGameStartMessageContent(bot));
		}
	}
	
	/**
	 * Sends a "gameend" message to all the bots in the game.
	 */
	public void sendGameEnd()
	{
		Iterator<Bot> botIt = bots.iterator();
		while (botIt.hasNext())
		{	// For each bot, send the game start message.
			Bot bot = botIt.next();
			if (bot.getCommunicator().isBotLoggedIn())
				bot.getCommunicator().sendGameEnd(genGameEndMessageContent(bot));
		}
	}
	
	/**
	 * Finalizes the game.
	 */
	//public abstract void finalize();
	
	/**
	 * Generates the content of a "gamestate" message for a specific bot.
	 * @see Documentation/protocol/gamestate.html
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestate" message.
	 */
	protected abstract JSONObject genGameStateMessageContent(Bot bot);
	
	/**
	 * Generates the content of a "gamestart" message for a specific bot.
	 * @see Documentation/protocol/gamestart.html
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestart" message.
	 */
	protected abstract JSONObject genGameStartMessageContent(Bot bot);
	
	/**
	 * Generates the content of a "gameend" message for a specific bot.
	 * @see Documentation/protocol/gameend.html
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gameend" message.
	 */
	protected abstract JSONObject genGameEndMessageContent(Bot bot);
	
	/**
	 * Generates the content of a "gamemute" message.
	 * @see Documentation/protocol/gamemute.html
	 * @param reason the reason of the mute.
	 * @return the content of the "gamemute" message.
	 */
	protected JSONObject genGameMuteMessageContent(String reason)
	{
		JSONObject content = new JSONObject();
		try
		{
			content.put("reason", reason);
		}
		catch (JSONException e) {}
		return content;
	}
	
	/**
	 * Gets an iterator that iterates over bots in this game.
	 * @return an iterator from the bot list.
	 */
	public Iterator<Bot> getBotIterator()
	{
		return bots.iterator();
	}
	
	/**
	 * Returns whether the game is ready to update itself for the current round.
	 * @return true if all bots have played, false otherwise.
	 */
	public boolean isReady()
	{
		return ready;
	}
	
	/**
	 * Gets the time the server has to wait after "gamestart" message.
	 * @return the time to wait after a "gamestart" message.
	 */
	public int getLoadTimeMs()
	{
		return loadTimeMs;
	}
	
	/**
	 * Gets he time the server has to wait for receiving "gameactions" message.
	 * @return the time to wait for bot actions.
	 */
	public int getResponseTimeMs()
	{
		return responseTimeMs;
	}
	
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
}
