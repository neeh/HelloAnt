package com.polytech.di4.HelloAnt;

import java.util.List;

import junit.framework.TestCase;

public class AntGameMapTest extends TestCase
{
	public void testMask()
	{
		AntGameMap map = new AntGameMap(7, 7);
		for (int i = 0; i<7;i++)
		{
			for (int j=0;j<7;++j)
			{
			assertEquals(true,map.getGameObjectAt(i, j).isEmpty());
			}
		}
		
		Ant tAnt = new Ant(0,0);
		Ant tAnt2 = new Ant(1,1);
		map.addGameObject(tAnt);
		map.addGameObject(tAnt2);
		map.addGameObject(new Ant(5,5));
		AntGameMapMask masque = new AntGameMapMask(2);
		List<AntGameObject> test = map.applyMask(0, 0, masque);
		/*assertEquals(test.get(0).getColumn(),0);
		assertEquals(test.get(0).getRow(),0);*/
		
		assertEquals(2, test.size());
		
	}
}
