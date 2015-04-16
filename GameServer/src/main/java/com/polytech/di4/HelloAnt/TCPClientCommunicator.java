package com.polytech.di4.HelloAnt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new TCP client. This class is responsible for the communication between
 * the server and the client. When the client needs the server to execute a specific
 * command, it should create a JSON message which respects the protocol specification
 * and give the required elements for the execution of the command.
 * @see Documentation/protocol/
 * @class
 * @author Nicolas
 */
public class TCPClientCommunicator implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(
			TCPClientCommunicator.class);
	
	/**
	 * The socket used to communicate with the client.
	 * Should be set only once, when you call the constructor.
	 * @see Socket
	 */
	private Socket socket;
	
	/**
	 * The client thread is the thread in charge of listening client messages.
	 * Creating a new thread enables us to listen to multiple client at the same time.
	 * @see Thread
	 */
	private TCPClientCommunicatorCallback eventCallback;
	private Thread clientThread;
	private PrintWriter __writer__;
	private BufferedReader __reader__;
	private Bot bot;
	private DBInterface dbi;
	private Boolean muted;
	
	/**
	 * Whether the listening thread is closed.
	 * Turning this boolean to true will stop the listening thread from running.
	 * Be careful, there's no provided method to restart the listening thread.
	 */
	private Boolean closed;
	
	/**
	 * Creates a new TCP client communicator.
	 * @constructor
	 * @param socket the socket of the TCP client.
	 */
	public TCPClientCommunicator(Socket socket,
			TCPClientCommunicatorCallback eventCallback) {
		this.socket = socket;
		this.eventCallback = eventCallback;
		// Create the client thread.
		clientThread = new Thread(this);
		try {
			// At this point, we cache a PrintWriter object and a BufferedReader object
			// to avoid excessive object creation during the communication.
			__writer__ = new PrintWriter(socket.getOutputStream());
			__reader__ = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch(IOException e) {
			e.printStackTrace();
		}
		bot = null;
		dbi = DBInterface.getInstance();
		muted = false;
		closed = false;
		// Start the listening of the client.
		clientThread.start();
		eventCallback.newClient(this);
	}
	
	/**
	 * Returns whether the client is connected with a bot.
	 * @return true if the client is connected, false otherwise
	 */
	public Boolean isConnected() {
		// When a client connect himslef, his bot object is assigned to the 'bot' property
		// of this class, so we can simply test this property.
		return bot != null;
	}
	
	/**
	 * Gets the bot of the client.
	 * @return the bot of the client.
	 */
	public Bot getBot() {
		return bot;
	}
	
	/**
	 * Mutes a client.
	 * A muted client can send messages, but the server no longer read those.
	 */
	public void mute() {
		if (muted == true) {
			LOGGER.warn("Muted a client that was already muted");
		}
		muted = true;
	}
	
	/**
	 * Unmutes a muted client.
	 * A muted client can send messages, but the server no longer read those.
	 */
	public void unmute() {
		if (muted == false) {
			LOGGER.warn("Unmuted a client that was not muted");
		}
		muted = false;
	}
	
	// TODO: doc
	private void disconnect()
	{
		if (isConnected()) {
			if (bot.isInGame()) {
				// TODO: remove the bot from this game.
			}
			// Notify the database the bot is out.
			dbi.disconnect(bot.getNick());
		}
	}
	
	/**
	 * Closes the communicator with the client.
	 * The server will no longer listen to this client. If the client was connected with
	 * a bot, it will be disconnected.
	 */
	public void close() {
		disconnect();
		// TODO: remove the client from the clients array on the game server.
		// Once the client is removed on the 'clients' array, there is no more reference
		// to this instance in the server so it should be garbage collected soon.
		// Close the listening thread:
		closed = true;
		// Problem: the caller of close() is not necessarily the thread itself!
		// So passing this boolean to false may lead to listening the client one last time
		// before effectivly closing the thread... Thread.interrupt() may also causes
		// problems in case the caller is the thread!
		// TODO: close the socket
		clientThread.interrupt();
	}
	
	// TODO: doc
	@SuppressWarnings("unused")
	private void _close()
	{
		disconnect();
		closed = true;
	}
	
	/**
	 * Sends a "gamestate" message to the client. The client is supposed to return its
	 * actions within a given time interval.
	 * @see Documentation/protocol/gamestate.html
	 * @param content the object representing the current game state.
	 */
	public void sendGameStateMessage(JSONObject content) {
		sendMessage("gamestate", 0, "Current state of the game, please return your "
				+ "actions", content);
	}
	
	/**
	 * Sends a "gamestart" message to the client indicating that a game just started for
	 * him.
	 * @see Documentation/protocol/gamestart.html
	 * @param content the object representing the starting game state.
	 */
	public void sendGameStartMessage(JSONObject content) {
		sendMessage("gamestart", 0, "A game just started", content);
	}
	
	/**
	 * Sends a "gameend" message to the client indicating that the game just ended.
	 * @see Documentation/protocol/gameend.html
	 * @param content the object representing the ending game state and replay data.
	 */
	public void sendGameEndMessage(JSONObject content) {
		sendMessage("gameend", 0, "The game ended", content);
	}
	
	/**
	 * Sends a "gamemute" message to the client indicating that the client was muted for
	 * this game and hence, can no longer send "gameactions" message until it receives a
	 * new "gamestate" message.
	 * @see Documentation/protocol/gamemute.html
	 * @param content the object giving the reason(s) and additional information(s) about
	 *        the mute.
	 */
	public void sendGameMuteMessage(JSONObject content) {
		sendMessage("gamemute", 0, "You are muted until the end of the game", content);
	}
	
	/**
	 * Tests whether a nickname is valid according to the nickname specifications.
	 * @see Documentation/protocol/nickspecs.html
	 * @param nick the desired nickname for a bot.
	 * @return true if the nickname is valid, false otherwise.
	 */
	private Boolean isValidNickname(String nick) {
		return nick.matches("^[a-zA-Z][a-zA-Z0-9]{3,16}$");
	}
	
	/**
	 * Executes a client command.
	 * @see Documentation/protocol/
	 * @param msgObj the JSON message containing the command.
	 */
	private void exec(JSONObject msgObj) throws JSONException {
		String type = msgObj.getString("type");
		JSONObject content = msgObj.getJSONObject("content");
		// For each message type, run the appropriate method.
		if (type.equals("gameactions"))		execGameActions(content);
		else if (type.equals("connect"))	execConnect(content);
		else if (type.equals("disconnect"))	execDisconnect(content);
		else if (type.equals("setmode"))	execSetMode(content);
		else if (type.equals("token"))		execToken(content);
		else if (type.equals("killbot"))	execKillBot(content);
		else {
			// Notify the client we cannot treat its command.
			sendMessage("error", 2, "Unknown command " + type, null);
		}
	}
	
	/**
	 * Executes a "gameactions" client command and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the command and the reason
	 * of the failure.
	 * @see Documentation/protocol/gameactions.html
	 * @param content the content of the "gameactions" command, depends on the type of
	 *        game implemented.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void execGameActions(JSONObject content) throws JSONException {
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// First, test if the client is connected with a bot.
		if (isConnected()) {
			// Then, test if the bot is effectively playing in a game.
			if (bot.isInGame()) {
				// Send bot actions to the game.
				error = bot.getGame().receiveActions(bot, content);
				if (error == 104) {
					outputMessage = "Too late";
				} else if (error == 103) {
					outputMessage = "Not your turn";
				} else if (error == 102) {
					outputMessage = "You were muted for this game";
				} else {
					// error should be 0 in this block, but we set it to 0 to prevent the
					// method from returning unknown error ID for this message.
					error = 0;
					outputMessage = "OK";
				}
			} else {
				error = 101;
				outputMessage = "Not in a game";
			}
		} else {
			error = 3;
			outputMessage = "Not connected";
		}
		// Finally, send the response to the client.
		sendMessage("gameactions", error, outputMessage, null);
	}
	
	/**
	 * Executes a "connect" client command and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the command and the reason
	 * of the failure.
	 * @see Documentation/protocol/connect.html
	 * @param content the content of the "connect" command, should contains the token
	 *        generated for the bot only.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void execConnect(JSONObject content) throws JSONException {
		// Response message parameters:
		int error = 0;
		String outputMessage;
		JSONObject outputContent = null;
		// First, check if the client is not already connected with a bot.
		if (isConnected() == false) {
			String modeString = "regular";
			try
			{
				modeString = content.getString("mode");
			}
			catch(JSONException e) {}
			BotMode mode = BotMode.fromString(modeString);
			String ip = socket.getInetAddress().getHostAddress();
			try
			{
				Bot bot = dbi.login(content.getString("token"), this, mode, ip);
				this.bot = bot;
				eventCallback.botConnected(bot);
				outputMessage = "Connected";
			}
			catch (BotLoginException e)
			{
				switch (e.getErrorNumber())
				{
				case 1:
					error = 101;
					outputMessage = "Token does not exist";
					break;
				case 2:
					error = 102;
					outputMessage = "Bot with this token is already connected";
					break;
				default:
					error = 801;
					outputMessage = "Unknown error";
					break;
				}
			}
		} else {
			error = 103;
			outputMessage = "Already connected with bot '" + bot.getNick() + "'";
		}
		// Finally, send the response to the client.
		sendMessage("connect", error, outputMessage, outputContent);
	}
	
	/**
	 * Executes a "disconnect" client command and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the command and the reason
	 * of the failure.
	 * @see Documentation/protocol/disconnect.html
	 * @param content the content of the "disconnect" command (it should be null in this
	 *        type of message.)
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void execDisconnect(JSONObject content) throws JSONException {
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// Check that the client is connected.
		if (isConnected()) {
			// TODO: if the bot was in game, take it out of the game.
			// TODO: handle DB interactions with both ID?
			dbi.disconnect(this.bot.getNick());
			eventCallback.botDisconnected(this.bot);
			outputMessage = "Disconnected " + this.bot.getNick();
			// TODO: remove the bot from the clients array in the game server class.
			// TODO: top client thread and destroy this instance.
			this.bot = null;
		} else {
			error = 3;
			outputMessage = "Not connected";
		}
		// Finally, send the response to the client.
		sendMessage("disconnect", error, outputMessage, null);
	}
	
	/**
	 * Executes a "setmode" client command and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the command and the reason
	 * of the failure.
	 * @see Documentation/protocol/setmode.html
	 * @param content the content of the "setmode" command, should contains the desired
	 *        mode.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void execSetMode(JSONObject content) throws JSONException {
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// First, test if the client is connected with a bot.
		if (isConnected()) {
			// Then, get the desired mode and try to set it for the bot.
			String mode = content.getString("mode");
			if (mode == "regular") {
				if (bot.getMode() != BotMode.REGULAR) {
					bot.setMode(BotMode.REGULAR);
					outputMessage = "Mode changed to regular";
				} else {
					error = 102;
					outputMessage = "Already in regular mode";
				}
			} else if (mode == "training") {
				if (bot.getMode() != BotMode.TRAINING) {
					bot.setMode(BotMode.TRAINING);
					outputMessage = "Mode changed to training";
				} else {
					error = 102;
					outputMessage = "Already in training mode";
				}
			} else {
				error = 101;
				outputMessage = "Unknown mode " + mode;
			}
		} else {
			error = 3;
			outputMessage = "Not connected";
		}
		// Finally, send the response to the client.
		sendMessage("setmode", error, outputMessage, null);
	}
	
	/**
	 * Executes a "token" client command and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the command and the reason
	 * of the failure.
	 * @see Documentation/protocol/token.html
	 * @param content the content of the "token" command, should contains the desired
	 *        nickname for the bot to create.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void execToken(JSONObject content) throws JSONException {
		// Response message parameters:
		int error = 0;
		String outputMessage;
		JSONObject outputContent = null;
		// Check if the bot is already connected, the token demand might be an error...
		if (!isConnected()) {
			String nick = content.getString("nickname");
			System.out.println(nick);
			System.out.println(isValidNickname("coucou"));
			// Check the validity of the desired nickname
			if (isValidNickname(nick)) {
				String token = dbi.createBot(nick);
				if (token != null) {
					outputContent = new JSONObject();
					// Add the generated token to the content of the message
					outputContent.put("token", token);
					outputMessage = "Bot " + nick + " created";
				} else {
					// A null token implies that the token already exists in the DB.
					error = 103;
					outputMessage = "Nickname " + nick + " already exists";
				}
			} else {
				error = 102;
				outputMessage = "Invalid nickname";
			}
		} else {
			error = 101;
			outputMessage = "You are currently using a token";
		}
		// Finally, send the response to the client.
		sendMessage("token", error, outputMessage, outputContent);
	}
	
	/**
	 * Executes a "killbot" client command and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the command and the reason
	 * of the failure.
	 * @see Documentation/protocol/killbot.html
	 * @param content the content of the "killbot" command, should contains the token of
	 *        the bot to remove.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void execKillBot(JSONObject content) throws JSONException {
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// Get the token to remove.
		String token = content.getString("token");
		// Check that the bot is not connected.
		if (dbi.isBotOnline(token) == false) {
			// Try to remove the bot.
			if (dbi.removeBot(token)) {
				outputMessage = "Bot removed";
			} else {
				error = 101;
				outputMessage = "Token does not exist";
			}
		} else {
			error = 102;
			outputMessage = "Bot is connected";
		}
		// Finally, send the response to the client.
		sendMessage("killbot", error, outputMessage, null);
	}
	
	/**
	 * Sends a message to the TCP client. Messages are formatted in JSON.
	 * @see Documentation/protocol/
	 * @param type the type of message.
	 * @param error the error id (0 if no error.)
	 * @param message a legible string describing the message.
	 * @param content the content of the message, depends on the type of message.
	 */
	private void sendMessage(String type, int error, String message, JSONObject content) {
		// The JSONObject containing the message.
		JSONObject msgObj = new JSONObject();
		try {
			msgObj.put("type", type);
			msgObj.put("error", error);
			msgObj.put("message", message);
			msgObj.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		__writer__.println(msgObj.toString());
		// Flush the buffer so the client can see the message.
		__writer__.flush();
	}
	
	/**
	 * Runs the reception of message from the TCP client.
	 * When a message is received, it is given to the 'exec' method to be treated.
	 */
	public void run() {
		while (closed == false) {
			// Don't read client messages if client is muted.
			if (muted == false) {
				try {
					String input = __reader__.readLine();
					// Corrected NullPointException: We cannot create a JSONObject from
					// a null string.
					if (input != null) {
						JSONObject msgObj = new JSONObject(input);
						exec(msgObj);
					} else {
						// A null input implies the socket was closed on the client side.
						close();
						// Closing the client will switch the boolean 'closed' to false
						// hence we're getting out of the listening loop.
					}
				} catch (JSONException e) {
					// If there was an error during the JSON parsing, it means that the
					// input message was not correctly formed. Hence, we send him a
					// malformed message error.
					sendMessage("error", 1, "Malformed message, check the syntax in the "
							+ "documentation", null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
