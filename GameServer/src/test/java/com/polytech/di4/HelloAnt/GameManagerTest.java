package com.polytech.di4.HelloAnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.lang.Object;

import com.mysql.jdbc.AssertionFailedException;

import junit.framework.Assert;
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


	public void testIncrementPosToTest()
	{
		GameManager gm = new GameManager();
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
		try{
			assertFalse(gm.incrementPosToTest(posToTest, vectBot));
		}
		catch (AssertionFailedError e){
			System.err.println("Echec : ");
			System.err.println("Longueur : " + myClone.length + "=/=" + posToTest.length);
			for(int i=0; i<posToTest.length; i++){
				System.err.println("Champ [" + i + "]" + myClone[i] + "=/=" + posToTest[i]);
			}
		}

		vectBot.add(new Bot(null, null, null, 0, null));
		try{
			gm.incrementPosToTest(posToTest, vectBot);
			assertEquals(myClone.length, posToTest.length);
			for(int i=0; i<posToTest.length; i++){
				assertEquals(myClone[i], posToTest[i]);
			}
		}
		catch (AssertionFailedError e){
			System.err.println("Echec : ");
			System.err.println("Longueur : " + myClone.length + "=/=" + posToTest.length);
			for(int i=0; i<posToTest.length; i++){
				System.err.println("Champ [" + i + "]" + myClone[i] + "=/=" + posToTest[i]);
			}
		}
	}

	public void testFindCompatibleLists()
	{
		GameManager gm = new GameManager();
		Bot a = new Bot(null, "A", null, 30, null);
		Bot b = new Bot(null, "B", null, 0, null);
		Bot c = new Bot(null, "C", null, 15, null);
		Bot d = new Bot(null, "D", null, 10, null);
		Bot e = new Bot(null, "E", null, 1, null);
		Bot f = new Bot(null, "F", null, 0, null);
		
		gm.addBot(a);
		gm.addBot(b);
		gm.addBot(c);
		gm.addBot(d);
		gm.addBot(e);
		gm.addBot(f);
		
		gm.fillChallengers();
		
		System.out.print("Carte au début :\n" + gm);
		
		ArrayList<ArrayList<Bot>> list = gm.findCompatibleLists(2);
		
		System.out.println("\nMATCHUPS POSSIBLES :");

		for(int i=0; i<list.size(); i++)
		{
			StringBuffer sb = new StringBuffer();
			for(int j=0; j<list.get(i).size(); j++)
			{
				sb.append(list.get(i).get(j).getNick()+',');
			}
			System.out.println(sb);
		}
		
		ArrayList<ArrayList<Bot>> fights = gm.chooseFights(list);
		
		System.out.println("\nMATCHUPS CHOISIS :");

		for(int i=0; i<fights.size(); i++)
		{
			StringBuffer sb = new StringBuffer();
			for(int j=0; j<fights.get(i).size(); j++)
			{
				sb.append(fights.get(i).get(j).getNick()+',');
			}
			System.out.println(sb);
		}
		
		gm.setBotsInFight(fights);
		
		System.out.print("\nCarte à la fin \n" + gm);
	}
}
