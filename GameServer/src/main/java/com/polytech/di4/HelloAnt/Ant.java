package com.polytech.di4.HelloAnt;
/**
 * This class instantiates ant objects. It is just an AntGameObject which can carry
 *food. This is a movable object.
 * @author Benjamin
 *
 */
public class  Ant extends AntGameObject 
{

	private Bot bot;
	private boolean food; 
	/**
	 * Creates a new ant  on the selected cell associated to a specific bot.
	 * It has no food at first.
	 * @param col
	 * @param row
	 * @param bot
	 */
	public Ant(int col, int row, Bot bot)
	{
		super(col, row);
		this.bot = bot;
		this.food = false;
	}
	/**
	 * Creates a new ant  on the selected cell associated without bot.
	 * It has no food at first. This constructor is mainly used for tests.
	 * @param col
	 * @param row
	 */
	public Ant(int col, int row)
	{
		super(col, row);
		this.food = false;
		this.bot = null;
	}
	/**
	 * @return the bot that owns the ant.
	 */
	public Bot getBot()
	{
		return this.bot;
	}
	/**
	 * @return True if the ant is carrying food. False if  not.
	 */
	public boolean hasFood()
	{
		return this.food;
	}
	/**
	 * Set food at true. The ant found food.
	 */
	public void setFood()
	{
		this.food = true;
	}
}
