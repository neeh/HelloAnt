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
import java.util.Random;

import basis.Bot;
import basis.BotMode;
import basis.GameHandler;
import basis.GameManager;

/**
 * This class is responsible for creating ant games when appropriated.
 * Here, "appropriated" means that bots should have relatively equitable scores and should
 * not wait for too long in the lobby.
 * @class
 * @author Jonathan, Juliette
 */
public class AntGameManager extends GameManager
{
	/** The number of rounds required for food to respawn. */
	private static final int FOOD_RESPAWN_DELAY = 15;
	
	/** The squared radius of the view mask. */
	private static final float VIEW_RADIUS_2 = 77.0f;
	
	/** The squared radius of the attack mask. */
	private static final float ATTACK_RADIUS_2 = 5.0f;
	
	/**
	 * The list of playable map templates.
	 */
	private ArrayList<AntMapTemplate> maps;
	
	private Random rand = new Random();
	
	/**
	 * Creates a new game manager for an ant game.
	 * @constructor
	 * @param gameHandler the handler used to notify the server when games are created.
	 * @param maps a list of playable maps.
	 */
	public AntGameManager(GameHandler gameHandler, ArrayList<AntMapTemplate> maps)
	{
		super(gameHandler);
		this.maps = maps;
	}
	
	/**
	 * Creates ant game for bots if appropriated.
	 * { @code
	 *   import java.util.timer;
	 *   Timer timer = new Timer();
	 *   long whenToStart = 0;
	 *   long interval = 3000;
	 *   timer.scheduleAtFixedRate(gameManager, whenToStart, interval); }
	 */
	@Override
	public void run()
	{
		int nbPlayers;
		
		AntMapTemplate map = maps.get(rand.nextInt(maps.size()));
		nbPlayers = map.getBotCount();
		
		fillChallengers();
		ArrayList<ArrayList<Bot>> potentialMatchs = findCompatibleLists(nbPlayers);
		if (potentialMatchs != null)
		{	// Null pointer exception fixed.
			ArrayList<ArrayList<Bot>> fights = chooseFights(potentialMatchs);
			setBotsInFight(fights, map);
		}
	}
	
	/**
	 * Sets the bots in the list as fighting.
	 * Implies resetting the priority,
	 * removing them from the map and from the vector of the other bots,
	 * and setting their status as inGame.
	 * @param toFightList a list of games to create/bots associations.
	 * @param map the map on which the bots will fight.
	 */
	public void setBotsInFight(ArrayList<ArrayList<Bot>> toFightList, AntMapTemplate map)
	{
		for (ArrayList<Bot> botsInGame : toFightList)
		{	// For each game to create...
			AntGame game = new AntGame(botsInGame, MAX_ROUND, RESPONSE_TIME_MS, LOAD_TIME_MS, map,
					FOOD_RESPAWN_DELAY, VIEW_RADIUS_2, ATTACK_RADIUS_2);
			for (Bot bot : botsInGame)
			{	// For each bot in the game...
				bot.setGame(game);
				removeBot(bot);
			}
			gameHandler.addGame(game);
		}
	}
	
	/**
	 * Adds a bot in the game manager specific to the game of ants.
	 * If the bot is in training mode, the game manager immediately creates a game for it.
	 * @param the bot to add in the game manager lobby.
	 */
	public void addBot(Bot bot)
	{
		if (bot.getMode() == BotMode.TRAINING)
		{	// ---------------------------------------------------------------------------
			// Could be nice to have a small delay, to let the user some time to logout
			//    between 2 games (for example put this in a TimerTask)
			// ---------------------------------------------------------------------------
			// Take a random map.
			AntMapTemplate map = maps.get(rand.nextInt(maps.size()));
			int botCount = map.getBotCount();
			// Create the bot list and add the bot in training mode.
			ArrayList<Bot> bots = new ArrayList<Bot>(botCount);
			bots.add(bot);
			// Create the ant game and run it on the server.
			AntGame game = new AntGame(bots, MAX_ROUND, RESPONSE_TIME_MS, LOAD_TIME_MS, map,
					FOOD_RESPAWN_DELAY, VIEW_RADIUS_2, ATTACK_RADIUS_2);
			bot.setGame(game);
			gameHandler.addGame(game);
		}
		else
		{	// Let the generic game manager do the work.
			super.addBot(bot);
		}
	}
}
