package com.test.loginmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.message.Problem;

public class KillbotTest
{
	public static void testKillbotError101(PrintWriter pw, BufferedReader br)
	{
		JSONObject conNew = new JSONObject();
		try
		{
			conNew.put("token", "21014616ea7d673e04046a132b6593ad");
		} catch (JSONException e2)
		{
			e2.printStackTrace();
		}
		JSONObject msgSendNew = new JSONObject();
		
		try
		{
			msgSendNew.put("type", "killbot");
			msgSendNew.put("content", conNew);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSendNew);
		System.out.println("killbot101msgSendNew: " + msgSendNew);
		//System.out.println("msgSendNew:"+msgSendNew);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("killbot101msgReceive: " + msgReceive);
					//System.out.println("msgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 101)
					{
						System.out.println("function killbot101:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function killbot101: Ca Marche Pas!");
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
	
	public static void testKillbotError102(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "7fc3a0ef2e18ad6a007f87a7436ab98a");
			con.put("mode", "training");
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		JSONObject msgSend = new JSONObject();
		try
		{
			msgSend.put("type", "killbot");
			msgSend.put("content", con);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSend);
		System.out.println("killbot102msgSend: " + msgSend);
		try
		{
			br.readLine();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		JSONObject conNew = new JSONObject();
		try
		{
			conNew.put("token", "7fc3a0ef2e18ad6a007f87a7436ab98a");
		} catch (JSONException e2)
		{
			e2.printStackTrace();
		}
		JSONObject msgSendNew = new JSONObject();
		try
		{
			msgSendNew.put("type", "killbot");
			msgSendNew.put("content", conNew);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSendNew);
		//System.out.println("msgSendNew:"+msgSendNew);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("killbot102msgReceive: " + msgReceive);
					//System.out.println("msgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 102)
					{
						System.out.println("function killbot102:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function killbot102: Ca Marche Pas!");
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
