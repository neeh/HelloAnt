package com.polytech.di4.HelloAnt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DBInterface is the class that interfaces with the MySQL database.
 * Its holds the database connection and statements. Every code that needs to interact
 * with the database should use the DBI singleton.
 * @class
 * @author Nicolas
 */
public class DBInterface implements BotDBCallback
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DBInterface.class);
	
	/**
	 * The database interface is a singleton. Hence we hold the created instance here and
	 * serve it when a component needs access to this class.
	 */
	private static DBInterface instance = null;
	
	/**
	 * The MySQL database connection object.
	 * @see Connection
	 */
	private Connection conn;
	
	/**
	 * The MySQL prepared statements used by the server to manage bots.
	 * @See PreparedStatement
	 */
	private PreparedStatement isOnlineStmt;
	private PreparedStatement disconnectStmt;
	private PreparedStatement createBotStmt;
	private PreparedStatement loginSelectStmt;
	private PreparedStatement loginUpdateStmt;
	private PreparedStatement removeBotStmt;
	private PreparedStatement updateBotScoreStmt;
	private PreparedStatement resetBotStatusStmt;
	
	/**
	 * Creates a new database interface and connect to the database using the provided
	 * parameters. Be careful: this class is a singleton so it should be instantiated only
	 * once. That's why the constructor is private.
	 * @constructor
	 * @param dbname the name of the database.
	 * @param username the username used to connected to the database.
	 * @param password the password associated with this username.
	 */
	private DBInterface(String dbname, String username, String password)
	{
		try
		{   // First, connect to the database.
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + dbname +
					"?user=" + username + "&password=" + password);
		}
		catch (SQLException e)
		{
			// Warning, the database connection is not supposed to fail here.
			// If this happens, relaunch the whole archive and pray.
			logSQLException("Cannot connect to MySQL database", e);
			return;
		}
		try
		{   // Then, prepare SQL statements.
			isOnlineStmt = conn.prepareStatement(
					"SELECT COUNT(*) FROM bots WHERE token = ? AND status = 1;");
			disconnectStmt = conn.prepareStatement(
					"UPDATE bots SET status = 0 WHERE nick = ?;");
			createBotStmt = conn.prepareStatement("SELECT NewBot(?);");
			loginSelectStmt = conn.prepareStatement(
					"SELECT status, nick, score FROM bots WHERE token = ? LIMIT 1;");
			loginUpdateStmt = conn.prepareStatement("UPDATE bots SET status = 1, "
					+ "lastLoginDate = NOW(), lastIP = ? WHERE token = ? LIMIT 1;");
			removeBotStmt = conn.prepareStatement("DELETE FROM bots WHERE token = ?;");
			updateBotScoreStmt = conn.prepareStatement(
					"UPDATE bots SET score = ? WHERE nick = ?;");
			resetBotStatusStmt = conn.prepareStatement("UPDATE bots SET status = 0;");
		}
		catch (SQLException e)
		{
			logSQLException("Unable to prepare SQL statements", e);
			return;
		}
		// Clean bot status
		resetBotStatus();
	}
	
	/**
	 * Logs a personalized error message for SQL exceptions.
	 * @param message the explaining of the error.
	 * @param e the reference of the SQLException object.
	 */
	private void logSQLException(String message, SQLException e) {
		// TEMP
		System.out.println(message + "\n"
				+ e.getMessage() + "\n"
				+ e.getSQLState() + "\n"
				+ e.getErrorCode());
		LOGGER.error(message + "\n"
				+ "SQLException: " + e.getMessage() + "\n"
				+ "SQLState: " + e.getSQLState() + "\n"
				+ "VendorError: " + e.getErrorCode());
	}
	
	/**
	 * Initializes the database interface singleton by instantiating the class.
	 * @param dbname the name of the database.
	 * @param username the username used to connected to the database.
	 * @param password the password associated with this username.
	 */
	public static void init(String dbname, String username, String password) {
		if (instance == null) {
			instance = new DBInterface(dbname, username, password);
		} else {
			LOGGER.warn("Attempt to initialize a DBI that was previously initialized");
		}
	}
	
	/**
	 * Serves the database interface singleton.
	 * It's the only way to access the DBInterface once it has been initialized.
	 * @return the database interface.
	 */
	public static DBInterface getInstance() {
		if (instance == null) {
			LOGGER.warn("An uninitialized DBI was returned");
		}
		return instance;
	}
	
	/**
	 * Returns whether a bot having the provided token is connected on the server.
	 * @param token the token of the bot to test.
	 * @return true if the bot is connected, false otherwise.
	 */
	public Boolean isBotOnline(String token) {
		// The number of result:
		int count = 0;
		ResultSet result = null;
		try {
			isOnlineStmt.setString(1, token);
			result = isOnlineStmt.executeQuery();
			if (result.next()) {
				count = result.getInt(1);
			}
		} catch (SQLException e) {
			logSQLException("Cannot execute the SQL statement for testing the status of a"
					+ " bot", e);
		} finally {
			// Release ResultSet.
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {}
				result = null;
			}
		}
		return count > 0;
	}
	
	public Bot login(String token, TCPClientCommunicator com, BotMode mode, String ip)
			throws BotLoginException
	{
		Bot bot = null;
		ResultSet result;
		
		try
		{
			loginSelectStmt.setString(1, token);
			result = loginSelectStmt.executeQuery();
			if (result.next())
			{
				if (result.getBoolean(1) == false)
				{
					bot = new Bot(com, result.getString(2), mode, result.getDouble(3),
							this);
					loginUpdateStmt.setString(1, ip);
					loginUpdateStmt.setString(2, token);
					loginUpdateStmt.executeUpdate();
				}
				else
				{
					throw new BotLoginException(102);
				}
			}
			else
			{
				throw new BotLoginException(101);
			}
			//loginSelectStmt.setString(1, token);
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statements for logging a bot", e);
			return null;
		}
		return bot;
	}
	
	/**
	 * Disconnect a bot on the database by setting its status to 0. The bot to disconnect
	 * is identified by its nickname.
	 * @param nick the nickname of the bot to disconnect.
	 */
	public void disconnect(String nick)
	{
		try
		{
			// Here, we don't have to care about SQL injections because we're
			// disconnecting the bot using a prepared statement.
			disconnectStmt.setString(1, nick);
			disconnectStmt.executeUpdate();
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for disconnecting a bot",
					e);
		}
	}
	
	/**
	 * Attempts to create a new bot in the database using the given nickname.
	 * The nickname is supposed to be valid here.
	 * Returns null if the token is already used.
	 * @param nick the valid nickname of the bot.
	 * @return the generated token for the created bot OR null if the nickname is already
	 *         taken by another bot.
	 */
	public String createBot(String nick)
	{
		// The token that will be returned.
		String token = null;
		ResultSet result = null;
		try
		{
			// Here, we don't have to care about SQL injections because we're creating the
			// bot using prepared statement + MySQL stored function.
			createBotStmt.setString(1, nick);
			result = createBotStmt.executeQuery();
			if (result.next())
			{
				token = result.getString(1);
			}
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for creating a bot", e);
		}
		finally
		{
			// Release ResultSet.
			if (result != null)
			{
				try
				{
					result.close();
				}
				catch (SQLException e)
				{
					
				}
				result = null;
			}
		}
		return token;
	}
	
	/**
	 * Attempts to remove the bot having to provided token in the database.
	 * Returns false if the token is not present in the data base.
	 * @param token the token of the bot to remove.
	 * @return true if the bot was successfully removed, false otherwise.
	 */
	public Boolean removeBot(String token) {
		int result = 0;
		try {
			// Here, we don't have to care about SQL injections because we're removing the
			// bot using a prepared statement.
			removeBotStmt.setString(1, token);
			result = removeBotStmt.executeUpdate();
		} catch (SQLException e) {
			logSQLException("Cannot execute the SQL statement for removing a bot", e);
		}
		return result > 0;
	}
	
	/**
	 * Updates the score of a bot in the database.
	 * @param bot the bot whose score needs an update.
	 */
	@Override
	public void updateBotScore(Bot bot)
	{
		try
		{
			updateBotScoreStmt.setDouble(1, bot.getScore());
			updateBotScoreStmt.setString(2, bot.getNick());
			updateBotScoreStmt.executeUpdate();
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for updating the score of a"
					+ " bot", e);
		}
	}
	
	/**
	 * Resets the status of every bot status to "logged out". If the server previously
	 * crashed, it is strongly possible that some bots are still referred as "logged in"
	 * in the database.
	 * Should be called once at the start-up of the database connection to clean bot
	 * status.
	 */
	private void resetBotStatus()
	{
		try
		{
			resetBotStatusStmt.executeUpdate();
		}
		catch (SQLException e)
		{
			logSQLException( "Cannot execute the SQL statement for resetting bot status",
					e);
		}
	}
}
