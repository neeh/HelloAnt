package com.polytech.di4.HelloAnt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client listener is responsible for accepting incoming TCP communications and
 * creating communicator that will enable the server to exchange with them. Hence, it's a
 * singleton.
 * Once a client is created, it should be added to the client list in the game server
 * singleton. So there's a reference to the game server used to call the callback method
 * for adding a client.
 * @class
 * @author Nicolas
 */
public class TCPClientListener implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClientListener.class);
	
	/**
	 * TODO: update doc
	 * The listener will call the 'handleClient' method from that class when a client is
	 * connecting on the socket server.
	 */
	private TCPClientListenerCallback eventCallback;
	
	/**
	 * The server socket used to accept incoming TCP communications.
	 * @see ServerSocket
	 */
	private ServerSocket srvsock;
	
	/**
	 * The network port on which to listen connections.
	 */
	private int port;
	
	/**
	 * Creates a new client listener.
	 * @constructor
	 * @param gamesrv a reference to the class in charge of handling arriving clients.
	 */
	public TCPClientListener(TCPClientListenerCallback eventCallback) {
		this.eventCallback = eventCallback;
	}
	
	/**
	 * Gets the port on which the listener is accepting clients.
	 * @return the port of the server.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port of the client listener.
	 * @param port
	 */
	public void setPort(int port) {
		if (port > 0 && port < 65536) {
			try {
				if (srvsock != null && srvsock.isBound()) {
					srvsock.close();
				}
				srvsock = new ServerSocket();
				srvsock.setReuseAddress(true);
				srvsock.bind(new InetSocketAddress(port));
				this.port = port;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.error("Listening on invalid address port: " + port);
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket socket = srvsock.accept();
				eventCallback.handleClient(new TCPClientCommunicator(socket, eventCallback));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
