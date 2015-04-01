package com.polytech.di4.HelloAnt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class BotAuthenticator implements Runnable
{
	private Socket socket;
	private BotListenerCallback callback;
	
	public BotAuthenticator(Socket socket, BotListenerCallback callback)
	{
		super();
		this.socket = socket;
		this.callback = callback;
	}

	public void run()
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String s = br.readLine();
			callback.botCreated(new Bot("Abc", socket, 1200));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
