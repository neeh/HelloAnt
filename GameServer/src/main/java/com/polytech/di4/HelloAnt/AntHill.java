package com.polytech.di4.HelloAnt;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class represents an ant hill where ants can spawn from. A hill belongs to a bot.
 * @class
 * @author Benjamin
 */
public class AntHill extends AntGameObject 
{
	/**
	 * The bot that owns the ant hill.
	 */
	private Bot bot;
	
	/**
	 * The last time an ant spawned from the hill.
	 */
	private int lastPopRound;
	
	/**
	 * Creates a new ant hill for a bot from a column and a row identifier.
	 * @constructor
	 * @param col the column identifier of the hill.
	 * @param row the row identifier of the hill.
	 * @param bot the bot that owns the hill.
	 */
	public AntHill(int col, int row, Bot bot)
	{
		super(col, row, false, false);
		this.bot = bot;
	}
	
	/**
	 * Creates a new ant hill from a cell descriptor and a reference to the owner.
	 * @constructor
	 * @param cell the cell descriptor that positions the hill on the map.
	 * @param bot the bot the hill belong to.
	 */
	public AntHill(Cell cell, Bot bot)
	{
		super(cell, false, false);
		this.bot = bot;
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
	 * Gets the last time an ant spawned from the hill.
	 * @return the last time an ant spawned from the hill.
	 */
	public int getLastPopRound()
	{
		return lastPopRound;
	}
	
	/**
	 * Gets a JSON representation of a wall.
	 * @see Documentation/protocol/gamestate.html
	 * @param botId the bot identifier of the owner, viewed by the bot.
	 * @return [ "H", col, row, owner_id ]
	 */
	public JSONArray toJSONArray(int botId)
	{
		JSONArray array = new JSONArray();
		try
		{
			array.put(0, "H");
			array.put(1, col);
			array.put(2, row);
			array.put(3, botId);
		}
		catch (JSONException e) {}
		return array;
	}
	
	/**
	 * Sets the last time an ant spawned from the hill.
	 * @param lastPopRound the last time an ant spawned from the hill.
	 */
	public void setLastPopRound(int lastPopRound)
	{
		this.lastPopRound = lastPopRound;
	}
}
