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

package basis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

/**
 * This class is responsible for creating games when appropriated.
 * Here, "appropriated" means that bots should have relatively equitable scores and should
 * not wait for too long in the lobby.
 * @class
 * @author Jonathan, Juliette
 */
public class GameManager extends TimerTask
{
	/** The minimum number of bots in a created game. */
	public static int NB_PLAYERS_MIN;
	
	/** The maximum number of bots in a created game. */
	public static int NB_PLAYERS_MAX;
	
	private HashMap<Bot, Vector<Bot>> botMap;
	
	private Random rand = new Random();
	
	/**
	 * The handler used to notify the server when new games are created.
	 */
	protected GameHandler gameHandler;
	
	/**
	 * Creates a new game manager from a ranged number of bots.
	 * @constructor
	 * @param gameHandler the handler used to notify the server when games are created.
	 * @param nbPlayersMin the minimum number of bots in games to create.
	 * @param nbPlayersMin the maximum number of bots in games to create.
	 */
	public GameManager(GameHandler gameHandler, int nbPlayersMin, int nbPlayersMax)
	{
		this.gameHandler = gameHandler;
		botMap = new HashMap<>();
		
		if (nbPlayersMin > nbPlayersMax)
		{
			throw new IllegalArgumentException("nbPlayersMax shall be at least equal to"
					+ "nbPlayersMin");
		}

		NB_PLAYERS_MIN = nbPlayersMin;
		NB_PLAYERS_MAX = nbPlayersMax;
	}
	
	/**
	 * Creates a new game manager from a fixed number of bots.
	 * @constructor
	 * @param gameHandler the handler used to notify the server when games are created.
	 * @param nbPlayers the number of bots in games to create.
	 */
	public GameManager(GameHandler gameHandler, int nbPlayers)
	{
		this(gameHandler, nbPlayers, nbPlayers);
	}
	
	/**
	 * Creates a new game manager for 4-bot games.
	 * @constructor
	 * @param gameHandler the handler used to notify the server when games are created.
	 */
	public GameManager(GameHandler gameHandler)
	{
		this(gameHandler, 4);
	}
	
	/**
	 * Creates game for bots if appropriated.
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
		int nbPlayers = rand.nextInt(1 + NB_PLAYERS_MAX - NB_PLAYERS_MIN) +
				NB_PLAYERS_MIN;
		
		fillChallengers();
		ArrayList<ArrayList<Bot>> potentialMatchs = findCompatibleLists(nbPlayers);
		if (potentialMatchs != null)
		{	// Null pointer exception fixed.
			ArrayList<ArrayList<Bot>> fights = chooseFights(potentialMatchs);
			setBotsInFight(fights);
		}
	}
	
	/**
	 * Fill the possible challenger for a bot depending of its priority.
	 * It actually fills the vector of the bot HashMap.
	 */
	public void fillChallengers()
	{
		Set<Bot> botSet = botMap.keySet();
		for (Bot bot : botSet)
		{
			bot.incPriority();
			for (Bot canMatchBot : botSet)
			{
				if (!canMatchBot.equals(bot))
				{
					if ((canMatchBot.getScore() >= bot.getScore() - bot.getPriority())
							&& (canMatchBot.getScore() <= bot.getScore()
								+ bot.getPriority()))
					{
						botMap.get(bot).add(canMatchBot);
					}
				}
			}
		}
	}
	
	/**
	 * Function that chooses which fights should be played.
	 * It's based on the sum of the priority to the bots.
	 * It modifies the MatchsList so only chosen fights remain.
	 * @param matchsList the list of possible matchs.
	 * @return a list of matchs to do.
	 */
	public ArrayList<ArrayList<Bot>> chooseFights(ArrayList<ArrayList<Bot>> matchsList)
	{
		ArrayList<ArrayList<Bot>> toRet = new ArrayList<>();
		while(!isEachListUnique(matchsList)){
			int[] weight = new int[matchsList.size()];
			/*
			 * Setting a weight for each possible match
			 */
			for (int i=0; i<matchsList.size(); i++)
			{
				weight[i] = 0;
				for(Bot bot : matchsList.get(i))
				{
					weight[i] += bot.getPriority();
				}
			}
			/*
			 * Get the higher weight
			 */
			int compareWeight = -1;
			int index = -1;
			for (int i = 0; i < weight.length; i++)
			{
				if (weight[i] > compareWeight)
				{
					compareWeight = weight[i];
					index = i;
				}
			}
			
			/*
			 * Suppressing lists where bots chosen are in :
			 * it's not a feasible match
			 */
			ArrayList<Bot> listBots = matchsList.get(index);
			for(Bot b : listBots)
				for(int i = 0; i < matchsList.size(); i++)
					if(matchsList.get(i).contains(b))
					{
						matchsList.remove(i);
						i--;
					}
			toRet.add(listBots);
		}
		return toRet;
	}
	
	/**
	 * Set the bots in the list as fighting.
	 * Implies resetting the priority,
	 * removing them from the map
	 * and from the vector of the other bots,
	 * and set their status as inGame.
	 * @param toFightList a list of games to create/bots associations.
	 */
	public void setBotsInFight(ArrayList<ArrayList<Bot>> toFightList)
	{
		// Does nothing
		// You should create your own method for your own game.
	}
	
	/**
	 * Find the compatible lists in the bot map.
	 * (this means the matchs possibles)
	 * @param nbPlayers the number of player we want to make fight each other.
	 * @return a list of list of possible matchs, null if no match is possible.
	 */
	public ArrayList<ArrayList<Bot>> findCompatibleLists(int nbPlayers)
	{
		if (botMap.size() >= nbPlayers)
		{
			ArrayList<ArrayList<Bot>> toMatch = new ArrayList<>();
			
			for (Bot keyBot : botMap.keySet())
			{
				Vector<Bot> botVect = botMap.get(keyBot);
				
				if (botVect.size() >= nbPlayers-1)
				{
					int[] posToTest = new int[nbPlayers - 1];
					for (int i = 0; i < posToTest.length; i++)
						posToTest[i] = i;
					
					boolean isFinished = false;
					
					while (!isFinished)
					{
						boolean isPresent = true;
						for (int pos : posToTest)
						{
							// CAUTION : NULL POINTER EXCEPTION POSSIBLE
							// 			 IF A BOTVECT CONTAINS A BOT NOT IN HASHMAP KEYS
							
							isPresent &= botMap.get(
									botVect.elementAt(pos)).contains(keyBot);
							for (int p : posToTest)
								if (p != pos)
									isPresent &= botMap.get(
											botVect.elementAt(pos)).contains(
													botVect.elementAt(p));
						}
						if (isPresent)
						{
							ArrayList<Bot> toAdd = new ArrayList<>();
							toAdd.add(keyBot);
							for (int pos : posToTest)
							{
								toAdd.add(botVect.elementAt(pos));
							}
							
							if(!isListInMatrix(toAdd, toMatch))
								toMatch.add(toAdd);
						}
						isFinished = !incrementPosToTest(posToTest, botVect);
					}
				}
			}
			return toMatch;
		}
		
		return null;
	}
	
	/**
	 * Function to know if a list of bot is in a list of list of bot (in any order).
	 * @param child the list of bot.
	 * @param parent the list of list of bot.
	 * @return true if child is in parent, false otherwise.
	 */
	private boolean isListInMatrix(ArrayList<Bot> child, ArrayList<ArrayList<Bot>> parent)
	{
		boolean exists = false;
		for (ArrayList<Bot> CompParent : parent)
		{
			for (Bot childToCheck : child)
			{
				exists = CompParent.contains(childToCheck);
				if (!exists) break;
			}
			if (exists) return true;
		}
		return false;
	}
	
	/**
	 * Checks the uniqueness of each list in the bot matrix.
	 * @param botMatrix the list of list to check.
	 * @return true if each list is unique, false otherwise.
	 */
	private boolean isEachListUnique(ArrayList<ArrayList<Bot>> botMatrix)
	{
		for (ArrayList<Bot> botList : botMatrix)
		{
			for (ArrayList<Bot> botListToSearchIn : botMatrix)
			{
				if (!botListToSearchIn.equals(botList))
				{
					for (Bot bot : botList)
					{
						if (botListToSearchIn.contains(bot))
							return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Function that permit to update the values in posToTest,
	 * so it helps explore the tree.
	 * @param posToTest array to update.
	 * @param v a vector representing the tree.
	 * @return a boolean that indicates a success.
	 */
	public boolean incrementPosToTest(int posToTest[], Vector<Bot> v) 
			throws IndexOutOfBoundsException
	{
		if (posToTest.length > v.size())
			throw new IndexOutOfBoundsException(
					"posToTest ("+
							posToTest.length+
							") must be smaller than v ("+
							v.size()+
					")");
		
		for (int i = posToTest.length-1; i >= 0 ; i--)
		{
			if (posToTest[i] >= v.size())
				throw new IndexOutOfBoundsException(
						"posToTest["+i+"] ("+
								posToTest[i]+") must be smaller than v ("+v.size()+")");
			
			if (!v.elementAt(posToTest[i]).equals(v.elementAt(v.size() - posToTest.length + i)))
			{
				posToTest[i]++;
				for (int j=i+1; j < posToTest.length; j++)
				{
					posToTest[j] = posToTest[j-1] + 1;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Exports the bot map as a string.
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (Bot keyBot : botMap.keySet())
		{
			sb.append("[ "+keyBot.getNick()+" |");
			for (Bot bot : botMap.get(keyBot))
			{
				sb.append(" " + bot.getNick());
			}
			sb.append("]\n");
		}
		return sb.toString();
	}
	
	/**
	 * Function used to add a Bot as a key the bot map.
	 * Sets an empty Vector to this bot.
	 * @param bot the bot to add in the bot map.
	 */
	public void addBot(Bot bot)
	{
		if (!botMap.containsKey(bot))
		{
			botMap.put(bot, new Vector<Bot>());
		}
	}
	
	/**
	 * Function to remove a bot from the botMap and every occurrence in the vector of
	 * others bots.
	 * @param bot the bot to remove from the bot map.
	 * @return true if the bot was in the bot map.
	 */
	public boolean removeBot(Bot bot)
	{
		if (botMap.containsKey(bot))
		{
			bot.resetPriority();
			botMap.remove(bot);
			for (Bot key : botMap.keySet())
			{
				if (botMap.get(key).contains(bot))
				{
					botMap.get(key).remove(bot);
				}
			}
			return true;
		}
		return false;
	}
}
