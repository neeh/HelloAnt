package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.management.loading.MLet;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;


public class GameManager implements Runnable
{
	public static int NB_PLAYERS = 4;
	private HashMap<Bot, Vector<Bot>> botMap;

	public GameManager(){
		botMap = new HashMap<>();
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Fill the possible challenger for a bot
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
							&& (canMatchBot.getScore() <= bot.getScore()+bot.getPriority()))
					{
						botMap.get(bot).add(canMatchBot);
					}
				}
			}
		}
	}

	/**
	 * Function that chooses which fights should be played
	 * It's based on the sum of the priority to the bots
	 * It modifies the MatchsList so only chosen fights remain
	 * @param matchsList the list of possible matchs
	 * @return A list of matchs to do
	 */
	public ArrayList<ArrayList<Bot>> chooseFights(ArrayList<ArrayList<Bot>> matchsList)
	{
		ArrayList<ArrayList<Bot>> toRet = new ArrayList<>();
		while(!areEachListUnique(matchsList)){
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
			for (int i=0; i<weight.length; i++)
			{
				if(weight[i]>compareWeight)
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
				for(int i=0; i<matchsList.size(); i++)
					if(matchsList.get(i).contains(b)){
						matchsList.remove(i);
						i--;
					}
			toRet.add(listBots);
		}
		return toRet;
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
				//				bot.setGame(game);
				removeBot(bot);
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

			for (Bot keyBot : botMap.keySet())
			{
				Vector<Bot> botVect = botMap.get(keyBot);

				if (botVect.size() >= nbPlayers-1)
				{
					int[] posToTest = new int[nbPlayers-1];
					for (int i=0; i<posToTest.length; i++)
						posToTest[i]=i;

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
	 * Function to know if a list of bot is in a list of list of bot (in any order)
	 * @param child the list of bot
	 * @param parent the list of list of bot
	 * @return true if child is in parent, false otherwise
	 */
	private boolean isListInMatrix(ArrayList<Bot> child, ArrayList<ArrayList<Bot>> parent)
	{
		boolean exists = false;
		for (ArrayList<Bot> CompParent : parent)
		{
			for (Bot childToCheck : child)
			{
				exists = CompParent.contains(childToCheck);
				if(!exists) break;
			}
			if(exists) return true;
		}
		return false;
	}

	private boolean areEachListUnique(ArrayList<ArrayList<Bot>> botMatrix)
	{
		for (ArrayList<Bot> botList : botMatrix)
		{
			for(ArrayList<Bot> botListToSearchIn : botMatrix)
			{
				if(!botListToSearchIn.equals(botList))
				{
					System.out.println("HI");
					for (Bot bot : botList)
					{
						if(botListToSearchIn.contains(bot))
							return false;
					}
				}
			}
		}
		return true;
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

	@Override
	/**
	 * Displays the botMap
	 */
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
