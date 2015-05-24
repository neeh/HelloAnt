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
 * This class represents an ant hill where ants can spawn from. A hill belongs to a bot.
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
	 * The last time an ant was on top of the hill.
	 */
	private int lastVisitRound;
	
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
	 * Gets a JSON representation of a hill.
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

	/**
	 * Used to get less recently visited hill first using a sort.
	 */
	@Override
	public int compareTo(AntHill o)
	{
		return getLastVisitRound() - o.getLastVisitRound();
	}
}
