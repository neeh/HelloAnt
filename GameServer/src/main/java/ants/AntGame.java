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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.Cell;
import util.Move;
import basis.Bot;
import basis.BotGameInfo;
import basis.BotMode;
import basis.Game;

public class AntGame extends Game
{
	/**
	 * The static map template used to initialize the game objects of the game map.
	 * It represents the generic layout of the game map.
	 */
	private AntMapTemplate mapTemplate;
	
	/**
	 * The dynamic map storing all the game objects of the game.
	 * It represents the current state of the game.
	 */
	private AntGameMap map;
	
	/**
	 * The list of ants present on the map.
	 */
	private ArrayList<Ant> ants;
	
	/**
	 * The list of food spawns present on the map.
	 */
	private ArrayList<AntFoodSpawn> foodSpawns;
	
	/**
	 * The number of rounds required for food to respawn.
	 */
	private static int foodRespawnDelay = 15;
	
	/**
	 * The mask that simulates fog war for vision.
	 */
	private AntGameMapMask viewMask;
	
	/**
	 * The mask that simulates fog war for battles.
	 */
	private AntGameMapMask attackMask;
	
	/**
	 * The class that holds replay data for the ant game.
	 */
	private AntGameReplayData replay;
	
	/**
	 * Creates a new ant game from a map template and a list of bots playing in this game.
	 * Fake bots are created to play with the other bots if there is not enough bots.
	 * @constructor
	 * @param bots the list of bots that are gonna play in this game.
	 * @param mapTemplate the template used to initialize the game map.
	 */
	public AntGame(ArrayList<Bot> bots, AntMapTemplate mapTemplate)
	{
		// Add fake bots to supply the correct bot count for the map.
		int fakeBotCount = mapTemplate.getBotCount() - bots.size();
		for (int i = 0; i < fakeBotCount; i++)
		{	// Create communicators and bots for fake bots.
			AntFakeCommunicator fakeCom = new AntFakeCommunicator(map);
			Bot fakeBot = new Bot(fakeCom, "CPU" + i, BotMode.TRAINING, 0, null);
			fakeCom.setBot(fakeBot);
			bots.add(fakeBot);
		}
		this.bots = bots;
		// Create bot game info for each bot.
		botInfos = new HashMap<Bot, BotGameInfo>(bots.size());
		Iterator<Bot> botIt = bots.iterator();
		int botId = 0;
		while (botIt.hasNext())
		{
			Bot bot = botIt.next();
			botInfos.put(bot, new AntBotGameInfo(botId++));
		}
		this.mapTemplate = mapTemplate;
		// Create game map and game objects.
		map = new AntGameMap(mapTemplate.getCols(), mapTemplate.getRows());
		ants = new ArrayList<Ant>();
		foodSpawns = new ArrayList<AntFoodSpawn>();
		// Create fog war masks.
		viewMask = new AntGameMapMask(77.0f);
		attackMask = new AntGameMapMask(5.0f);
		// Create replay object.
		replay = new AntGameReplayData(mapTemplate, 77.0f, 5.0f, maxRound,
				loadTimeMs, responseTimeMs);
	}
	
	/**
	 * Adds an ant to the game.
	 * @param ant the ant to add to the ants lists.
	 */
	protected void addAnt(Ant ant)
	{
		replay.addAntData(ant.getReplayData());
		ants.add(ant);
		((AntBotGameInfo) botInfos.get(ant.getBot())).addAnt(ant);
		map.addGameObject(ant);
	}
	
	/**
	 * Removes an ant from the game.
	 * @param ant the ant to remove from the ants lists.
	 */
	protected void removeAnt(Ant ant)
	{
		ants.remove(ant);
		((AntBotGameInfo) botInfos.get(ant.getBot())).removeAnt(ant);
		map.removeGameObject(ant);
	}
	
	/**
	 * Initializes an ant game.
	 */
	@Override
	public void init()
	{
		// Initialize bot game info for each bot.
		for(Map.Entry<Bot, BotGameInfo> entry : botInfos.entrySet())
		{
			((AntBotGameInfo) entry.getValue()).init(entry.getKey());
		}
		// Clears the map.
		map.clear();
		// Create walls on the map.
		Iterator<Cell> wallIt = mapTemplate.getWalls().iterator();
		while (wallIt.hasNext())
		{
			map.addGameObject(new AntWall(wallIt.next()));
		}
		// Create food spawns on the map.
		foodSpawns.clear();
		Iterator<Cell> foodSpawnIt = mapTemplate.getFoodSpawns().iterator();
		while (foodSpawnIt.hasNext())
		{
			AntFoodSpawn foodSpawn = new AntFoodSpawn(foodSpawnIt.next());
			foodSpawns.add(foodSpawn);
			map.addGameObject(foodSpawn);
			// Pop initial food.
			foodSpawn.createFood(curRound);
			replay.addFoodData(foodSpawn.getReplayData());
		}
		// Create initial ant(s) and hill(s) for each bot on the map.
		Iterator<ArrayList<Cell>> botHillsIt = mapTemplate.getHills().iterator();
		Iterator<Bot> botIt = bots.iterator();
		while (botHillsIt.hasNext())
		{
			Bot bot = botIt.next();
			AntBotGameInfo botInfo = (AntBotGameInfo) botInfos.get(bot);
			ArrayList<Cell> botHills = botHillsIt.next();
			Iterator<Cell> hillIt = botHills.iterator();
			while (hillIt.hasNext())
			{
				Cell hillCell = hillIt.next();
				// Create the hill on the map for the bot.
				AntHill hill = new AntHill(hillCell, bot);
				botInfo.addHill(hill);
				map.addGameObject(hill);
				// Create an initial ant inside the created hill for the bot.
				Ant ant = new Ant(hillCell, bot, botInfo.getId(), curRound);
				addAnt(ant);
				// Each bot start with 1 point per hill.
				botInfo.addGameScore(+1);
			}
		}
	}
	
	/**
	 * Returns whether the rank is stabilized (i.e. no player with at least one remaining 
	 *  hill can beat the score of any of his opponents).
	 * @return true if the rank is stabilized, false otherwise.
	 */
	private boolean isRankStabilized()
	{
		for (BotGameInfo player : botInfos.values())
		{
			AntBotGameInfo botInfo = (AntBotGameInfo) player;
			if (botInfo.isAlive() && botInfo.hasHills())
			{
				// There are 2 cases :
				// - The opponent has the same score as the player
				//   In this case, the player must be able to get a score strictly
				//    superior to the opponent for the rank to change
				// - The opponent has a higher score than the player
				//   In this case, the player must be able to get at least the same score
				//    as the opponent for the rank to change
				int maxPlayerScore = botInfo.getGameScore(),
						minEqualOpponentScore = Integer.MAX_VALUE,
						minSuperiorOpponentScore = Integer.MAX_VALUE;
				for (BotGameInfo opponent : botInfos.values())
				{
					AntBotGameInfo opponentInfo = (AntBotGameInfo) opponent;
					if (opponent == player ||
							botInfo.getGameScore() > opponentInfo.getGameScore())
					{
						continue;
					}
					int currentMinOpponentScore = opponentInfo.getGameScore();
					Iterator<AntHill> hillIt = opponentInfo.getHillIterator();
					while (hillIt.hasNext())
					{
						hillIt.next();
						currentMinOpponentScore -= 1;
						maxPlayerScore += 2;
					}
					if (botInfo.getGameScore() == opponentInfo.getGameScore())
					{
						if (currentMinOpponentScore < minEqualOpponentScore)
						{
							minEqualOpponentScore = currentMinOpponentScore;
						}
					}
					else
					{
						if (currentMinOpponentScore < minSuperiorOpponentScore)
						{
							minSuperiorOpponentScore = currentMinOpponentScore;
						}
					}
				}
				// If we can catch up, the rank is not stabilized yet
				if(maxPlayerScore >= minSuperiorOpponentScore
						|| maxPlayerScore > minEqualOpponentScore)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns whether the ant game is finished.
	 * An ant game is finished when: "lone survivor", "rank stabilized", or
	 * "turn limit reached".
	 * @return true if the game is finished, false otherwise.
	 */
	@Override
	public boolean isFinished()
	{
		// Turn limit rule
		if (super.isFinished())
		{
			replay.setCutoff("turn limit reached");
			return true;
		}
		// Alive player rules (extermination / lone survivor)
		int remainingPlayers = 0;
		for (BotGameInfo bgi : botInfos.values())
		{
			if(((AntBotGameInfo) bgi).isAlive())
			{
				remainingPlayers++;
			}
		}
		if (remainingPlayers == 0)
		{
			replay.setCutoff("extermination");
			return true;
		}
		if (remainingPlayers == 1)
		{
			replay.setCutoff("lone survivor");
			return true;
		}
		// Rank stabilization rule
		if (isRankStabilized())
		{
			replay.setCutoff("rank stabilized");
			return true;
		}
		return true;
	}
	
	/**
	 * Updates the game state of an ant game.
	 * Phases: attack, raze hills, spawn ants, gather food, spawn food.
	 */
	@Override
	public void update()
	{
		// Remove ants that died last round.
		for (Ant ant : ants)
		{
			if(ant.isDead())
			{
				removeAnt(ant);
			}
		}
		// Resolve battles.
		HashMap<Ant, ArrayList<Ant>> nearbyEnemies = new HashMap<Ant, ArrayList<Ant>>();
		for (Ant ant : ants)
		{
			Bot owner = ant.getBot();
			ArrayList<Ant> antEnemies = new ArrayList<Ant>();
			ArrayList<AntGameObject> attackable = map.applyMask(ant.getCol(),
					ant.getRow(), attackMask);
			for (AntGameObject obj : attackable)
			{
				if(obj instanceof Ant && ((Ant) obj).getBot() != owner)
				{
					antEnemies.add((Ant) obj);
				}
			}
			nearbyEnemies.put(ant, antEnemies);
		}
		for (Ant ant : ants)
		{
			ArrayList<Ant> enemies = nearbyEnemies.get(ant);
			int weakness = enemies.size();
			if(weakness == 0) continue;
			int minEnemyWeakness = Integer.MAX_VALUE;
			for (Ant enemy : enemies)
			{
				int enemyWeakness = nearbyEnemies.get(enemy).size(); 
				if(enemyWeakness < minEnemyWeakness)
				{
					minEnemyWeakness = enemyWeakness;
				}
			}
			if(minEnemyWeakness <= weakness)
			{
				ant.kill(curRound);
			}
		}
		// Raze hills & spawn ants.
		for (Map.Entry<Bot, BotGameInfo> entry : botInfos.entrySet())
		{
			Bot bot = entry.getKey();
			AntBotGameInfo botInfo = (AntBotGameInfo) entry.getValue();
			ArrayList<AntHill> availableHills = new ArrayList<AntHill>();
			// Raze hills.
			Iterator<AntHill> hillIt = botInfo.getHillIterator();
			while (hillIt.hasNext())
			{
				AntHill hill = hillIt.next();
				Ant razerAnt = map.getAntAt(hill.getCol(), hill.getRow());
				if (razerAnt != null)
				{
					if (razerAnt.getBot() == bot)
					{	// Both ant and hill belong to the bot.
						hill.setLastVisitRound(curRound);
						if (razerAnt.hasFood() == true)
						{	// If the ant holds a food unit, then increment the hive.
							botInfo.incrementHive();
							razerAnt.setFood(false);
						}
					}
					else
					{	// The ant does not belong to the bot that owns the hill, raze it.
						botInfo.removeHill(hill);
						map.removeGameObject(hill);
						// Update game score of both parties.
						AntBotGameInfo opponentInfo = (AntBotGameInfo)
								botInfos.get(razerAnt.getBot());
						opponentInfo.addGameScore(+2);
						botInfo.addGameScore(-1);
					}
				}
				else
				{
					// No ant is on this hill, it is available for an ant spawn.
					availableHills.add(hill);
				}
			}
			// Spawn ants.
			Collections.sort(availableHills);
			for (Iterator<AntHill> availIt = availableHills.iterator(); availIt.hasNext()
					&& botInfo.getHive() > 0;)
			{
				AntHill antHill = (AntHill) availIt.next();
				Ant ant = new Ant(antHill.getCol(), antHill.getRow(), bot,
						botInfo.getId(), curRound);
				addAnt(ant);
				botInfo.decrementHive();
			}
		}
		// Gather & spawn food.
		Iterator<AntFoodSpawn> foodSpawnIt = foodSpawns.iterator();
		while (foodSpawnIt.hasNext())
		{
			AntFoodSpawn foodSpawn = foodSpawnIt.next();
			if (foodSpawn.hasFood() == true)
			{	// Gather food.
				Ant gatherAnt = map.getAntAt(foodSpawn.getCol(), foodSpawn.getRow());
				if (gatherAnt != null && gatherAnt.hasFood() == false)
				{	// There's an ant that does not hold food on the spawn food.
					AntBotGameInfo gatherBotInfo = (AntBotGameInfo)
							botInfos.get(gatherAnt.getBot());
					foodSpawn.harvestFood(gatherBotInfo.getId(), curRound);
					gatherAnt.setFood(true);
				}
			}
			else
			{	// Spawn food.
				if (foodSpawn.hasFood() == false &&
						curRound - foodSpawn.getLastHarvestRound() > foodRespawnDelay)
				{	// Time to add food.
					foodSpawn.createFood(curRound);
					replay.addFoodData(foodSpawn.getReplayData());
				}
			}
		}
		// Detect ants that did not move during this round.
		for (Map.Entry<Bot, BotGameInfo> entry : botInfos.entrySet())
		{
			AntBotGameInfo botInfo = (AntBotGameInfo) entry.getValue();
			Iterator<Ant> antIt = botInfo.getAntIterator();
			while (antIt.hasNext())
			{
				Ant ant = antIt.next();
				// If the ant did not move during this round, just add a blank move in the
				// replay track.
				if (ant.hasMoved() == false) ant.addBlankMove();
				else ant.setMoved(false);
			}
		}
		curRound++;
	}
	
	/**
	 * Executes the ant moves ordered by a bot.
	 * @see Documentation/protocol/gameactions.html
	 * @param bot the bot which gave the moves.
	 * @param content the content of the "gameactions" message.
	 * @throws JSONException if the actions object is not correctly formed.
	 */
	@Override
	protected void executeActions(Bot bot, JSONObject content) throws JSONException
	{
		JSONArray moves = content.getJSONArray("moves");
		int len = moves.length();
		for (int i = 0; i < len; i++)
		{	// Extract action attributes.
			JSONArray move = moves.getJSONArray(i);
			int row = move.getInt(0);
			int col = move.getInt(1);
			Move direction = Move.fromString(move.getString(2));
			// Get game objects from the game map.
			Ant ant = map.getAntAt(col, row);
			if (ant != null && ant.getBot() == bot && ant.isDead() == false)
			{	// there's an alive ant at this cell and this ant belongs to the bot.
				// We can move it in the desired direction.
				ant.move(direction);
				// Check that the ant did not go into a wall.
				ArrayList<AntGameObject> gobs = map.getGameObjectsAt(ant.getCol(),
					ant.getRow());
				Iterator<AntGameObject> gobIt = gobs.iterator();
				AntBotGameInfo botInfo = (AntBotGameInfo) botInfos.get(bot);
				while (gobIt.hasNext())
				{
					AntGameObject gob = gobIt.next();
					if (gob.isCollideable())
					{	// The ant in on a wall, just remove it from the ant list of the
						// bot and from the map.
						botInfo.removeAnt(ant);
						map.removeGameObject(ant);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Generates the content of a "gamestate" message for a specific bot.
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestate" message.
	 * @see Documentation/protocol/gamestate.html
	 */
	@Override
	protected JSONObject genGameStateMessageContent(Bot bot)
	{
		JSONObject content = new JSONObject();
		// Get visible game objects.
		HashSet<AntGameObject> visibleGobs = new HashSet<AntGameObject>();
		AntBotGameInfo botInfo = (AntBotGameInfo) botInfos.get(bot);
		Iterator<Ant> antIt = botInfo.getAntIterator();
		while (antIt.hasNext())
		{
			Ant ant = antIt.next();
			if (ant.isDead() == false)
			{	// If the ant is alive, show all the game objects in its vision radius.
				visibleGobs.addAll(map.applyMask(ant.getCol(), ant.getRow(), viewMask));
			}
			else
			{	// The ant is dead, just notify the player it is dead.
				visibleGobs.add(ant);
			}
		}
		// Create the message.
		JSONArray gobs = new JSONArray();
		Iterator<AntGameObject> gobIt = visibleGobs.iterator();
		while (gobIt.hasNext())
		{
			AntGameObject gob = gobIt.next();
			JSONArray gobState;
			if (gob instanceof Ant)
			{	// The game object to send in an ant, we should add a visible id.
				Ant ant = (Ant) gob;
				gobState = ant.toJSONArray(botInfo.getBotId(ant.getBot()));
			}
			else if (gob instanceof AntHill)
			{	// The game object to send in a hill, we should add a visible id.
				AntHill hill = (AntHill) gob;
				gobState = hill.toJSONArray(botInfo.getBotId(hill.getBot()));
			}
			else
			{
				gobState = gob.toJSONArray();
				// Is null when the game object is a food spawn that has no food unit.
			}
			if (gobState != null) gobs.put(gobState);
		}
		try
		{
			content.put("gameobjects", gobs);
		}
		catch (JSONException e) {}
		return content;
	}
	
	@Override
	protected JSONObject genGameStartMessageContent(Bot bot)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected JSONObject genGameEndMessageContent(Bot bot)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
