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
		Bot a = new Bot(null, "A", null, 0, null);
		Bot b = new Bot(null, "B", null, 0, null);
		Bot c = new Bot(null, "C", null, 0, null);
		Bot d = new Bot(null, "D", null, 0, null);
		Bot e = new Bot(null, "E", null, 0, null);
		Bot f = new Bot(null, "F", null, 0, null);

		HashMap<Bot, Vector<Bot>> hm = new HashMap<>();
		Vector<Bot> vA = new Vector<>();
		Vector<Bot> vB = new Vector<>();
		Vector<Bot> vC = new Vector<>();
		Vector<Bot> vD = new Vector<>();
		Vector<Bot> vE = new Vector<>();
		Vector<Bot> vF = new Vector<>();
		vA.add(b);
		vA.add(c);
		vA.add(d);	
		vA.add(e);
		vA.add(f);

		vB.add(a);	
		vB.add(c);	
		vB.add(d);
		
		vC.add(a);	
		vC.add(b);	
		vC.add(d);
		vC.add(e);
		vC.add(f);
		
		vD.add(a);	
		vD.add(b);	
		vD.add(c);	
		
		
		vF.add(a);
		vF.add(e);

		hm.put(a, vA);
		hm.put(b, vB);
		hm.put(c, vC);
		hm.put(d, vD);
		hm.put(e, vE);
		hm.put(f, vF);

		ArrayList<ArrayList<Bot>> list = gm.findCompatibleLists(hm);
//		System.out.println("\n\nMATCHUPS POSSIBLES :\n");
		assertEquals(1, list.size());
		assert(list.get(0).contains(a));
		assert(list.get(0).contains(b));
		assert(list.get(0).contains(c));
		assert(list.get(0).contains(d));
//		for(int i=0; i<list.size(); i++)
//		{
//			StringBuffer sb = new StringBuffer();
//			for(int j=0; j<list.get(i).size(); j++)
//			{
//				sb.append(list.get(i).get(j).getNick()+',');
//			}
//			System.out.println(sb);
//		}
	}
}