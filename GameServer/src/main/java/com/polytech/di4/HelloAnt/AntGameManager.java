package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is responsible for creating ant games when appropriated.
 * Here, "appropriated" means that bots should have relatively equitable scores and should
 * not wait for too long in the lobby.
 * @class
 * @author Jonathan, Juliette
 */
public class AntGameManager extends GameManager
{
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
		int sizeMapList = maps.size();
		
		AntMapTemplate map = maps.get(rand.nextInt(sizeMapList));
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
	 * Set the bots in the list as fighting.
	 * Implies resetting the priority,
	 * removing them from the map
	 * and from the vector of the other bots,
	 * and set their status as inGame.
	 * @param toFightList a list of games to create/bots associations.
	 * @param map the map on which the bots will fight.
	 */
	public void setBotsInFight(ArrayList<ArrayList<Bot>> toFightList, AntMapTemplate map)
	{
		for (ArrayList<Bot> botsInGame : toFightList)
		{	// For each game to create...
			AntGame game = new AntGame(botsInGame, map);
			for (Bot bot : botsInGame)
			{	// For each bot in the game...
				bot.setGame(game);
				removeBot(bot);
			}
			gameHandler.handleGameCreated(game);
		}
	}
}
