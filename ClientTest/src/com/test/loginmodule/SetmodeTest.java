package com.test.loginmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.message.Problem;

public class SetmodeTest
{
	public static void testSetmodeError101(PrintWriter pw, BufferedReader br)
	{
		Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "b29f14bf1ca41f631d2967495848c462");
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
			conNew.put("mode", "training");
		} catch (JSONException e2)
		{
			e2.printStackTrace();
		}
		JSONObject msgSendNew = new JSONObject();
		try
		{
			msgSendNew.put("type", "setmode");
			msgSendNew.put("content", conNew);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSendNew);
		System.out.println("SETMODE101msgSendNew:"+msgSendNew);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("setMODE101msgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 101)
					{
						System.out.println("function setmode:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function setmode: Ca Marche Pas!");
						System.out.println("setmode have a problem, receive error number is :"+error);
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
