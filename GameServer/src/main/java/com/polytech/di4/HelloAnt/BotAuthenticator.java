package com.polytech.di4.HelloAnt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
			String type = message.getString("type");
			if(type.equals("connect"))
			{
				JSONObject content = message.getJSONObject("content");
				Bot newBot = getBot(content.getString("token"), socket);
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
			else if(type.equals("token"))
			{
				// Inscrire le bot
				JSONObject content = message.getJSONObject("content");
				String botName = content.getString("nickname");
				String token = DBInterface.getInstance().newbot(botName);
				if(token == null)
				{
					System.out.println("Impossible d'inscrire " + botName);
				}
				else
				{
					JSONObject response = new JSONObject();
					response.put("token", token);
					System.out.println(botName + " : " + token);
					PrintWriter p = new PrintWriter(socket.getOutputStream());
					p.println(response.toString());
					p.flush();
				}
			}
			else
			{
				System.out.println("'" + type + "'");
			}
		}
		catch (IOException e)
		{
			System.out.println("IO error");
			e.printStackTrace();
		}
		//Erreur de parsing (message JSON incorrect) ou de lecture (pas de token)
		catch (JSONException e)
		{
			System.out.println("JSON error");
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
		//socket.getInetAddress().getHostAddress();
		if(token.equals("123abc"))
		{
			return new Bot("Roger", socket, 1200d);
		}
		return null;
	}
}
