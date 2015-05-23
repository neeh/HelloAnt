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
	private Boolean food;
	
	/**
	 * The last time food was taken from the food spawn.
	 */
	private int lastHarvestRound;
	
	/**
	 * The replay data of the food currently present in the spawn.
	 * [ col, row, pop_round, gth_round, bot_id ]
	 */
	private JSONArray replayData;
	
	/**
	 * Creates a new food spawn from a column and row identifier.
	 * @constructor
	 * @param col the column identifier of the food spawn.
	 * @param row the row identifier of the food spawn.
	 */
	public AntFoodSpawn(int col, int row)
	{
		super(col, row, false, false);
		food = true;
		lastHarvestRound = 0;
	}
	
	/**
	 * Creates a new food spawn from a cell descriptor.
	 * @constructor
	 * @param cell the cell descriptor that positions the food spawn on the map.
	 */
	public AntFoodSpawn(Cell cell)
	{
		super(cell, false, false);
		food = true;
		lastHarvestRound = 0;
	}
	
	/**
	 * Creates food on the food spawn.
	 * @param round the round at which the food is spawned.
	 */
	public void createFood(int round)
	{
		replayData = new JSONArray();
		try
		{
			replayData.put(0, col);
			replayData.put(1, row);
			replayData.put(2, round);
		}
		catch (JSONException e) {}
		food = true;
	}
	
	/**
	 * Harvests food on the food spawn.
	 * @param botId the identifier of the bot that harvested the food unit.
	 * @param round the round at which the food is harvested.
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
	 * Returns whether a food unit is present on the food spawn.
	 * @return true if food there is food on the spawn, false otherwise.
	 */
	public Boolean hasFood()
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
	 * Gets a JSON representation of a food unit.
	 * @see Documentation/protocol/gamestate.html
	 * @note the function returns null when the spawn is empty because bots should not
	 *       know a food spawn is there, unless food is currently present on it.
	 * @return [ "F", col, row ] when food is present.
	 */
	public JSONArray toJSONArray()
	{
		if (food == false) return null;
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, "F");
			array.put(1, col);
			array.put(2, row);
		}
		catch (JSONException e) {}
		return array;
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
