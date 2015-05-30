package com.test.message;

public class Problem {
	private String type;
	private int problemID;
	private String description;
	
	public Problem(){
		
	}
	
	public Problem(String type, int problemID, String description){
		this.type = type;
		this.problemID = problemID;
		this.description = description;
	}
	
	public int getProblemID() {
		return problemID;
	}
	public void setProblemID(int problemID) {
		this.problemID = problemID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
