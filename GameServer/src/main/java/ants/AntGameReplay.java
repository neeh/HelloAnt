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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class stores the evolution of the state of an ant game. It can be exported as JSON
 * and then used in the AIViewer to replay the game.
 * @see Documentation/protocol/replayformat.html
 * @class
 * @author Nicolas
 */
public class AntGameReplay
{
	/**
	 * The track of bot ants.
	 */
	private ArrayList<JSONArray> ants;
	
	/**
	 * The track of food units.
	 */
	private ArrayList<JSONArray> food;
	
	/**
	 * The name of the cut-off rule that ended the game.
	 */
	@SuppressWarnings("unused")
	private String cutoff;
	
	/**
	 * Creates a new game replay object.
	 * @constructor
	 */
	public AntGameReplay()
	{
		ants = new ArrayList<JSONArray>();
		food = new ArrayList<JSONArray>();
	}
	
	/**
	 * Generates the replay of an ant game as a JSON object.
	 * @return a JSON object containing replay data for this game.
	 * @throws JSONException if replay data cannot be accessed.
	 */
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject replay = new JSONObject();
		replay.put("replayformat", "json");
		JSONObject replaydata = new JSONObject();
		replaydata.put("ants", ants);
		replaydata.put("food", food);
		return null;
	}
	
	/**
	 * Adds an ant track in the replay.
	 * @param antData the ant track to add.
	 */
	public void addAntData(JSONArray antData)
	{
		ants.add(antData);
	}
	
	/**
	 * Adds an ant track in the replay.
	 * @param foodData the food track to add.
	 */
	public void addFoodData(JSONArray foodData)
	{
		food.add(foodData);
	}
	
	/**
	 * Sets the name of the cut-off rule that ended the game.
	 * @param status the name of the cut-off rule.
	 */
	public void setCutoff(String cutoff)
	{
		this.cutoff = cutoff;
	}
}
