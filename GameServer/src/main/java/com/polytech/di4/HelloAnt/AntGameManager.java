package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.Random;

public class AntGameManager extends GameManager
{
	private ArrayList<AntMapTemplate> maps;
	private Random rand = new Random();
	
	public AntGameManager(ArrayList<AntMapTemplate> maps)
	{
		this.maps = maps;
	}
	
	public void run()
	{
		int nbPlayers;
		int sizeMapList = maps.size();
		
		AntMapTemplate map = maps.get(rand.nextInt(sizeMapList));
		nbPlayers = map.getBotCount();
		
		fillChallengers();
		ArrayList<ArrayList<Bot>> potentialMatchs = findCompatibleLists(nbPlayers);
		ArrayList<ArrayList<Bot>> fights = chooseFights(potentialMatchs);
		setBotsInFight(fights, map);
	}
	


	/**
	 * Set the bots in the list as fighting
	 * implies resetting the priority,
	 * removing them from the map
	 * and from the vector of the other bots,
	 * and set their status as inGame.
	 * @param toFightList the list of bots who will fight
	 * @param map the Map on which the bots will fight
	 */
	@Override
	public void setBotsInFight(ArrayList<ArrayList<Bot>> toFightList, AntMapTemplate map)
	{
		for (ArrayList<Bot> list : toFightList)
		{
			for (Bot bot : list)
			{
				// TODO	bot.setGame(game);
				removeBot(bot);
			}
		}
		// TODO LAUNCH THE GAME ON THE MAP HERE
	}
}
