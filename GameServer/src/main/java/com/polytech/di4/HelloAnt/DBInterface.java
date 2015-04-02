package com.polytech.di4.HelloAnt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBInterface
{
	private static DBInterface instance = null;
	private Connection conn;
	private PreparedStatement newBotStmt;
	//private PreparedStatement connectStmt;
	
	private DBInterface()
	{
		try
		{
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbants?user=root&password=");
			//name
			newBotStmt = conn.prepareStatement("SELECT NewBot(?);");
			//token, ip
			//connectStmt = conn.prepareStatement("SELECT Login(?, ?);");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static DBInterface getInstance()
	{
		if(instance == null)
			instance = new DBInterface();
		return instance;
	}
	
	public String newbot(String name)
	{
		String tok = null;
		try
		{
			newBotStmt.setString(1, name);
			ResultSet result = newBotStmt.executeQuery();
			if(!result.next())
			{
				//Error
				return null;
			}
			tok = result.getString(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return tok;
	}
}
