package ants;

import java.util.List;

public interface AntGameMapView
{
	/**
	 * Get the list of objects at a specific position on the map
	 * @param row Y-coordinate of the objects to get
	 * @param col X-coordinate of the objects to get
	 * @return A list of objects that are at this position
	 */
	public List<AntGameObject> getGameObjectsAt(int row, int col);
}
