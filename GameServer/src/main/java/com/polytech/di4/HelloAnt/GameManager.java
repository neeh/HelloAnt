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
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<ArrayList<Bot>> findCompatibleLists(HashMap<Bot, Vector<Bot>> botMap)
	{
		if(botMap.size()>=NB_PLAYERS)
		{
			ArrayList<ArrayList<Bot>> toMatch = new ArrayList<>();
			
			Set<Bot> botSet = botMap.keySet();
			for(Bot b : botSet)
			{
				Vector<Bot> botVect = botMap.get(b);
				
				if(botVect.size() >= NB_PLAYERS-1)
				{
					int[] posToTest = new int[NB_PLAYERS-1];
					for(int i=0; i<posToTest.length; i++)
					{
						posToTest[i]=i;
					}
					boolean isFinished = (posToTest[0] == botVect.size()-NB_PLAYERS+1);
					
					while(!isFinished)
					{
						boolean isPresent = true;
						for(int pos : posToTest)
						{
							
							// CAUTION : NULL POINTER EXCEPTION POSSIBLE
							// 			 IF A BOTVECT CONTAINS A BOT NOT IN HASHMAP KEYS
							
							isPresent &= botMap.get(botVect.elementAt(pos)).contains(b);
							for(int p : posToTest)
								if(p != pos)
									isPresent &= botMap.get(botVect.elementAt(pos)).contains(botVect.elementAt(p));	
							
						}
						if(isPresent)
						{
							ArrayList<Bot> toAdd = new ArrayList<>();
							toAdd.add(b);
							for(int pos : posToTest)
							{
								toAdd.add(botVect.elementAt(pos));
							}
							boolean notExists = true;
							for(ArrayList<Bot> Comparator : toMatch)
							{
								for(Bot toCheck : toAdd)
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
	 * Function that permit to update the values in posToTest, so it helps explore the tree
	 * @param posToTest array to update
	 * @param v a vector representing the tree
	 * @return a boolean that indicates a success
	 */
	public boolean incrementPosToTest(int posToTest[], Vector<Bot> v) throws IndexOutOfBoundsException
	{
		if (posToTest.length > v.size())
			throw new IndexOutOfBoundsException(
					"posToTest ("+posToTest.length+") must be smaller than v ("+v.size()+")");
		
		for (int i = posToTest.length-1; i>=0 ; i--)
		{
			if(posToTest[i] >= v.size())
				throw new IndexOutOfBoundsException(
						"posToTest["+i+"] ("+posToTest[i]+") must be smaller than v ("+v.size()+")");
			
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
	
	
	
	

}
