package com.test.loginmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.message.Problem;

public class LogoutTest
{
	public static void TestLogoutNormal(PrintWriter pw, BufferedReader br)
	{
		/*Problem problem = new Problem();
		JSONObject con = new JSONObject();
		try
		{
			con.put("token", "28624b77912520ec68173c4f1b8325dd");
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
		//System.out.println("logoutmsgSend"+msgSend);
		try
		{
			br.readLine();
			
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}*/
		JSONObject conNew = new JSONObject();
		JSONObject msgSendNew = new JSONObject();
		try
		{
			msgSendNew.put("type", "logout");
			msgSendNew.put("content", conNew);
		} catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		//send message
		pw.println(msgSendNew);
		System.out.println("logoutmsgSendNew:"+msgSendNew);
		//read message
		//while(br != null){
			try
			{
				String message = br.readLine();
				try
				{
					JSONObject msgReceive = new JSONObject(message);
					System.out.println("logoutmsgReceive:"+msgReceive);
					int error = msgReceive.getInt("error");
					if(error == 0)
					{
						System.out.println("function logout:");
						System.out.println("Ca Marche!");
					}else
					{
						System.out.println("function logout: Ca Marche Pas!");
						System.out.println("logout have a problem, receive error number is :"+error);
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
