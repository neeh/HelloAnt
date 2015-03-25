package com.polytech.di4.HelloAnt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotListener implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BotListener.class);
	private static BotListener instance;
	private ServerSocket listener;
	private BotListenerCallback callback;
	
	private BotListener(int port, BotListenerCallback callback)
	{
		this.callback = callback;
		try
		{
			listener = new ServerSocket();
			listener.setReuseAddress(true);
			listener.bind(new InetSocketAddress(port));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static BotListener init(int port, BotListenerCallback callback)
	{
		//TODO: erreur si déjà init
		instance = new BotListener(port, callback);
		return instance;
	}
	
	public static BotListener getInstance()
	{
		return instance;
	}

	public void run()
	{
		Socket socket;
		while (true)
		{
			try
			{
				socket = listener.accept();
				new Thread(new BotAuthenticator(socket, callback)).start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
