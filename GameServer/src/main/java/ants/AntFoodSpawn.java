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

/**
 * This class represents a game object that generates food on the map that can be
 * harvested by ants. A food unit can be held by an ant and brought back to the ant's hill
 * to give birth to a new ant.
 * @class
 * @author Benjamin
 */
public class AntFoodSpawn extends AntGameObject
{
	/**
	 * Whether food is available on the spawn.
	 */
	private boolean food;
	
	/**
	 * The last time food was harvested from the food spawn.
	 */
	private int lastHarvestRound;
	
	/**
	 * The replay data of the food currently present in the spawn.
	 * [ row, col, pop_round, gth_round, bot_id ]
	 * @see Documentation/protocol/replayformat.html
	 */
	private JSONArray replayData;
	
	/**
	 * Creates a new food spawn from a column and row identifier.
	 * @constructor
	 * @param moveHandler the handler used to move the food spawn on the game map.
	 * @param col the initial column identifier of the food spawn.
	 * @param row the initial row identifier of the food spawn.
	 */
	public AntFoodSpawn(AntGameMapCallback moveHandler, int col, int row)
	{
		super(moveHandler, col, row, false, false);
		food = true;
		lastHarvestRound = 0;
	}
	
	/**
	 * Creates a new food spawn from a cell descriptor.
	 * @constructor
	 * @param moveHandler the handler used to move the food spawn on the game map.
	 * @param cell the cell descriptor that positions the food spawn on the map.
	 */
	public AntFoodSpawn(AntGameMapCallback moveHandler, Cell cell)
	{
		super(moveHandler, cell, false, false);
		food = true;
		lastHarvestRound = 0;
	}
	
	/**
	 * Creates food on the food spawn.
	 * @param round the game round at which the food is spawned.
	 */
	public void createFood(int round)
	{
		replayData = new JSONArray();
		try
		{
			replayData.put(0, row);
			replayData.put(1, col);
			replayData.put(2, round);
		}
		catch (JSONException e) {}
		food = true;
	}
	
	/**
	 * Harvests food on the food spawn.
	 * @param botId the identifier of the bot that harvested the food unit.
	 * @param round the game round at which the food is harvested.
	 */
	public void harvestFood(int botId, int round)
	{
		try
		{
			replayData.put(3, round);
			replayData.put(4, botId);
		}
		catch (JSONException e) {}
		replayData = null;
		food = false;
		lastHarvestRound = round;
	}
	
	/**
	 * Gets a JSON representation of a food unit.
	 * @see Documentation/protocol/gamestate.html
	 * @note the function returns null when the spawn is empty because bots should not
	 *       know a food spawn is there, unless food is currently present on it.
	 * @return [ "F", row, col ] when food is present.
	 */
	public JSONArray toJSONArray()
	{
		if (food == false) return null;
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, "F");
			array.put(1, row);
			array.put(2, col);
		}
		catch (JSONException e) {}
		return array;
	}
	
	/**
	 * Returns whether a food unit is present on the food spawn.
	 * @return true if food there is food on the spawn, false otherwise.
	 */
	public boolean hasFood()
	{
		return food;
	}
	
	/**
	 * Gets the last time a food unit was harvested on this spawn.
	 * @return the last time food was harvested.
	 */
	public int getLastHarvestRound()
	{
		return lastHarvestRound;
	}
	
	/**
	 * Gets the replay data for the food unit currently present in the spawn.
	 * @return the replay data of the food unit.
	 */
	public JSONArray getReplayData()
	{
		return replayData;
	}
}
