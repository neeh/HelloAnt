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
 * and then used in the AIVisualizer to replay the game.
 * @see Documentation/protocol/replayformat.html
 * @class
 * @author Nicolas
 */
public class AntGameReplayData
{
	/** The name of the cut-off rule that ended the game. */
	private String cutoff;
	
	/** The list of bot ants tracks. */
	private ArrayList<JSONArray> ants;
	
	/** The list of food units tracks. */
	private ArrayList<JSONArray> food;
	
	/** Hive history track of each bot. */
	private ArrayList<ArrayList<Integer>> hiveHistory;
	
	/** The list of ant hills tracks. */
	private ArrayList<JSONArray> hills;
	
	/** The map template data. */
	private JSONObject map;
	
	/** The ending bonus points for each bot. */
	private ArrayList<Integer> bonus;
	
	/** The last round of the game. */
	private int winningTurn;
	
	/** Bot game scores tracks */
	private ArrayList<ArrayList<Integer>> scores;
	
	/** The ranking round of the game. */
	private int rankingTurn;
	
	/** The squared radius of the view mask. */
	private float viewRadius2;
	
	/** The loading time of the game. */
	private int loadTime;
	
	/** The maximum number of turns of the game. */
	private int turns;
	
	/** The squared radius of the attack mask. */
	private float attackRadius2;
	
	/** The number of players in the game. */
	private int players;
	
	/** The time for one turn of this game. */
	private int turnTime;
	
	/**
	 * Creates a new game replay object.
	 * @constructor
	 * @see Documentation/protocol/replayformat.html
	 * @param mapTemplate the map template used in the game.
	 * @param viewRadius2 the squared radius of the view mask.
	 * @param attackRadius2 the squared radius of the attack mask.
	 * @param maxRound the maximum round count for this game.
	 * @param loadTimeMs the load time of the game in milliseconds.
	 * @param responseTimeMs the response time for one round in milliseconds.
	 */
	public AntGameReplayData(AntMapTemplate mapTemplate, float viewRadius2,
			float attackRadius2, int maxRound, int loadTimeMs, int responseTimeMs)
	{
		int botCount = mapTemplate.getBotCount();
		int i;
		
		cutoff = "";
		ants = new ArrayList<JSONArray>();
		food = new ArrayList<JSONArray>();
		hiveHistory = new ArrayList<ArrayList<Integer>>(botCount);
		for (i = 0; i < botCount; i++)
		{
			hiveHistory.add(new ArrayList<Integer>());
		}
		hills = new ArrayList<JSONArray>();
		map = mapTemplate.toJSON();
		bonus = new ArrayList<Integer>(botCount);
		for (i = 0; i < botCount; i++)
		{
			bonus.add(0);
		}
		winningTurn = 0;
		scores = new ArrayList<ArrayList<Integer>>(botCount);
		for (i = 0; i < botCount; i++)
		{
			scores.add(new ArrayList<Integer>());
		}
		rankingTurn = 0;
		this.viewRadius2 = viewRadius2;
		loadTime = loadTimeMs;
		turns = maxRound;
		this.attackRadius2 = attackRadius2;
		players = botCount;
		turnTime = responseTimeMs;
	}
	
	/**
	 * Generates the replay of an ant game as a JSON object.
	 * @return a JSON object containing replay data for this game.
	 * @throws JSONException if replay data cannot be accessed.
	 */
	public JSONObject toJSONObject() throws JSONException
	{
		int botCount = players;
		int i;
		
		JSONArray hiveHistory = new JSONArray();
		for (i = 0; i < botCount; i++)
		{
			hiveHistory.put(i, new JSONArray(this.hiveHistory.get(i)));
		}
		JSONArray scores = new JSONArray();
		for (i = 0; i < botCount; i++)
		{
			scores.put(i, new JSONArray(this.scores.get(i)));
		}
		
		JSONObject replaydata = new JSONObject();
		replaydata.put("cutoff", cutoff);
		replaydata.put("ants", new JSONArray(ants));
		replaydata.put("food", new JSONArray(food));
		replaydata.put("hive_history", hiveHistory);
		replaydata.put("player_seed", 0);
		replaydata.put("hills", new JSONArray(hills));
		replaydata.put("food_rate", 5);
		replaydata.put("revision", 3);
		replaydata.put("map", map);
		replaydata.put("bonus", new JSONArray(bonus));
		replaydata.put("winning_turn", winningTurn);
		replaydata.put("scores", scores);
		replaydata.put("ranking_turn", rankingTurn);
		replaydata.put("spawnradius2", 1);
		replaydata.put("engine_seed", 0);
		replaydata.put("viewradius2", viewRadius2);
		replaydata.put("loadtime", loadTime);
		replaydata.put("turns", turns);
		replaydata.put("attackradius2", attackRadius2);
		replaydata.put("players", players);
		replaydata.put("food_turn", 0);
		replaydata.put("food_start", 0);
		replaydata.put("turntime", turnTime);
		return replaydata;
	}
	
	/**
	 * Sets the name of the cut-off rule that ended the game.
	 * @param status the name of the cut-off rule.
	 */
	public void setCutoff(String cutoff)
	{
		this.cutoff = cutoff;
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
	 * Adds a hive amount in the hive history track of the bot identified {@code botId}
	 * in the game.
	 * @param botId the game identifier of the bot to add the hive record.
	 * @param hive the hive record to add to the track.
	 */
	public void addHiveHistoryRecord(int botId, int hive)
	{
		hiveHistory.get(botId).add(hive);
	}
	
	/**
	 * Adds a hill track in the replay.
	 * @param hillData the hill track to add.
	 */
	public void addHillData(JSONArray hillData)
	{
		hills.add(hillData);
	}
	
	/**
	 * Sets the bonus points of the bot identified {@code botId} in the game.
	 * @param botId the game identifier of the bot to set the bonus points.
	 * @param bonus the bonus points amount.
	 */
	public void setBonus(int botId, int bonus)
	{
		this.bonus.set(botId, bonus);
	}
	
	/**
	 * Sets the last round of the game in the replay data.
	 * @param winninTurn the last gaming round.
	 */
	public void setWinningTurn(int winningTurn)
	{
		this.winningTurn = winningTurn;
	}
	
	/**
	 * Adds a game score amount in the game scores track of the bot identified
	 * {@code botId} in the game.
	 * @param botId the game identifier of the bot to add the game score amount.
	 * @param score the game score amount to add.
	 */
	public void addScoresRecord(int botId, int score)
	{
		scores.get(botId).add(score);
	}
	
	/**
	 * Sets the ranking turn of the game.
	 * @param rankingTurn the ranking turn of the game.
	 */
	public void setRankingTurn(int rankingTurn)
	{
		this.rankingTurn = rankingTurn;
	}
}
