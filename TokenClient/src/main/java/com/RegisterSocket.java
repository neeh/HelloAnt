/* 
 * This source file is part of HelloAnt.
 * 
 * Coyright(C) 2015 Nicolas Monmarché
 * 
 * HelloAnt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HelloAnt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HelloAnt.  If not, see <http://www.gnu.org/licenses/>.
 */

package com;

/**
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
		//creates the JSON object with the given botname
		JSONObject message = new JSONObject();
		message.put("type","token");
		
		JSONObject content = new JSONObject();
		content.put("nick",botName);
		
		message.put("content", content);
		//send the botname in a JSON format
		out = new PrintWriter(socket.getOutputStream());
		out.println(message.toString());
		out.flush();
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//read the answer
		String reponse = in.readLine();
		
		JSONObject reponseJson = new JSONObject(reponse);
		String type = reponseJson.getString("type");
		//The first answer message is a welcome made by the constructor 
		//TCPClientCommunicator
		if (type.equals("welcome"))
		{
			 reponse = in.readLine();
			 reponseJson = new JSONObject(reponse);
			 type = reponseJson.getString("type");
		}
		//Here we read the true token answer
		if (type.equals("token"))
		{
			//First we check for errors
			if(reponseJson.getInt("error") == 0)
			{
				String test = reponseJson.getJSONObject("content").getString("token");
				if (test!=null)
				{
					this.setResult(test);
				}
			}
			
			else
			{
				javax.swing.JOptionPane.showMessageDialog(null,reponseJson.getString("message")); 
			}
		}
		else
		{
			System.out.println("Wrong message Type : "+reponseJson.getString("type"));
		}
		socket.close();
		
	}

	/**
	 * Get the result  attribut with returned token
	 * @return
	 */
	public String getResult() 
	{
		return result;
	}
	/**
	 * Put the result  attribut with returned token
	 * @return
	 */
	public void setResult(String result) 
	{
		this.result = result;
	}
	
	
}
