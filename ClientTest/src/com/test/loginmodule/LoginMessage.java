package com.test.loginmodule;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginMessage
{
	private String type;
	private JSONObject content;
	
	public LoginMessage(String type, JSONObject content)
	{
		this.type = type;
		this.content = content;
	}
	
	/*public LoginMessage(String type, String token, String mode){
		this.type = type;
		try {
			content.put("token", token);
			content.put("mode", mode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/
	
	
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public JSONObject getContent()
	{
		return content;
	}

	public void setContent(JSONObject content)
	{
		this.content = content;
	}
}
