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
	ArrayList<JSONArray> ants;
	
	/**
	 * The track of food units.
	 */
	ArrayList<JSONArray> food;
	
	
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
}
