package com.polytech.di4.HelloAnt;

public class BotLoginException extends Exception
{
	private static final long serialVersionUID = 1L;
	private int errorNumber;
	
	public int getErrorNumber()
	{
		return errorNumber;
	}

	public void setErrorNumber(int errorNumber)
	{
		this.errorNumber = errorNumber;
	}

	public BotLoginException(int errorNumber)
	{
		super();
		this.errorNumber = errorNumber;
	}
}
