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

package ants;

import org.json.JSONArray;
import org.json.JSONException;

import util.Cell;
import util.Move;
import basis.Bot;

/**
 * This class represents an ant in the game of ants. An ant is a movable game object
 * controlled by bot that can holds a food unit. An ant may die during a battle.
 * @class
 * @author Benjamin
 */
public class Ant extends AntGameObject
{
	/**
	 * The bot that owns the ant.
	 */
	private Bot bot;
	
	/**
	 * Whether the ant holds food.
	 */
	private boolean food;
	
	/**
	 * Whether the ant is dead.
	 */
	private boolean dead;
	
	/**
	 * The replay data for this ant, stored in a JSON array.
	 * [ row, col, pop_round, dth_round, owner_id, move_string ]
	 * @see Documentation/protocol/replayformat.html
	 */
	private JSONArray replayData;
	
	/**
	 * Whether the ant has moved for this round.
	 */
	private boolean moved;
	
	/**
	 * Creates a new ant for a bot from a column and a row identifier.
	 * @constructor
	 * @param moveHandler the handler used to moved the ant on the game map.
	 * @param col the initial column identifier of the ant.
	 * @param row the initial row identifier of the ant.
	 * @param bot the bot that owns the ant.
	 * @param botId the id associated with the bot (used for replay data).
	 * @param round the current game round (used for replay data).
	 */
	public Ant(AntGameMapCallback moveHandler, int col, int row, Bot bot, int botId,
			int round)
	{
		super(moveHandler, col, row, true, true);
		this.bot = bot;
		food = false;
		dead = false;
		createReplayData(botId, round);
	}
	
	/**
	 * Creates a new ant from a cell descriptor and a reference to the owner of the ant.
	 * @constructor
	 * @param moveHandler the handler used to moved the ant on the game map.
	 * @param cell the cell descriptor that positions the ant on the map.
	 * @param bot the bot that owns the ant.
	 * @param botId the id associated with the bot (used for replay data).
	 * @param round the current game round (used for replay data).
	 */
	public Ant(AntGameMapCallback moveHandler, Cell cell, Bot bot, int botId, int round)
	{
		super(moveHandler, cell, true, true);
		this.bot = bot;
		food = false;
		dead = false;
		createReplayData(botId, round);
	}
	
	/**
	 * Creates replay data for the ant.
	 * @see Documentation/protocol/replayformat.html
	 * @param botId the id associated with the bot.
	 * @param round the current game round.
	 */
	private void createReplayData(int botId, int round)
	{
		replayData = new JSONArray();
		try
		{
			replayData.put(0, row);
			replayData.put(1, col);
			replayData.put(2, round);
			replayData.put(4, botId);
			replayData.put(5, "");
		}
		catch (JSONException e) {}
	}
	
	/**
	 * Adds a blank move to the move string of the replay data of the ant.
	 * @see Documentation/protocol/replayformat.html
	 */
	public void addBlankMove()
	{
		try
		{
			replayData.put(5, replayData.getString(5) + "-");
		}
		catch (JSONException e) {}
	}
	
	/**
	 * Moves an ant on the game map.
	 * This method overrides the one of its parent to manage replay data.
	 */
	@Override
	public void move(Move direction)
	{
		super.move(direction);
		try
		{	// Add the move in the move string.
			String moveChar = Move.toString(direction).toLowerCase();
			replayData.put(5, replayData.getString(5) + moveChar);
			// ---------------------------------------------------------------------------
			// Now, we set a boolean 'moved' to true to remember that a move was already
			// added in the move string of this ant during the current game round.
			// After each round, the game will check for ants that did not move and add a
			// blank move in the move string for these ants to ensure the consistency of
			// the move string over time.
			// Hence, 'moved' should be reset to false at the end of a game round.
			// ---------------------------------------------------------------------------
			moved = true;
		}
		catch (JSONException e) {}
	}
	
	/**
	 * Gets a JSON representation of the ant.
	 * @see Documentation/protocol/gamestate.html
	 * @param botId the bot identifier of the owner, viewed by the bot.
	 * @return [ ["A" | "B" | "X"], col, row, owner_id ]
	 */
	public JSONArray toJSONArray(int botId)
	{
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, dead == false ? food == false ? "A" : "B" : "X");
			array.put(1, row);
			array.put(2, col);
			array.put(3, botId);
		}
		catch (JSONException e) {}
		return array;
	}
	
	/**
	 * Gets the bot the ant belongs to.
	 * @return the bot that owns the ant.
	 */
	public Bot getBot()
	{
		return bot;
	}
	
	/**
	 * Returns whether the ant holds food.
	 * @return true if the ant holds food, false otherwise.
	 */
	public boolean hasFood()
	{
		return food;
	}
	
	/**
	 * Returns whether the ant is dead.
	 * @return true is the ant is dead, false otherwise.
	 */
	public boolean isDead()
	{
		return dead;
	}
	
	/**
	 * Gets the replay data for this ant.
	 * @return the replay data of the ant.
	 */
	public JSONArray getReplayData()
	{
		return replayData;
	}
	
	/**
	 * Returns whether the ant has moved for this round.
	 * @return true if the ant moved, false otherwise.
	 */
	public boolean hasMoved()
	{
		return moved;
	}
	
	/**
	 * Sets whether the ant holds food.
	 * @param food true if the ant holds food, false otherwise.
	 */
	public void setFood(boolean food)
	{
		this.food = food;
	}
	
	/**
	 * Kills the ant.
	 * @param round the round at which the kill occurred (used for replay data).
	 */
	public void kill(int round)
	{
		dead = true;
		try
		{
			replayData.put(3, round);
		}
		catch (JSONException e) {}
	}
	
	/**
	 * Sets whether the ant has moved for this game round.
	 * @return true if the ant moved, false otherwise.
	 */
	public void setMoved(boolean moved)
	{
		this.moved = moved;
	}
}
