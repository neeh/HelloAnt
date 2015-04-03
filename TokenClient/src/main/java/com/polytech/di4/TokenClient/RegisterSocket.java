package com.polytech.di4.TokenClient;
/**
 * 
 * This class implements client socket communication with the Game Server.
 * When the constructor is called, a client socket is created with the address parameter.
 * The second parameter is the bot name which needs to be sent.
 * It is encapsulated in JSON format.  
 * The JSON answer of the game server is either null or an alphanumeric token.
 * If it's null an error window pops-up.
 * If it's not, the token is stocked in the result attribute.
 * @param botName
 * 	The submitted bot name
 * @param serverIp
 * 	The game server IP address
 * @result
 * 	The attribute of the instance of this class.
 */
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
{	
	private String result;
	 	
	public RegisterSocket(String botName, InetAddress serverIp) throws IOException, JSONException 
	{
		PrintWriter out;
		BufferedReader in;
		Socket socket;
		
		socket = new Socket(serverIp,12345);
		
		
		JSONObject message = new JSONObject();
		message.put("type","token");
		
		JSONObject content = new JSONObject();
		content.put("nickname",botName);
		
		message.put("content", content);
		
		
		out = new PrintWriter(socket.getOutputStream());
		out.println(message);
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String reponse = in.readLine();
		JSONObject reponseJson = new JSONObject(reponse);
	
		String test = reponseJson.getJSONObject("content").getString("nickname");
		if (test!=null)
		{
			this.setResult(test);
		}
		else 
		{
			javax.swing.JOptionPane.showMessageDialog(null,"This nickname already exists"); 
		}
		

		socket.close();
		
	}

	public String getResult() 
	{
		return result;
	}

	public void setResult(String result) 
	{
		this.result = result;
	}
	
	
}
