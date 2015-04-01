package com.polytech.di4.TokenClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterSocket
{	private String result;
	 	
	public RegisterSocket(String botName, InetAddress serverIp) throws IOException, JSONException {
		PrintWriter out;
		BufferedReader in;
		Socket socket;
		
		socket = new Socket(serverIp,2015);
		
		
		JSONObject message = new JSONObject();
		message.put("type","token");
		
		JSONObject content = new JSONObject();
		content.put("nickname","roger");
		
		message.put("content", content);
		
		
		out = new PrintWriter(socket.getOutputStream());
		out.println(message);
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String reponse = in.readLine();
		JSONObject reponseJson = new JSONObject(reponse);
	
		String test = reponseJson.getJSONObject("content").getString("nickname");
		if (test!=null)
		{
			this.result=test;
		}
		else 
		{
			javax.swing.JOptionPane.showMessageDialog(null,"This nickname already exists"); 
		}
		
		
		socket.close();
		
	}
	
	
}
