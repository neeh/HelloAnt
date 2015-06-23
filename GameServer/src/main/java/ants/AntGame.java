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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Cell;
import util.Move;
import basis.Bot;
import basis.BotGameInfo;
import basis.BotMode;
import basis.Game;

/**
 * This class represents an ant game being played. It overloads the generic Game class to
 * implement a game of ants similar to ants.AIChallenge.
 * General game rules can be found on the ants.AIChalllenge web site.
 * @see http://ants.aichallenge.org/specification.php
 * @class
 * @author JMN
 */
public class AntGame extends Game
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AntGame.class);
	
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
	private int foodRespawnDelay;
	
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
	 * The game starting date.
	 */
	private Date startDate;
	
	/**
	 * Creates a new ant game from a map template and a list of bots playing in this game.
	 * Fake bots are created to play with the other bots if there is not enough bots.
	 * @constructor
	 * @param bots the list of bots that are gonna play in this game.
	 * @param maxRound the maximum number of round in this game.
	 * @param responseTime the maximum response time for a bot in ms.
	 * @param loadTime the time allowed to the bots to init their AI in ms.
	 * @param mapTemplate the template used to initialize the game map.
	 * @param foodRespawnDelay the number of turns between 2 food spawns.
	 * @param viewRadius2 the square of the view radius of an ant.
	 * @param attackRadius2 the square of the attack radius of an ant.
	 */
	public AntGame(ArrayList<Bot> bots, int maxRound, int responseTime, int loadTime,
			AntMapTemplate mapTemplate, int foodRespawnDelay, float viewRadius2,
			float attackRadius2)
	{
		this.startDate = new Date();
		this.mapTemplate = mapTemplate;
		// Create game map and game objects.
		map = new AntGameMap(mapTemplate.getCols(), mapTemplate.getRows());
		ants = new ArrayList<Ant>();
		foodSpawns = new ArrayList<AntFoodSpawn>();
		// Add fake bots to supply the correct bot count for the map.
		int fakeBotCount = mapTemplate.getBotCount() - bots.size();
		for (int i = 0; i < fakeBotCount; i++)
		{	// Create communicators and bots for fake bots.
			AntFakeCommunicator fakeCom = new AntFakeCommunicator(map);
			Bot fakeBot = new Bot(fakeCom, "CPU" + i, BotMode.TRAINING, 0, null);
			fakeCom.setBot(fakeBot);
			fakeBot.setGame(this);
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
		// Create fog war masks.
		viewMask = new AntGameMapMask(viewRadius2);
		attackMask = new AntGameMapMask(attackRadius2);
		// Init settings
		this.maxRound = maxRound;
		this.loadTimeMs = loadTime;
		this.responseTimeMs = responseTime;
		this.foodRespawnDelay = foodRespawnDelay;
		// Create replay object.
		replay = new AntGameReplayData(mapTemplate, viewRadius2, attackRadius2, maxRound,
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
	 * Removes an ant from the game, while being safe used in a loop.
	 * @param ant the ant to remove from the ants lists.
	 * @param antIt the iterator used in the loop (used for safe removal, must be an
	 * 		  iterator on the "ants" array).
	 */
	protected void removeAnt(Ant ant, Iterator<Ant> antIt)
	{
		antIt.remove();
		((AntBotGameInfo) botInfos.get(ant.getBot())).removeAnt(ant);
		map.removeGameObject(ant);
	}

	/**
	 * Adds a hill to the game.
	 * @param hill the hill to add to the hills lists.
	 */
	protected void addHill(AntHill hill)
	{
		replay.addHillData(hill.getReplayData());
		((AntBotGameInfo) botInfos.get(hill.getBot())).addHill(hill);
		map.addGameObject(hill);
	}
	
	/**
	 * Removes a hill from the game.
	 * @param hill the hill to remove from the hills lists.
	 */
	protected void removeHill(AntHill hill)
	{
		((AntBotGameInfo) botInfos.get(hill.getBot())).removeHill(hill);
		map.removeGameObject(hill);
	}
	
	/**
	 * Removes a will from the game, while being safe used in a loop.
	 * @param hill the hill to remove from the hills lists.
	 * @param hillIt the iterator used in the loop (have to be from
	 *        {@code botInfo.getHillIterator()}).
	 */
	protected void removeHill(AntHill hill, Iterator<AntHill> hillIt)
	{
		hillIt.remove();
		map.removeGameObject(hill);
	}
	
	/**
	 * Initializes an ant game.
	 */
	@Override
	public void init()
	{
		// Initialize bot game info for each bot.
		for (Map.Entry<Bot, BotGameInfo> entry : botInfos.entrySet())
		{
			((AntBotGameInfo) entry.getValue()).init(entry.getKey());
		}
		// Clears the map.
		map.clear();
		// Create walls on the map.
		Iterator<Cell> wallIt = mapTemplate.getWallIterator();
		while (wallIt.hasNext())
		{
			map.addGameObject(new AntWall(map, wallIt.next()));
		}
		// Create food spawns on the map.
		foodSpawns.clear();
		Iterator<Cell> foodSpawnIt = mapTemplate.getFoodSpawnIterator();
		while (foodSpawnIt.hasNext())
		{
			AntFoodSpawn foodSpawn = new AntFoodSpawn(map, foodSpawnIt.next());
			foodSpawns.add(foodSpawn);
			map.addGameObject(foodSpawn);
			// Pop initial food.
			foodSpawn.createFood(curRound);
			replay.addFoodData(foodSpawn.getReplayData());
		}
		// Create initial ant(s) and hill(s) for each bot on the map.
		Iterator<ArrayList<Cell>> botHillsIt = mapTemplate.getHillIterator();
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
				AntHill hill = new AntHill(map, hillCell, bot, botInfo.getId());
				addHill(hill);
				// Create an initial ant inside the created hill for the bot.
				Ant ant = new Ant(map, hillCell, bot, botInfo.getId(), curRound);
				addAnt(ant);
				// Each bot start with 1 point per hill.
				botInfo.addGameScore(+1);
			}
		}
		//map._DEBUG_print_map();
	}
	
	/**
	 * Terminates the ant game and cleans it.
	 */
	@Override
	public void terminate()
	{
		// TODO : Bonus points
		// (i.e. remaining ants get points for the remaining hills)
		// Kill remaining ants
		for (Ant ant : ants)
		{
			ant.kill(curRound + 1);
		}
		// Clear reamining food
		for (AntFoodSpawn foodSpawn : foodSpawns)
		{
			foodSpawn.cleanFood(curRound + 1);
		}
		// Set bot end_turn and raze remaining hills
		ArrayList<AntBotGameInfo> rankedList = new ArrayList<AntBotGameInfo>();
		for (BotGameInfo botInfo : botInfos.values())
		{
			AntBotGameInfo antBotInfo = (AntBotGameInfo) botInfo;
			rankedList.add(antBotInfo);
			if (antBotInfo.getDeathTurn() == -1)
			{
				antBotInfo.setDeath(curRound);
			}
			for (Iterator<AntHill> iterator = antBotInfo.getHillIterator();
					iterator.hasNext();)
			{
				AntHill hill = iterator.next();
				hill.raze(curRound + 1);
			}
		}
		
		//Compute bots ranks
		Collections.sort(rankedList);
		int currentRank = 0;
		int previousScore = -1;
		for (int i = 0; i < rankedList.size(); i++)
		{
			AntBotGameInfo botInfo = rankedList.get(i);
			int score = botInfo.getGameScore();
			if (score != previousScore)
			{
				currentRank = i;
				previousScore = score;
			}
			botInfo.setRank(currentRank);
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
				if (maxPlayerScore >= minSuperiorOpponentScore
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
		//if (curRound >= 100) return true;
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
			if (((AntBotGameInfo) bgi).isAlive())
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
		return false;
	}
	
	/**
	 * Updates the game state of an ant game.
	 * Phases: attack, raze hills, spawn ants, gather food, spawn food.
	 */
	@Override
	public void update()
	{
		curRound++;
		// Save hives and scores state
		for (BotGameInfo botInfo : botInfos.values())
		{
			AntBotGameInfo antBotInfo = (AntBotGameInfo) botInfo;
			replay.addHiveHistoryRecord(antBotInfo.getId(), antBotInfo.getHive());
			replay.addScoresRecord(antBotInfo.getId(), antBotInfo.getGameScore());
		}
		for (Ant ant : ants)
		{
			// Check that the ant did not go into a wall (or another ant).
			ArrayList<AntGameObject> gobs = map.getGameObjectsAt(ant);
			Iterator<AntGameObject> gobIt = gobs.iterator();
			while (gobIt.hasNext())
			{
				AntGameObject gob = gobIt.next();
				if (gob != ant && gob.isCollideable())
				{	// The ant in on a wall, kill it instantly.
					ant.kill();
					break;
				}
			}
		}
		// Remove ants which died last round.
		for (Iterator<Ant> antIt = ants.iterator(); antIt.hasNext();)
		{
			Ant ant = antIt.next();
			if (ant.isDead())
			{
				// Avoid random ConcurrentModificationException by removing using the
				// iterator
				removeAnt(ant, antIt);
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
				if (obj instanceof Ant && ((Ant) obj).getBot() != owner)
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
			if (weakness == 0) continue;
			int minEnemyWeakness = Integer.MAX_VALUE;
			for (Ant enemy : enemies)
			{
				int enemyWeakness = nearbyEnemies.get(enemy).size(); 
				if (enemyWeakness < minEnemyWeakness)
				{
					minEnemyWeakness = enemyWeakness;
				}
			}
			if (minEnemyWeakness <= weakness)
			{
				ant.kill();
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
						if (razerAnt.hasFood())
						{	// If the ant holds a food unit, then increment the hive.
							botInfo.incrementHive();
							razerAnt.setFood(false);
						}
					}
					else
					{	// The ant does not belong to the bot that owns the hill, raze it.
						hill.raze(curRound);
						removeHill(hill, hillIt);
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
				Ant ant = new Ant(map, antHill.getCol(), antHill.getRow(), bot,
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
			if (foodSpawn.hasFood())
			{	// Gather food.
				Ant gatherAnt = map.getAntAt(foodSpawn.getCol(), foodSpawn.getRow());
				if (gatherAnt != null && !gatherAnt.hasFood())
				{	// There's an ant that does not hold food on the spawn food.
					AntBotGameInfo gatherBotInfo = (AntBotGameInfo)
							botInfos.get(gatherAnt.getBot());
					foodSpawn.harvestFood(gatherBotInfo.getId(), curRound);
					gatherAnt.setFood(true);
				}
			}
			else
			{	// Spawn food.
				if (!foodSpawn.hasFood() &&
						curRound - foodSpawn.getLastHarvestRound() > foodRespawnDelay)
				{	// Time to add food.
					foodSpawn.createFood(curRound);
					replay.addFoodData(foodSpawn.getReplayData());
				}
			}
		}
		// Detect ants that did not move during this round.
		Iterator<Ant> antIt = ants.iterator();
		while (antIt.hasNext())
		{
			Ant ant = antIt.next();
			if (!ant.isDead() && !ant.hasMoved())
			{	// If an alive ant did not move during this round, just add a blank
				// move in its replay track.
				ant.addBlankMove();
			}
			else
			{	// Enable the ant to move again
				ant.setMoved(false);
			}
		}
		// Detect dead bots
		for (Map.Entry<Bot, BotGameInfo> entry : botInfos.entrySet())
		{
			AntBotGameInfo botInfo = (AntBotGameInfo) entry.getValue();
			if (!botInfo.isAlive())
			{
				botInfo.setDeath(curRound, "eliminated");
			}
		}
	}
	
	/**
	 * Mutes a bot in this game and sends it a "gamemute" message.
	 * @see Documentation/protocol/gamemute.html
	 * @param bot the bot to mute.
	 * @param reason a message explaining why the bot was muted.
	 */
	@Override
	public void muteBot(Bot bot, String reason)
	{
		((AntBotGameInfo) botInfos.get(bot)).setDeath(curRound + 1, "timeout");
		super.muteBot(bot, reason);
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
			if (ant != null && ant.getBot() == bot && !ant.hasMoved())
			{	// there's an alive ant at this cell and this ant belongs to the bot.
				// We can move it in the desired direction.
				ant.move(direction);
				// We can't check here if the ant walked into a wall or another ant
				// Since not every ant has moved.
			}
		}
	}
	
	/**
	 * Generates the content of a "gamestate" message for a specific bot.
	 * @see Documentation/protocol/gamestate.html
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestate" message.
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
			if (!ant.isDead())
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
		catch (JSONException e)
		{
			LOGGER.error("Error generating game state message content ({})", e.getMessage());
		}
		return content;
	}
	
	/**
	 * Generates the content of a "gamestart" message for a specific bot.
	 * @see Documentation/protocol/gamestart.html
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gamestart" message.
	 */
	@Override
	protected JSONObject genGameStartMessageContent(Bot bot)
	{
		try
		{
			JSONObject jsonContent = new JSONObject();
			JSONObject jsonMap = new JSONObject();
			jsonMap.put("cols", map.getCols());
			jsonMap.put("rows", map.getRows());
			AntBotGameInfo botInfo = (AntBotGameInfo) botInfos.get(bot);
			for (Iterator<AntHill> iterator = botInfo.getHillIterator();
					iterator.hasNext();)
			{
				AntHill hill = iterator.next();
				JSONObject jsonHill = new JSONObject();
				jsonHill.put("col", hill.getCol());
				jsonHill.put("row", hill.getRow());
				jsonMap.append("hills", jsonHill);
			}
			jsonContent.put("map", jsonMap);
			return jsonContent;
		}
		catch (JSONException e)
		{
			LOGGER.error("Could not generate the game start message content.");
			return null;
		}
	}
	
	/**
	 * Generates the content of a "gameend" message for a specific bot.
	 * @see Documentation/protocol/gameend.html
	 * @param bot the bot that will receive the message.
	 * @return the content of the "gameend" message.
	 */
	@Override
	protected JSONObject genGameEndMessageContent(Bot bot)
	{
		try
		{
			JSONObject jsonContent = new JSONObject();
			JSONObject jsonReplay = new JSONObject();
			jsonReplay.put("replaydata", replay.toJSONObject());
			JSONArray jsonStatus = new JSONArray();
			JSONArray jsonRank = new JSONArray();
			JSONArray jsonScore = new JSONArray();
			JSONArray jsonPlayerTurns = new JSONArray();
			JSONArray jsonPlayerNames = new JSONArray();
			JSONArray jsonSubmissionIds = new JSONArray();
			JSONArray jsonUserIds = new JSONArray();
			JSONArray jsonChallengeRank = new JSONArray();
			JSONArray jsonChallengeSkill = new JSONArray();
			for (Entry<Bot, BotGameInfo> entry : botInfos.entrySet())
			{
				Bot b = entry.getKey();
				AntBotGameInfo botInfo = (AntBotGameInfo) entry.getValue();
				int id = botInfo.getId();
				jsonStatus.put(id, botInfo.getDeathReason());
				jsonRank.put(id, botInfo.getRank());
				jsonPlayerTurns.put(id, botInfo.getDeathTurn());
				jsonScore.put(id, botInfo.getGameScore());
				jsonPlayerNames.put(id, b.getNick());
				jsonSubmissionIds.put(id, "0");
				jsonUserIds.put(id, "0");
				jsonChallengeRank.put(id, "0");
				jsonChallengeSkill.put(id, "0");
			}
			jsonReplay.put("status", jsonStatus);
			jsonReplay.put("rank", jsonRank);
			jsonReplay.put("playerturns", jsonPlayerTurns);
			jsonReplay.put("score", jsonScore);
			jsonReplay.put("playernames", jsonPlayerNames);
			jsonReplay.put("submission_ids", jsonSubmissionIds);
			jsonReplay.put("user_ids", jsonUserIds);
			jsonReplay.put("challenge_rank", jsonChallengeRank);
			jsonReplay.put("challenge_skill", jsonChallengeSkill);
			
			jsonReplay.put("post_id", 0);
			jsonReplay.put("matchup_id", 0);
			jsonReplay.put("challenge", "ants");
			jsonReplay.put("replayformat", "json");
			jsonReplay.put("location", "Polytech");
			jsonReplay.put("game_length", curRound);
			jsonReplay.put("user_url", "about:blank");
			jsonReplay.put("game_url", "about:blank");
			SimpleDateFormat dateFormat =
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			jsonReplay.put("date", dateFormat.format(startDate));
			jsonReplay.put("game_id", 0);
			jsonReplay.put("worker_id", 0);
			
			jsonContent.put("replay", jsonReplay);
			return jsonContent;
		}
		catch (JSONException e)
		{
			LOGGER.error("Could not generate the game end message content.");
			return null;
		}
	}
}
