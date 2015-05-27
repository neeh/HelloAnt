package com;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Vector;

import basis.Bot;
import basis.GameManager;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GameManagerTest extends TestCase
{

	public GameManagerTest( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{ 
		return new TestSuite( GameManagerTest.class );
	}
	
	/**
	 * Test if the function incrementPosToTest, if the pos are already maxed, return the correct value
	 */
	public void testIncrementPosToTest_ImpossibleToIncrement(){
		GameManager gm = new GameManager(null);
		Vector<Bot> vectBot = new Vector<>();
		vectBot.add(new Bot(null, null, null, 0, null));
		vectBot.add(new Bot(null, null, null, 0, null));
		vectBot.add(new Bot(null, null, null, 0, null));
		int posToTest[] = new int[3];
		posToTest[0] = 0;
		posToTest[1] = 1;
		posToTest[2] = 2;
		int[] myClone = posToTest.clone();
		myClone[2]++;

		assertFalse(gm.incrementPosToTest(posToTest, vectBot));
	}
	
	/**
	 * Test if the function incrementPosToTest increments correctly the pos
	 */
	public void testIncrementPosToTest_IncrementOk()
	{
		GameManager gm = new GameManager(null);
		Vector<Bot> vectBot = new Vector<>();
		vectBot.add(new Bot(null, null, null, 0, null));
		vectBot.add(new Bot(null, null, null, 0, null));
		vectBot.add(new Bot(null, null, null, 0, null));
		vectBot.add(new Bot(null, null, null, 0, null));
		int posToTest[] = new int[3];
		posToTest[0] = 0;
		posToTest[1] = 1;
		posToTest[2] = 2;
		int[] myClone = posToTest.clone();
		myClone[2]++;

		
		gm.incrementPosToTest(posToTest, vectBot);
		assertEquals(myClone.length, posToTest.length);
		for(int i=0; i<posToTest.length; i++){
			assertEquals(myClone[i], posToTest[i]);
		}
	}

	/**
	 * Test if the function findCompatibleLists find the good lists
	 */
	public void testFindCompatibleLists()
	{
		GameManager gm = new GameManager(null);
		Bot a = new Bot(null, "A", null, 30, null);
		Bot b = new Bot(null, "B", null, 0, null);
		Bot c = new Bot(null, "C", null, 15, null);
		Bot d = new Bot(null, "D", null, 10, null);
		Bot e = new Bot(null, "E", null, 1, null);
		Bot f = new Bot(null, "F", null, 0, null);
		
		/* B, E and F are the only compatible if we look at score */
		
		gm.addBot(a);
		gm.addBot(b);
		gm.addBot(c);
		gm.addBot(d);
		gm.addBot(e);
		gm.addBot(f);
		
		gm.fillChallengers();
		
		ArrayList<ArrayList<Bot>> list = gm.findCompatibleLists(3);
		
		assertEquals(1, list.size());
		
		ArrayList<Bot> toCheck = list.get(0);
		assertEquals(3, toCheck.size());
		assertTrue(toCheck.contains(b));
		assertTrue(toCheck.contains(e));
		assertTrue(toCheck.contains(f));
		
		ArrayList<ArrayList<Bot>> fights = gm.chooseFights(list);
		
		gm.setBotsInFight(fights);
	}
	
	/**
	 * Test if the function testChooseFights returns the same list if send a list with a unique possibility
	 */
	public void testChooseFights_UniqueList()
	{
		Bot a = new Bot(null, "A", null, 0, null);
		Bot b = new Bot(null, "B", null, 0, null);
		Bot c = new Bot(null, "C", null, 0, null);
		
		ArrayList<ArrayList<Bot>> list = new ArrayList<>();
		ArrayList<Bot> toAdd = new ArrayList<>();
		toAdd.add(a);
		toAdd.add(b);
		toAdd.add(c);
		list.add(toAdd);
		
		GameManager gm = new GameManager(null);
		ArrayList<ArrayList<Bot>> fights = gm.chooseFights(list);
		
		assertEquals(list.size(), fights.size());
		assertEquals(1, fights.size());
		
		assertEquals(list.get(0).size(), fights.get(0).size());
		assertTrue(fights.get(0).contains(a));
		assertTrue(fights.get(0).contains(b));
		assertTrue(fights.get(0).contains(c));
	}
	
	/**
	 * Test the chooseFights function with a more complete approach (using priority and score)
	 */
	public void testChooseFights_Complete()
	{
		
		Bot a = new Bot(null, "A", null, 30, null);
		Bot b = new Bot(null, "B", null, 30, null);
		Bot c = new Bot(null, "C", null, 30, null);
		Bot d = new Bot(null, "D", null, 30, null);
		Bot e = new Bot(null, "E", null, 30, null);

		/*
		 * A, B and E should fight together
		 */
		a.incPriority();
		b.incPriority();
		e.incPriority();
		
		GameManager gm = new GameManager(null);
		gm.addBot(a);
		gm.addBot(b);
		gm.addBot(c);
		gm.addBot(d);
		gm.addBot(e);
		
		gm.fillChallengers();
		
		ArrayList<ArrayList<Bot>> list = gm.findCompatibleLists(3);
		ArrayList<ArrayList<Bot>> fights = gm.chooseFights(list);
		
		assertEquals(1, fights.size());
		
		assertEquals(3, fights.get(0).size());
		
		assertTrue(fights.get(0).contains(a));
		assertTrue(fights.get(0).contains(b));
		assertTrue(fights.get(0).contains(e));
	}
	
	/**
	 * Test the function isEachListUnique, shall return true with an effetively unique list
	 */
	public void testIsEachListUnique_Unique(){
		Bot a = new Bot(null, "A", null, 0, null);
		Bot b = new Bot(null, "B", null, 0, null);
		Bot c = new Bot(null, "C", null, 0, null);
		Bot d = new Bot(null, "D", null, 0, null);
		Bot e = new Bot(null, "E", null, 0, null);
		Bot f = new Bot(null, "F", null, 0, null);

		ArrayList<Bot> list1 = new ArrayList<>();
		list1.add(a);
		list1.add(b);
		list1.add(c);
		
		ArrayList<Bot> list2 = new ArrayList<>();
		list2.add(d);
		list2.add(e);
		list2.add(f);
		
		ArrayList<ArrayList<Bot>> toCheck = new ArrayList<>();
		toCheck.add(list1);
		toCheck.add(list2);
		
		GameManager gm = new GameManager(null);
		assertTrue(gm.isEachListUnique(toCheck));
	}
	
	/**
	 * Test the isEachListUnique function, shall return false when a bot is present in at least 2 lists
	 */
	public void testIsEachListUnique_NotUnique(){
		Bot a = new Bot(null, "A", null, 0, null);
		Bot b = new Bot(null, "B", null, 0, null);
		Bot c = new Bot(null, "C", null, 0, null);

		ArrayList<Bot> list1 = new ArrayList<>();
		list1.add(a);
		list1.add(b);
		list1.add(c);
		
		ArrayList<ArrayList<Bot>> toCheck = new ArrayList<>();
		toCheck.add(list1);
		toCheck.add(list1);
		
		
		GameManager gm = new GameManager(null);
		
		assertFalse(gm.isEachListUnique(toCheck));
	}
}
