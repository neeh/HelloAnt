package com.polytech.di4.HelloAnt;

public interface TCPClientCommunicatorCallback
{
	public void newClient(TCPClientCommunicator newClient);
	public void botConnected(Bot newBot);
	public void botDisconnected(Bot oldBot);
}
