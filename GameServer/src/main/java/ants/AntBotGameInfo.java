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
import java.util.Iterator;
import java.util.Map;

import basis.Bot;
import basis.BotGameInfo;

/**
 * This class holds informations about the ant game a bot is currently playing.
 * @class
 * @author Nicolas
 */
public class AntBotGameInfo extends BotGameInfo
{
	/**
	 * The bot identifier for this game.
	 */
	private int id;
	
	/**
	 * Contains the bot identifiers viewed by the bot.
	 * A bot always view himself as being the bot 0. Every time the bot encounters a bot
	 * it has not encountered before, a new identifier is given to it. This mechanism
	 * prevents the bot from knowing the number of bots currently playing in the game.
	 */
	private Map<Bot, Integer> relativeBotIds;
	
	/**
	 * The number of different bots encountered during the game.
	 * It's used to determine the next identifier in the relative bot identifiers list.
	 */
	private int nbrOfBotsEncountered;
	
	/**
	 * The list of ants that belong to the bot.
	 */
	private ArrayList<Ant> ants;
	
	/**
	 * Ths list of hills that belong to the bot.
	 */
	private ArrayList<AntHill> hills;
	
	/**
	 * The state a the hive. The hive is the number of ant to spawn from the hill(s).
	 */
	private int hive;
	
	/**
	 * Creates a new ant game state holder for a bot playing in this ant game.
	 * @constructor
	 * @param id the bot identifier for this game.
	 */
	public AntBotGameInfo(int id)
	{
		this.id = id;
		ants = new ArrayList<Ant>();
		hills = new ArrayList<AntHill>(1);
	}
	
	/**
	 * Initializes a ant game info structure.
	 * @param bot the bot associated with those info.
	 */
	public void init(Bot bot)
	{
		super.init();
		// Make sure everything is cleared.
		ants.clear();
		hills.clear();
		relativeBotIds.clear();
		// Make the bot viewing itself as being bot 0.
		relativeBotIds.put(bot, 0);
		nbrOfBotsEncountered = 0;
		hive = 0;
	}
	
	/**
	 * Gets the bot identifier for this game.
	 * @return the bot identifier for this game.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Gets the bot identifier viewed by the bot.
	 * @param bot the bot to identify.
	 * @return The identifier of the given bot.
	 */
	public int getBotId(Bot bot)
	{
		if (relativeBotIds.containsKey(bot))
		{	// Return the id of a previously encountered bot.
			return relativeBotIds.get(bot);
		}
		// If the bot has not been encountered yet, it is not present in the identifiers
		// map and hence it should be given an identifier.
		nbrOfBotsEncountered++;
		relativeBotIds.put(bot, nbrOfBotsEncountered);
		return nbrOfBotsEncountered;
	}
	
	/**
	 * Gets the number of alive ants that belong to the bot.
	 * @return the number of alive ants that belong to the bot.
	 */
	public int getAntCount()
	{
		int count = 0;
		Iterator<Ant> antIt = ants.iterator();
		while (antIt.hasNext())
		{
			if (antIt.next().isDead() == false) count++;
		}
		return count;
	}
	
	/**
	 * Gets an iterator on the list of ants of the bot.
	 * @return an iterator on the ants of the bot.
	 */
	public Iterator<Ant> getAntIterator()
	{
		return ants.iterator();
	}
	
	/**
	 * Gets an iterator on the list of hills of the bot.
	 * @return an iterator on the hills of the bot.
	 */
	public Iterator<AntHill> getHillIterator()
	{
		return hills.iterator();
	}
	
	/**
	 * Gets the number of ants awaiting to spawn.
	 * @return the number of ants in the hive.
	 */
	public int getHive()
	{
		return hive;
	}
	
	/**
	 * Adds an ant in the list of ants that belong to the bot.
	 * @param ant the ant to add to the ants list.
	 */
	public void addAnt(Ant ant)
	{
		ants.add(ant);
	}
	
	/**
	 * Removes an ant from the list of ants that belong to the bot.
	 * @param ant The ant to remove from the ants list.
	 */
	public void removeAnt(Ant ant)
	{
		ants.remove(ant);
	}
	
	/**
	 * Adds a hill in the list of hills that belong to the bot.
	 * @param hill the hill to add to the hills list.
	 */
	public void addHill(AntHill hill)
	{
		hills.add(hill);
	}
	
	/**
	 * Removes a hill from the list of hills that belong to the bot.
	 * @param ant The hill to remove from the hills list.
	 */
	public void removeHill(AntHill hill)
	{
		hills.remove(hill);
	}
	
	/**
	 * Increments the hive of a bot.
	 */
	public void incrementHive()
	{
		hive++;
	}
	
	/**
	 * Decrements the hive of a bot.
	 */
	public void decrementHive()
	{
		if (hive > 0) hive--;
	}
}
