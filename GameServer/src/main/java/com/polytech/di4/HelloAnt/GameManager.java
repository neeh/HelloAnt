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
	
private ArrayList<ArrayList<Bot>> findCompatibleLists(HashMap<Bot, Vector<Bot>> botMap)
	{
		ArrayList<ArrayList<Bot>> toMatch = new ArrayList<>();
		
		Set<Bot> botSet = botMap.keySet();
		for(Bot b : botSet)
		{
			Vector<Bot> botVect = botMap.get(b);
			
			int[] posToTest = new int[NB_PLAYERS-1];
			for(int i=0; i<posToTest.length; i++)
			{
				posToTest[i]=i;
			}
			boolean isFinished = (posToTest[0] == botVect.size()-NB_PLAYERS+1);
			
			while(!isFinished)
			{
				boolean isPresent = false;
				for(int pos : posToTest)
				{
					isPresent = (isPresent &&  botMap.get(botVect.elementAt(pos)).contains(botVect.elementAt(pos)));
				}
				if(isPresent){
					ArrayList<Bot> toAdd = new ArrayList<>();
					for(int pos : posToTest)
					{
						toAdd.add(botVect.elementAt(pos));
					}
					toMatch.add(toAdd);
				}
				incrementPosToTest(posToTest, botVect);
			}
		}
		return toMatch;
	}

	/**
	 * Function that permit to update the values in posToTest, so it helps explore the tree
	 * @param posToTest array to update
	 * @param size the size of the tree
	 */
	private void incrementPosToTest(int posToTest[], Vector<Bot> v)
	{
		
	}
	
	
	
	

}
