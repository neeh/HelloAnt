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
		ArrayList<ArrayList<Bot>> toMatch = new ArrayList<>();
		
		Set<Bot> botSet = botMap.keySet();
		for(Bot b : botSet)
		{
			System.out.println("Itération sur le bot nommé " + b.getNick());
			Vector<Bot> botVect = botMap.get(b);
			
			int[] posToTest = new int[NB_PLAYERS];
			for(int i=0; i<posToTest.length; i++)
			{
				posToTest[i]=i;
			}
			boolean isFinished = (posToTest[0] == botVect.size()-NB_PLAYERS);
			System.out.println("FINI ? " + (isFinished));
			
			while(!isFinished)
			{
				boolean isPresent = true;
				for(int pos : posToTest)
				{
					isPresent = (isPresent && botMap.get(botVect.elementAt(pos)).contains(botVect.elementAt(pos)));
					System.out.println("Test présence " + botVect.elementAt(pos).getNick() + " : " + isPresent);
				}
				if(isPresent)
				{
					ArrayList<Bot> toAdd = new ArrayList<>();
					for(int pos : posToTest)
					{
						toAdd.add(botVect.elementAt(pos));
					}
					if(!toMatch.contains(toAdd))
						toMatch.add(toAdd);
				}
				isFinished = !incrementPosToTest(posToTest, botVect);
				System.out.println("FINI ? " + (isFinished));
			}
		}
		return toMatch;
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
