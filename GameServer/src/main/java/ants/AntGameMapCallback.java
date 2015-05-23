package ants;

import util.Move;

public interface AntGameMapCallback
{
	public void moveGameObject(AntGameObject object, Move dir);
	
}
