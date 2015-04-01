package com.polytech.di4.HelloAnt;

import java.util.ArrayList;

public class GameServer implements BotListenerCallback
{
	private ArrayList<Bot> botList;
	private Thread botListener;
	
	public GameServer()
	{
		botListener = new Thread(BotListener.init(12345, this));
		botListener.start();
		botList = new ArrayList<Bot>();
	}

	public void botCreated(Bot newBot)
	{
		botList.add(newBot);
	}
}
