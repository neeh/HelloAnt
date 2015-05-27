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
import basis.Bot;

/**
 * This class represents an ant hill where ants can spawn from.
 * A hill belongs to a bot.
 * @class
 * @author Benjamin
 */
public class AntHill extends AntGameObject implements Comparable<AntHill>
{
	/**
	 * The bot that owns the ant hill.
	 */
	private Bot bot;
	
	/**
	 * The last time an ant visited the hill.
	 */
	private int lastVisitRound;
	
	/**
	 * The replay data for this hill. Stored in a JSON array.
	 * [ row, col, owner_id, turn_death ]
	 * @see Documentation/protocol/replayformat.html
	 */
	private JSONArray replayData;
	
	/**
	 * Creates a new ant hill for a bot from a column and a row identifier.
	 * @constructor
	 * @param moveHandler the handler used to move the hill.
	 * @param col the initial column identifier of the hill.
	 * @param row the initial row identifier of the hill.
	 * @param bot the bot that owns the hill.
	 * @param botId the id associated with the bot (used for replay data).
	 */
	public AntHill(AntGameMapCallback moveHandler, int col, int row, Bot bot, int botId)
	{
		super(moveHandler, col, row, false, false);
		this.bot = bot;
		createReplayData(botId);
	}
	
	/**
	 * Creates a new ant hill from a cell descriptor and a reference to the owner.
	 * @constructor
	 * @param moveHandler the handler used to move the hill.
	 * @param cell the cell descriptor that positions the hill on the map.
	 * @param bot the bot that owns the hill.
	 * @param botId the id associated with the bot (used for replay data).
	 */
	public AntHill(AntGameMapCallback moveHandler, Cell cell, Bot bot, int botId)
	{
		super(moveHandler, cell, false, false);
		this.bot = bot;
		createReplayData(botId);
	}
	
	/**
	 * Creates replay data for the ant.
	 * @param botId the id associated with the bot.
	 * @param round the current game round.
	 */
	private void createReplayData(int botId)
	{
		replayData = new JSONArray();
		try
		{
			replayData.put(0, row);
			replayData.put(1, col);
			replayData.put(2, botId);
		}
		catch (JSONException e) {}
	}
	
	/**
	 * Razes the hill.
	 * @param round the game round at which the hill was razed (used for replay data).
	 */
	public void raze(int round)
	{
		try
		{
			replayData.put(3, round);
		}
		catch (JSONException e) {}
	}
	
	/**
	 * Compares with another hill to get the less recently visited first using a sort.
	 * @param hill The ant hill to compare to.
	 */
	@Override
	public int compareTo(AntHill hill)
	{
		return getLastVisitRound() - hill.getLastVisitRound();
	}
	
	/**
	 * Gets a JSON representation of a hill.
	 * @see Documentation/protocol/gamestate.html
	 * @param botId the bot identifier of the owner, viewed by the bot.
	 * @return [ "H", row, col, owner_id ]
	 */
	public JSONArray toJSONArray(int botId)
	{
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, "H");
			array.put(1, row);
			array.put(2, col);
			array.put(3, botId);
		}
		catch (JSONException e) {}
		return array;
	}
	
	/**
	 * Gets the owner of the ant hill.
	 * @return the bot that owns the hill.
	 */
	public Bot getBot()
	{
		return bot;
	}
	
	/**
	 * Gets the replay data for this hill.
	 * @return the replay data of the hill.
	 */
	public JSONArray getReplayData()
	{
		return replayData;
	}
	
	/**
	 * Gets the last time an ant was on top of the hill.
	 * @return the last time an ant was on top of the hill.
	 */
	public int getLastVisitRound()
	{
		return lastVisitRound;
	}
	
	/**
	 * Sets the last time an ant was on top of the hill.
	 * @param lastPopRound the last time an ant was on top of the hill.
	 */
	public void setLastVisitRound(int lastVisitRound)
	{
		this.lastVisitRound = lastVisitRound;
	}
}
