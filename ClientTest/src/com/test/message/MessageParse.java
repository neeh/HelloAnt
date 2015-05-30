package com.test.message;

import javax.swing.JTextArea;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.client.ClientFrame;

public class MessageParse 
{
	public static void sendMessageJSONObject(String sendMessage, JTextArea area_parseWindow)
	{
		try 
		{
			JSONObject msgObject = new JSONObject(sendMessage);
			String type = msgObject.getString("type");
			JSONObject content = msgObject.getJSONObject("content");
			showParseSendMessage(type, content,area_parseWindow);
			System.out.println("type:" + type);
			System.out.println("content:" + content);
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void reciveMessageJSONObject(String reciveMessage, JTextArea area_parseWindow)
	{
		try 
		{
			JSONObject msgObject = new JSONObject(reciveMessage);
			String type = msgObject.getString("type");
			int error = msgObject.getInt("error");
			String message = msgObject.getString("message");
			JSONObject content = null;
			if(type.equals("gameactions"))
			{
				content = null;
			}else if(type.equals("login"))
			{
				content = msgObject.getJSONObject("content");
			}else if(type.equals("logout"))
			{
				content = null;
			}else if(type.equals("setmode"))
			{
				content = null;
			}else if(type.equals("token"))
			{
				content = msgObject.getJSONObject("content");
			}else if(type.equals("killbot"))
			{
				content = null;
			}else if(type.equals("gamestate"))
			{
				content = msgObject.getJSONObject("content");
			}else if(type.equals("gamestart"))
			{
				content = msgObject.getJSONObject("content");
			}else if(type.equals("gameend"))
			{
				content = msgObject.getJSONObject("content");
			}else if(type.equals("gamemute"))
			{
				content = msgObject.getJSONObject("content");
			}else
			{
				type = "nuknown type";
				error = -111;
				message = "there are some problems of the server response";
				content = null;
			}
			showParseReceiveMessage(type, error, message, content, area_parseWindow);
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void showParseSendMessage(String type, JSONObject content, JTextArea area_parseWindow){
		area_parseWindow.append("Client SEND Message To Server:\n");
		area_parseWindow.append("\tType : " + type + "\n");
		area_parseWindow.append("\tContent : " + content + "\n");
		area_parseWindow.append("\n\n");
	}
	
	public static void showParseReceiveMessage(String type, int error, String message, JSONObject content, JTextArea area_parseWindow){
		area_parseWindow.append("Client RECEIVE Message From Server:\n");
		area_parseWindow.append("\tType : " + type + "\n");
		area_parseWindow.append("\tError : " + error + "\n");
		if(message != null)
		{
			area_parseWindow.append("\tMessage : " + message + "\n");
		}
		if(content != null)
		{
			area_parseWindow.append("\tContent : " + content + "\n");
		}
	}
}
