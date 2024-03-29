package com;

import java.util.List;

import basis.Bot;
import basis.BotMode;
import ants.Ant;
import ants.AntGameMap;
import ants.AntGameMapMask;
import ants.AntGameObject;
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
			assertEquals(true,map.getGameObjectsAt(i, j).isEmpty());
			}
		}
		
		Bot bot = new Bot(null, "Luc", BotMode.TRAINING, 1200, null);
		Ant tAnt = new Ant(map, 0, 0, bot, 0, 0);
		Ant tAnt2 = new Ant(map, 1, 1, bot, 0, 0);
		map.addGameObject(tAnt);
		map.addGameObject(tAnt2);
		map.addGameObject(new Ant(map, 5, 5, bot, 0, 0));
		AntGameMapMask masque = new AntGameMapMask(2);
		List<AntGameObject> test = map.applyMask(0, 0, masque);
		/*assertEquals(test.get(0).getColumn(),0);
		assertEquals(test.get(0).getRow(),0);*/
		
		assertEquals(2, test.size());
		
	}
}
