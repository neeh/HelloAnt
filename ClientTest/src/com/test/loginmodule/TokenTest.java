package com.test.loginmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.message.Problem;

public class TokenTest
{
	public static void testTokenError101(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "772775173343d36278f8e53af8fffb36");
			con.put("mode", "training");
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		JSONObject msgSend = new JSONObject();
		try
		{
			msgSend.put("type", "login");
			msgSend.put("content", con);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSend);
		//System.out.println("token101msgSend:"+msgSend);
		try
		{
			//System.out.println("token101msgReceive:"+br.readLine());
			br.readLine();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		JSONObject conNew = new JSONObject();
		try
		{
			conNew.put("nick", "neeh");
		} catch (JSONException e2)
		{
			e2.printStackTrace();
		}
		JSONObject msgSendNew = new JSONObject();
		try
		{
			msgSendNew.put("type", "token");
			msgSendNew.put("content", conNew);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSendNew);
		System.out.println("Token101msgSendNew:"+msgSendNew);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("Token101msgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 101)
					{
						System.out.println("function token101:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function token101: Ca Marche Pas!");
						System.out.println("token have a problem, receive error number is :"+error);
					}
					
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		//}
	}
	
	
	public static void testTokenError102(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("nick", "we");
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		JSONObject msgSend = new JSONObject();
		try
		{
			msgSend.put("type", "token");
			msgSend.put("content", con);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSend);
		System.out.println("Token10222msgSendNew:"+msgSend);
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("Token10222msgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 102)
					{
						System.out.println("function token102:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function token102: Ca Marche Pas!");
						System.out.println("token have a problem, receive error number is :"+error);
					}
					
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		//}
	}
	
	public static void testTokenError103(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("nick", "jerome");
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		JSONObject msgSend = new JSONObject();
		try
		{
			msgSend.put("type", "token");
			msgSend.put("content", con);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSend);
		System.out.println("Token1033333msgSendNew:"+msgSend);
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("Token103333msgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 103)
					{
						System.out.println("function token103:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function token103: Ca Marche Pas!");
						System.out.println("token have a problem, receive error number is :"+error);
					}
					
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		//}
	}
}
