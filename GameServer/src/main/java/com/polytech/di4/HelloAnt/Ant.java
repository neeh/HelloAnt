package com.polytech.di4.HelloAnt;

public class  Ant extends AntGameObject 
{

	private Bot bot;
	private boolean food; 
	
	public Ant(int col, int row, Bot bot)
	{
		super(col, row);
		this.bot = bot;
		this.food = false;
	}
	
	public Bot getBot()
	{
		return this.bot;
	}
	public boolean hasFood()
	{
		return this.food;
	}
	public void setFood()
	{
		this.food = true;
	}
}
