package com.polytech.di4.HelloAnt;
/**
 * This class implements the object used to make ants in the game.
 * It has a position (col,row) on the map and can be disabled.
 * @author Benjamin
 *
 */
public class AntHill extends AntGameObject 
{
	private Bot bot;
	private boolean active;
	private long lastPop;
	/**
	 * @return a new AntHill activated
	 * @param bot
	 * @param col
	 * @param row
	 */
	public AntHill(Bot bot, int col, int row)
	{
		super(col,row);
		this.bot = bot;
		active = true;
		movable = false;
		colideable = true;
		
		
	}
	/**
	 * @return The bot which possess this AntHill
	 */
	public Bot getBot()
	{
		return this.bot;
	}
	/**
	 * 
	 * @return true if the AntHill is active or false if not.
	 */
	public boolean isActive()
	{
		return this.active;
	}

	/**
	 * 
	 * @return Returns the last spawned object
	 */
	public long getLastPop()
	{
		return this.lastPop;
	}
	/**
	 * Makes the AntHill active
	 */
	public void setActive()
	{
		active = true;
	}
}
