package com.polytech.di4.HelloAnt;
/**
 * This class instantiate the AntGameObjects used to generate food.
 * @author Benjamin
 *
 */
public class AntFoodSpawn extends AntGameObject 
{

	private boolean food;
	private int lastHarvestRound;
	/**
	 * This function creates a new AntFoodSpawn on the selected cell
	 * @param column
	 * @param row
	 */
	public AntFoodSpawn(int column, int row)
	{
		super(column, row);
		food = false;
		lastHarvestRound = 0;
		movable = false;
		colideable = true;
		
	}
	/**
	 * @return True if food is ready to be taken by an ant, false if not.
	 */
	public boolean hasFood()
	{
		return food;
	}
	/**
	 * Set the food to true. 
	 * @param food
	 */
	public void setFood(boolean food)
	{
		this.food = food;
	}
	/**
	 * @return the number of the round when the last food has been taken.
	 */
	public int getLastHarvestRound()
	{
		return lastHarvestRound;
	}
	/**
	 * Set food to false
	 */
	public void harvest()
	{
		if (food==false)
		{
			//Do we raise an exception for this ?
		}
		food = false;
		//get the actual round to actualise the  lastHarvestRound
	}

}
