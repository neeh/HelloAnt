package com.test.loginmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.message.Problem;

public class LoginTest
{
	
	public static void testLoginError101(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "21014616ea7d673e04046a132b6593ad");
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
		System.out.println("login101msgSend: " + msgSend);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("login101msgReceive: " + msgReceive);
					problem.setType(msgReceive.getString("type"));
					problem.setProblemID(msgReceive.getInt("error"));
					if(problem.getProblemID() == 101)
					{
						System.out.println("function login101:");
						problem.setDescription("Ca Marche!");
						System.out.println(problem.getDescription());
					}else
					{
						System.out.println("function login101: Ca Marche Pas!");
						problem.setDescription("login have a problem, receive error number is :"+problem.getProblemID());
						System.out.println(problem.getDescription());
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
	
	public static void testLoginNormal(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "zef4ze6eerg1er21g3f545v4zc1ze313");
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
		
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					
					problem.setType(msgReceive.getString("type"));
					problem.setProblemID(msgReceive.getInt("error"));
					if(problem.getProblemID() == 0)
					{
						JSONObject content = msgReceive.getJSONObject("content");
						problem.setDescription("Ca Marche!\n"+"logged in with bot %"+content.getString("nick")+"%");
						
					}else
					{
						problem.setDescription("login have a problem, receive error number is :"+problem.getProblemID());
					}
					System.out.println(problem.getDescription());
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
	
	public static void testLoginError103(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "zef4ze6eerg1er21g3f545v4zc1ze313");
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
		System.out.println("login103msgSend: " + msgSend);
		try
		{
			//System.out.println("receive:"+br.readLine());
			br.readLine();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		pw.println(msgSend);
		//System.out.println("msgSend:"+msgSend);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("login103msgReceive: " + msgReceive);
					//System.out.println("msgReceive:"+msgReceive);
					problem.setType(msgReceive.getString("type"));
					problem.setProblemID(msgReceive.getInt("error"));
					System.out.println("function login103:");
					if(problem.getProblemID() == 103)
					{
						String mess = msgReceive.getString("message");
						problem.setDescription("Ca Marche!\n");
						
					}else
					{
						problem.setDescription("login have a problem, receive error number is :"+problem.getProblemID());
					}
					
					System.out.println(problem.getDescription());
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
