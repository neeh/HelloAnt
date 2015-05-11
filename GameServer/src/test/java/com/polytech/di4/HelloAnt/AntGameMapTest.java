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
		map.addGameObject(tAnt);
		AntGameMapMask masque = new AntGameMapMask(1);
		List<AntGameObject> test = map.applyMask(0, 0, masque);
		assertEquals(1, test.size());
	}
}
