package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;


public class GameManager implements Runnable
{
	public static int NB_PLAYERS = 4;
	private HashMap<Bot, Vector<Bot>> botMap;
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Fill the possible challenger for a bit
	 * depending of its priority
	 * It actually fills the vector of the bot HashMap
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
					if ((canMatchBot.getScore() >= bot.getScore()-bot.getPriority())
						|| (canMatchBot.getScore() <= bot.getScore()+bot.getPriority()))
					{
						botMap.get(bot).add(canMatchBot);
					}
				}
			}
		}
	}
	
	/**
	 * Set the bots in the list as fighting
	 * implies resetting the priority,
	 * removing them from the map
	 * and from the vector of the other bots,
	 * and set their status as inGame.
	 * @param toFightList the list of bots who will fight
	 */
	public void setBotsInFight(ArrayList<ArrayList<Bot>> toFightList)
	{
		for (ArrayList<Bot> list : toFightList)
		{
			for (Bot bot : list)
			{
				botMap.remove(bot);
				bot.resetPriority();
//				bot.setGame(game);
				Set<Bot> botsInMap = botMap.keySet();
				for (Bot botInMap : botsInMap)
				{
					botMap.get(botInMap).remove(bot);
				}
			}
		}
	}
	
	/**
	 * Find the compatible lists in the botMap
	 * this means the matchs possibles
	 * @param nbPlayers the number of player we want to make fight each other
	 * @return a List of List of possible matchs
	 */
	public ArrayList<ArrayList<Bot>> findCompatibleLists(int nbPlayers)
	{
		if (botMap.size()>=nbPlayers)
		{
			ArrayList<ArrayList<Bot>> toMatch = new ArrayList<>();
			
			Set<Bot> botSet = botMap.keySet();
			for (Bot bot : botSet)
			{
				Vector<Bot> botVect = botMap.get(bot);
				
				if (botVect.size() >= nbPlayers-1)
				{
					int[] posToTest = new int[nbPlayers-1];
					for (int i=0; i<posToTest.length; i++)
					{
						posToTest[i]=i;
					}
					boolean isFinished = (posToTest[0] == botVect.size()-nbPlayers+1);
					
					while (!isFinished)
					{
						boolean isPresent = true;
						for (int pos : posToTest)
						{
							
							// CAUTION : NULL POINTER EXCEPTION POSSIBLE
							// 			 IF A BOTVECT CONTAINS A BOT NOT IN HASHMAP KEYS
							
							isPresent &= botMap.get(botVect.elementAt(pos)).contains(bot);
							for (int p : posToTest)
								if (p != pos)
									isPresent &= botMap.get(
											botVect.elementAt(pos)).contains(
													botVect.elementAt(p));	
							
						}
						if (isPresent)
						{
							ArrayList<Bot> toAdd = new ArrayList<>();
							toAdd.add(bot);
							for (int pos : posToTest)
							{
								toAdd.add(botVect.elementAt(pos));
							}
							boolean notExists = true;
							for (ArrayList<Bot> Comparator : toMatch)
							{
								for (Bot toCheck : toAdd)
								{
									notExists &= !Comparator.contains(toCheck);
								}
							}
							if(notExists)
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
	 * Function that permit to update the values in posToTest,
	 * so it helps explore the tree
	 * @param posToTest array to update
	 * @param v a vector representing the tree
	 * @return a boolean that indicates a success
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
		
		for (int i = posToTest.length-1; i>=0 ; i--)
		{
			if (posToTest[i] >= v.size())
				throw new IndexOutOfBoundsException(
						"posToTest["+i+"] ("+
						posToTest[i]+") must be smaller than v ("+v.size()+")");
			
			if (v.elementAt(posToTest[i]) != v.elementAt(v.size() - posToTest.length + i))
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
	 * Function used to add a Bot as a key the HashMap
	 * Sets an empty Vector to this bot.
	 * @param bot the bot to add
	 */
	public void addBot(Bot bot)
	{
		if (!botMap.containsKey(bot))
		{
			botMap.put(bot, new Vector<Bot>());
		}
	}
	
	/**
	 * Function to remove a bot from the botMap
	 * and each occurence in the vector of others bots
	 * @param bot the bot to remove
	 * @return whether true of false depending if the bot was in the map or not
	 */
	public boolean removeBot(Bot bot)
	{
		if (botMap.containsKey(bot))
		{
			bot.resetPriority();
			botMap.remove(bot);
			for (Bot key : botMap.keySet())
			{
				if (botMap.get(key).contains(bot)){
					botMap.get(key).remove(bot);
				}
			}
			return true;
		}
		return false;
	}
	

}
