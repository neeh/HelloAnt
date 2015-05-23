/* 
 * This source file is part of HelloAnt.
 * 
 * Coyright(C) 2015 Nicolas Monmarché
 * 
 * HelloAnt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HelloAnt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HelloAnt.  If not, see <http://www.gnu.org/licenses/>.
 */

package basis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: for future projects, handle spam mute?
/**
 * Creates a new TCP client. This class is responsible for the communication between
 * the server and the client. When the client needs the server to execute a specific
 * command, it should create a JSON message which respects the protocol specification
 * and give the required elements for the execution of the command.
 * @see Documentation/protocol/
 * @class
 * @author Nicolas
 */
public class TCPClientCommunicator implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(
			TCPClientCommunicator.class);
	
	/**
	 * The socket used to communicate with the client.
	 * Should be set only once, when you call the constructor.
	 * @see Socket
	 */
	private Socket socket;
	
	/**
	 * The buffered reader used to read text-formatted messages from the client's socket
	 * input stream. It is created and cached during initialization to avoid creating a
	 * new BufferedReader every time we have to read a message from the client.
	 * @see BufferedReader
	 */
	private BufferedReader __reader__;
	
	/**
	 * The print writer used to print text-formatted messages on the client's socket
	 * output stream. It is created and cached during initialization to avoid creating a
	 * new PrintWriter every time we have to send a message to the client.
	 * @see PrintWriter
	 */
	private PrintWriter __writer__;
	
	/**
	 * The bot the client is logged in as.
	 * Is null when the client is not logged in as a bot yet.
	 */
	private Bot bot;
	
	/**
	 * The database manager that enables the communicator to retrieve and update data in
	 * the database.
	 */
	private DBManager dbm;
	
	/**
	 * The client handler used to call back the server when specific events occur.
	 */
	private TCPClientHandler handler;
	
	/**
	 * The client thread is the thread in charge of listening client messages.
	 * Create a new thread enables us to listen to multiple client at the same time.
	 * The client communicator is autonomous because it runs its own thread.
	 * @see Thread
	 */
	private Thread clientThread;
	
	/**
	 * Whether the client is muted.
	 * A muted client can still send messages, but those messages are queued because the
	 * communicator no longer read those.
	 */
	private boolean muted;
	
	/**
	 * Whether the listening thread is closed.
	 * Turning this boolean to true will stop the listening thread from running.
	 * @warning there is no way to restart the client thread.
	 */
	private boolean closed;

	/**
	 * Creates a new TCP client communicator without handler.
	 * @constructor
	 * @param socket the socket of the TCP client.
	 */
	public TCPClientCommunicator(Socket socket)
	{
		this(socket, null);
	}
	
	/**
	 * Creates a new TCP client communicator.
	 * @constructor
	 * @param socket the socket of the TCP client.
	 * @param handler the client handler used to notify the server a client was created.
	 */
	public TCPClientCommunicator(Socket socket, TCPClientHandler handler)
	{
		this.socket = socket;
		try
		{	// At this point, we cache a BufferedReader object and a BufferedWriter object
			// to avoid excessive object creation during the communication.
			if (socket != null)
			{	// (socket is null for fake communicators)
				__reader__ = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				__writer__ = new PrintWriter(socket.getOutputStream());
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Cannot create reader or writer to communicate with a client\n"
					+ e.getMessage());
		}
		bot = null;
		dbm = DBManager.getInstance();
		this.handler = handler;
		// Create the client thread.
		clientThread = new Thread(this);
		muted = false;
		closed = false;
		// Start the listening of the client.
		clientThread.start();
		// Notifies the server a new client was created.
		if (handler != null)
		{	// (handler is null for fake communicators)
			handler.handleClientConnected(this);
		}
		// Send the welcome message.
		sendWelcome();
	}
	
	/**
	 * Runs the mechanics of a TCP client communicator which is to read input messages
	 * from the client. When a message is read, the communicator handles it.
	 */
	public void run()
	{
		while (closed == false)
		{	// Don't read client messages if client is muted.
			if (muted == false)
			{
				try
				{	// Read an input message.
					String input = __reader__.readLine();
					// Corrected NullPointException: We cannot create a JSONObject from
					// a null string.
					if (input != null)
					{
						JSONObject msgObj = new JSONObject(input);
						// Receive the client request.
						receive(msgObj);
					}
					else
					{	// A null input implies the socket was closed on the client side.
						_close();
						// Closing the client will switch the boolean 'closed' to false
						// hence we're getting out of the reading loop.
					}
				}
				catch (JSONException e)
				{	// If there was an error during the JSON parsing, it means that the
					// input message was not correctly formed. Hence, we return a
					// malformed message error to the client.
					send("error", 1, "Malformed message, check the syntax in the "
							+ "documentation", null);
				}
				catch (IOException e)
				{	// The user might not have send a null message to disconnect itself.
					// So we close the communicator properly here.
					_close();
				}
			}
			else
			{	// If the client was muted.
				try
				{   // Sleep 250ms to slow down the loop.
					Thread.sleep(250);
				}
				catch (InterruptedException e) {}
			}
		}
	}
	
	/**
	 * Receives a client request.
	 * @see Documentation/protocol/
	 * @param msgObj the JSON message containing the request.
	 */
	private void receive(JSONObject msgObj) throws JSONException
	{
		String type = msgObj.getString("type");
		JSONObject content = msgObj.getJSONObject("content");
		// For each message type, call the appropriate method.
		if (type.equals("gameactions"))		receiveGameActions(content);
		else if (type.equals("login"))		receiveLogin(content);
		else if (type.equals("logout"))		receiveLogout(content);
		else if (type.equals("setmode"))	receiveSetMode(content);
		else if (type.equals("token"))		receiveToken(content);
		else if (type.equals("killbot"))	receiveKillBot(content);
		else
		{	// Notify the client we cannot treat its command.
			send("error", 2, "Unknown command " + type, null);
		}
	}
	
	/**
	 * Receives a "gameactions" client request and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the request and the reason
	 * of the failure.
	 * @see Documentation/protocol/gameactions.html
	 * @param content the content of the "gameactions" message, depends on the type of
	 *        game implemented.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void receiveGameActions(JSONObject content) throws JSONException
	{
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// First, test if the client is logged in as a bot.
		if (isBotLoggedIn())
		{
			// Then, test if the bot is effectively playing in a game.
			if (bot.isInGame())
			{
				// Send bot actions to the game.
				error = bot.getGame().receiveActions(bot, content);
				switch(error)
				{
				case 104:
					outputMessage = "Too late";
					break;
				case 103:
					outputMessage = "Not your turn";
					break;
				case 102:
					outputMessage = "You were muted for this game";
					break;
				case 0:
					outputMessage = "OK";
					break;
				default:
					// Force error ID to 801
					error = 801;
					outputMessage = "Unknown error";
					break;
				}
			}
			else
			{
				error = 101;
				outputMessage = "Not in a game";
			}
		}
		else
		{
			error = 3;
			outputMessage = "Not connected";
		}
		// Finally, send the response to the client.
		send("gameactions", error, outputMessage, null);
	}
	
	/**
	 * Receives a "login" client request and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the request and the reason
	 * of the failure.
	 * @see Documentation/protocol/login.html
	 * @param content the content of the "login" message, should only contains the token
	 *        generated for the bot.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void receiveLogin(JSONObject content) throws JSONException
	{
		// Response message parameters:
		int error = 0;
		String outputMessage;
		JSONObject outputContent = null;
		// First, check if the client is not already logged in as a bot.
		if (isBotLoggedIn() == false)
		{
			// Get the desired gaming mode, default is "regular".
			BotMode mode = BotMode.REGULAR;
			try
			{	// Be careful, "mode" is not necessarily provided by the client!
				// That's why we surround these instructions with a try-catch.
				String modeString = content.getString("mode");
				mode = BotMode.fromString(modeString);
			}
			catch (JSONException e) {}
			// Get the IP address of the client.
			String ip = socket.getInetAddress().getHostAddress();
			try
			{	// Attempt to login the bot on the database
				Bot bot = dbm.login(content.getString("token"), this, mode, ip);
				// No exception caught -> the login was successful.
				this.bot = bot;
				// Notify the server a bot just logged in.
				if (handler != null)
				{
					handler.handleBotLogin(bot);
				}
				outputMessage = "Logged in (" + mode.toString().toLowerCase() + " mode)";
				// If an exception is caught during the creation of the output message,
				// it's not the fault of the client.
				// That's why we surround this block with a try-catch.
				try
				{
					outputContent = new JSONObject();
					outputContent.put("nick", bot.getNick());
					outputContent.put("score", bot.getScore());
				}
				catch (JSONException e)
				{
					LOGGER.error("Cannot write the output content of a \"login\" "
							+ "message:\n" + e.getMessage());
				}
			}
			catch (BotLoginException e)
			{	// Login failed on the database side.
				error = e.getErrorCode();
				switch (error)
				{
				case 101:
					outputMessage = "Token does not exist";
					break;
				case 102:
					outputMessage = "Bot with this token is already connected";
					break;
				default:
					// Force error ID to 801
					error = 801;
					outputMessage = "Unknown error";
					break;
				}
			}
		}
		else
		{
			error = 103;
			outputMessage = "Already connected with bot '" + bot.getNick() + "'";
		}
		// Finally, send the response to the client.
		send("login", error, outputMessage, outputContent);
	}
	
	/**
	 * Receives a "logout" client request and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the request and the reason
	 * of the failure.
	 * @see Documentation/protocol/logout.html
	 * @param content the content of the "logout" message (it should be void in this type
	 *        of message.)
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void receiveLogout(JSONObject content) throws JSONException
	{
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// Check that the client is logged in as a bot.
		if (isBotLoggedIn())
		{
			// We use the logout method that does all the work.
			logout();
			outputMessage = "Logged out";
			this.bot = null;
		}
		else
		{
			error = 3;
			outputMessage = "Not connected";
		}
		// Finally, send the response to the client.
		send("logout", error, outputMessage, null);
	}
	
	/**
	 * Receives a "setmode" client request and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the request and the reason
	 * of the failure.
	 * @see Documentation/protocol/setmode.html
	 * @param content the content of the "setmode" message, should contains the desired
	 *        mode.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void receiveSetMode(JSONObject content) throws JSONException
	{
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// First, test if the client is logged in as a bot.
		if (isBotLoggedIn())
		{
			// Then, get the desired mode and try to set it for the bot.
			String modeString = content.getString("mode");
			BotMode mode = BotMode.fromString(modeString);
			if (mode == BotMode.REGULAR)
			{
				if (bot.getMode() != BotMode.REGULAR)
				{
					bot.setMode(BotMode.REGULAR);
					outputMessage = "Mode changed to regular";
				}
				else
				{
					error = 102;
					outputMessage = "Already in regular mode";
				}
			}
			else if (mode == BotMode.TRAINING)
			{
				if (bot.getMode() != BotMode.TRAINING)
				{
					bot.setMode(BotMode.TRAINING);
					outputMessage = "Mode changed to training";
				}
				else
				{
					error = 102;
					outputMessage = "Already in training mode";
				}
			}
			else
			{
				error = 101;
				outputMessage = "Unknown mode " + mode;
			}
		}
		else
		{
			error = 3;
			outputMessage = "Not connected";
		}
		// Finally, send the response to the client.
		send("setmode", error, outputMessage, null);
	}
	
	/**
	 * Receives a "token" client request and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the request and the reason
	 * of the failure.
	 * @see Documentation/protocol/token.html
	 * @param content the content of the "token" message, should contains the desired
	 *        nickname for the bot to create.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void receiveToken(JSONObject content) throws JSONException
	{
		// Response message parameters:
		int error = 0;
		String outputMessage;
		JSONObject outputContent = null;
		// Check if the bot is not already logged in as the bot
		// (the token demand might be an error)
		if (isBotLoggedIn() == false)
		{
			String nick = content.getString("nick");
			// Check the validity of the desired nickname
			if (isValidNickname(nick))
			{
				// Attempt to create the bot on the database.
				String token = dbm.createBot(nick);
				if (token != null)
				{
					outputContent = new JSONObject();
					// Add the generated token to the content of the message
					outputContent.put("token", token);
					outputMessage = "Bot " + nick + " created";
				}
				else
				{	// A null token means that the token already exists in the DB.
					error = 103;
					outputMessage = "Nickname " + nick + " already exists";
				}
			}
			else
			{
				error = 102;
				outputMessage = "Invalid nickname";
			}
		}
		else
		{
			error = 101;
			outputMessage = "You are currently using a token";
		}
		// Finally, send the response to the client.
		send("token", error, outputMessage, outputContent);
	}
	
	/**
	 * Receives a "killbot" client request and then sends back to the client a report
	 * describing the operation(s) performed or the failure of the request and the reason
	 * of the failure.
	 * @see Documentation/protocol/killbot.html
	 * @param content the content of the "killbot" message, should contains the token of
	 *        the bot to remove.
	 * @throws JSONException if the content is not correctly formed.
	 */
	private void receiveKillBot(JSONObject content) throws JSONException
	{
		// Response message parameters:
		int error = 0;
		String outputMessage;
		// Get the token to remove.
		String token = content.getString("token");
		// Check that the bot is not logged in elsewhere.
		if (dbm.isBotOnline(token) == false)
		{
			// Try to remove the bot.
			if (dbm.removeBot(token))
			{
				outputMessage = "Bot removed";
			}
			else
			{
				error = 101;
				outputMessage = "Token does not exist";
			}
		}
		else
		{
			error = 102;
			outputMessage = "Bot is connected";
		}
		// Finally, send the response to the client.
		send("killbot", error, outputMessage, null);
	}
	
	/**
	 * Sends a message to the TCP client. Messages are formatted in JSON.
	 * @see Documentation/protocol/
	 * @param type the type of message.
	 * @param error the error id (0 if no error.)
	 * @param message a legible string describing the message.
	 * @param content the content of the message, depends on the type of message.
	 */
	private void send(String type, int error, String message, JSONObject content)
	{
		// The JSONObject containing the message.
		JSONObject msgObj = new JSONObject();
		try
		{	// Cook the message.
			msgObj.put("type", type);
			msgObj.put("error", error);
			msgObj.put("message", message);
			msgObj.put("content", content);
		}
		catch (JSONException e)
		{
			LOGGER.error("Cannot put message parameters\n" + e.getMessage());
		}
		// Write the message in the stream.
		__writer__.println(msgObj.toString());
		// Flush the buffer so the client can see the message.
		__writer__.flush();
	}
	
	/**
	 * Sends a "gamestate" message to the client. The client is supposed to return its
	 * actions within a specific time interval.
	 * @see Documentation/protocol/gamestate.html
	 * @param content the object representing the current game state.
	 */
	public void sendGameState(JSONObject content)
	{
		send("gamestate", 0, "Current state of the game, please return your actions",
				content);
	}
	
	/**
	 * Sends a "gamestart" message to the client indicating that a game just started for
	 * him.
	 * @see Documentation/protocol/gamestart.html
	 * @param content the object representing the initial game state.
	 */
	public void sendGameStart(JSONObject content)
	{
		send("gamestart", 0, "A game just started", content);
	}
	
	/**
	 * Sends a "gameend" message to the client indicating that the game just ended.
	 * @see Documentation/protocol/gameend.html
	 * @param content the object representing the ending game state and replay data.
	 */
	public void sendGameEnd(JSONObject content)
	{
		send("gameend", 0, "The game ended", content);
	}
	
	/**
	 * Sends a "gamemute" message to the client indicating that its bot was muted for this
	 * game and hence, can no longer send "gameactions" message until it receives a new
	 * "gamestate" message.
	 * @see Documentation/protocol/gamemute.html
	 * @param content the object giving the reason(s) and additional information(s) about
	 *        the mute.
	 */
	public void sendGameMute(JSONObject content)
	{
		send("gamemute", 0, "You are muted until the end of the game", content);
	}
	
	/**
	 * Sends a welcome message to the client. The client is not supposed to respond.
	 * @see Documentation/protocol/welcome.html
	 */
	public void sendWelcome()
	{
		send("welcome", 0, "Welcome on HelloAnt game server. Have fun challenging other "
				+ "bots!", null);
	}
	
	/**
	 * Attempts to properly logout the bot of the client from the server and notifies the
	 * server of this event.
	 * Nothing happens if the client is not logged in as a bot.
	 * This method is a shortcut for the closing methods which require a proper logout.
	 */
	private void logout()
	{
		if (isBotLoggedIn())
		{
			if (bot.isInGame())
			{
				// TODO: remove the bot from its game.
			}
			// Notify the database the bot is logged out.
			dbm.logout(bot.getNick());
			// Notify the game server the bot is logged out.
			if (handler != null)
			{
				handler.handleBotLogout(bot);
			}
			// Set 'bot' to null so the client is effectively logged out.
			bot = null;
		}
	}
	
	/**
	 * Closes the communicator of a client.
	 * The server will no longer listen to this client. If the client is logged in with a
	 * bot, it will be logged out.
	 * @note this method can be called inside the client thread.
	 */
	private void _close()
	{
		// If the client is logged in, logout it.
		logout();
		// Notify the server its client is disconnected
		if (handler != null)
		{
			handler.handleClientDisconnected(this);
		}
		// Once the client is removed on the 'clients' array, there is no more reference
		// to this instance in the server so it should be garbage collected soon.
		try
		{	// Close the socket
			socket.close();
		}
		catch (IOException e) {}
		// Stop the thread loop by setting 'closed' to true.
		closed = true;
	}
	
	/**
	 * Closes the communicator of a client.
	 * The server will no longer listen to this client. If the client is logged in with a
	 * bot, it will be logged out.
	 * @warning this method is supposed to be called outside the client thread itself!
	 */
	public void close()
	{
		// Call the _close method that does all the work.
		_close();
		// Interrupt the thread so it no longer reads messages from client.
		clientThread.interrupt();
	}
	
	
	/**
	 * Tests whether a nickname is valid according to the nickname specifications.
	 * @see Documentation/protocol/nickspecs.html
	 * @param nick the desired nickname for a bot.
	 * @return true if the nickname is valid, false otherwise.
	 */
	private boolean isValidNickname(String nick)
	{
		return nick.matches("^[a-zA-Z][a-zA-Z0-9]{3,16}$");
	}
	
	/**
	 * Returns whether the client is logged in with a bot.
	 * @return true if the client is logged in, false otherwise
	 */
	public boolean isBotLoggedIn()
	{
		// When a client logs in, his bot object is assigned to the 'bot' property of this
		// class, so we can simply test this property.
		return (bot != null);
	}
	
	/**
	 * Gets the bot of the client.
	 * @return the bot of the client.
	 */
	public Bot getBot()
	{
		return bot;
	}
	
	/**
	 * Mutes a client.
	 * A muted client can still send messages, but the server no longer read those.
	 */
	public void mute()
	{
		muted = true;
	}
	
	/**
	 * Unmutes a client.
	 * As messages sent by a muted client are queued, all messages sent by the client
	 * while it was muted will be read the by communicator.
	 */
	public void unmute()
	{
		muted = false;
	}
}
