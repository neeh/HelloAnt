package com.test.client;

public class StringParse
{
	public static String stringFormat(String str)
	{
		String result = str;
		result = result.replaceAll(" ", "");
		result = result.replaceAll("\n", "");
		result = result.replaceAll("\t", "");
		return result;
	}
	
}
