package com.polytech.di4.HelloAnt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

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
			JSONObject message = new JSONObject(br.readLine());
			Bot newBot = getBot(message.getString("token"), socket);
			if(newBot == null)
			{
				//Invalid token !!!
				System.out.println("Invalid token ...");
			}
			else
			{
				System.out.println("New: " + newBot.getNickname());
				callback.botCreated(newBot);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		//Erreur de parsing (message JSON incorrect) ou de lecture (pas de token)
		catch (JSONException e)
		{
			//e.printStackTrace();
			//Just leave
			//Maybe warn the socket that the message is invalid ?
			try
			{
				socket.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	private Bot getBot(String token, Socket socket)
	{
		if(token.equals("123abc"))
		{
			return new Bot("Roger", socket, 1200d);
		}
		return null;
	}
}
