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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages interactions with the MySQL database.
 * Its holds the database connection and statements. Any component that needs to interact
 * with the database should use the DBM singleton.
 * @see Database/dbants.sql
 * @class
 * @author Nicolas
 */
public class DBManager implements BotDBCallback
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);
	
	/**
	 * The database manager is a singleton. Hence we hold the created instance here and
	 * serve it when a component needs access to this class.
	 */
	private static DBManager instance = null;
	
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
	private PreparedStatement loginSelectStmt;
	private PreparedStatement loginUpdateStmt;
	private PreparedStatement logoutStmt;
	private PreparedStatement createBotStmt;
	private PreparedStatement removeBotStmt;
	private PreparedStatement updateBotScoreStmt;
	private PreparedStatement resetBotStatusStmt;
	
	/**
	 * Creates a new database manager and connects to the database using the provided
	 * parameters. Be careful: this class is a singleton so it should be instantiated only
	 * once. That's why the constructor is private.
	 * @constructor
	 * @param dbname the name of the database.
	 * @param username the username used to connected to the database.
	 * @param password the password associated with this username.
	 */
	private DBManager(String dbname, String username, String password)
	{
		try
		{   // First, connect to the database.
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + dbname +
					"?user=" + username + "&password=" + password);
		}
		catch (SQLException e)
		{	// Warning, the database connection is not supposed to fail here.
			// If this happens, relaunch the whole archive and pray.
			logSQLException("Cannot connect to MySQL database", e);
			return;
		}
		try
		{   // Then, prepare SQL statements.
			isOnlineStmt = conn.prepareStatement(
					"SELECT COUNT(*) FROM bots WHERE token = ? AND status = 1;");
			loginSelectStmt = conn.prepareStatement(
					"SELECT status, nick, score FROM bots WHERE token = ? LIMIT 1;");
			loginUpdateStmt = conn.prepareStatement("UPDATE bots SET status = 1, "
					+ "lastLoginDate = NOW(), lastIP = ? WHERE token = ? LIMIT 1;");
			logoutStmt = conn.prepareStatement(
					"UPDATE bots SET status = 0 WHERE nick = ?;");
			createBotStmt = conn.prepareStatement("SELECT NewBot(?);");
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
		// Clean bots status.
		resetBotStatus();
	}
	
	/**
	 * Logs a personalized error message for a SQL exception.
	 * @param message a message that explains the error.
	 * @param e the reference of the SQLException object.
	 */
	private void logSQLException(String message, SQLException e)
	{
		LOGGER.error(message + "\n"
				+ "SQLException: " + e.getMessage() + "\n"
				+ "SQLState: " + e.getSQLState() + "\n"
				+ "VendorError: " + e.getErrorCode());
	}
	
	/**
	 * Initializes the database manager singleton by instantiating the class.
	 * @param dbname the name of the database.
	 * @param username the username used to connected to the database.
	 * @param password the password associated with this username.
	 */
	public static void init(String dbname, String username, String password)
	{
		if (instance == null)
		{
			instance = new DBManager(dbname, username, password);
		}
	}
	
	/**
	 * Serves the database manager singleton.
	 * It's the only way to access the DBManager singleton.
	 * @return the database manager.
	 * @throws IllegalStateException if the singleton was not initialized.
	 */
	public static DBManager getInstance() throws IllegalStateException
	{
		if (instance == null)
		{
			throw new IllegalStateException("The database manager singleton was not "
					+ "initialized");
		}
		return instance;
	}
	
	/**
	 * Returns whether a bot having the provided token is logged on the server.
	 * @param token the token of the bot to test.
	 * @return true if the bot is logged in, false otherwise.
	 */
	public Boolean isBotOnline(String token)
	{
		// The number of result:
		int count = 0;
		ResultSet result = null;
		try
		{	// SELECT COUNT(*) FROM bots WHERE token = ? AND status = 1;
			isOnlineStmt.setString(1, token);
			result = isOnlineStmt.executeQuery();
			if (result.next())
			{
				count = result.getInt(1);
			}
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for checking a bot", e);
		}
		finally
		{
			if (result != null)
			{	// Release ResultSet.
				try
				{
					result.close();
				}
				catch (SQLException e) {}
				result = null;
			}
		}
		return count > 0;
	}
	
	/**
	 * Attempts to login bot on the database.
	 * @see Documentation/protocol/login.html for error codes.
	 * @param token the token generated for the bot.
	 * @param com the communicator used to communicate with the bot.
	 * @param mode the desired gaming mode of the bot.
	 * @param ip the IP address of the client.
	 * @return a new bot if the login succeed.
	 * @throws BotLoginException if the login failed.
	 */
	public Bot login(String token, TCPClientCommunicator com, BotMode mode, String ip)
			throws BotLoginException
	{
		Bot bot = null;
		ResultSet result = null;
		try
		{	// SELECT status, nick, score FROM bots WHERE token = ? LIMIT 1;
			loginSelectStmt.setString(1, token);
			result = loginSelectStmt.executeQuery();
			if (result.next())
			{
				if (result.getBoolean(1) == false)
				{	// (status = 0 = false) implies that the bot is not logged in.
					// We can login it safely.
					bot = new Bot(com, result.getString(2), mode, result.getDouble(3),
							this);
					// UPDATE bots SET status = 1, lastLoginDate = NOW(), lastIP = ?
					// WHERE token = ? LIMIT 1;
					loginUpdateStmt.setString(1, ip);
					loginUpdateStmt.setString(2, token);
					loginUpdateStmt.executeUpdate();
				}
				else
				{	// The bot is already logged in on another client.
					throw new BotLoginException(102);
				}
			}
			else
			{	// No result therefore no bot is associated with the given token.
				throw new BotLoginException(101);
			}
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statements for logging a bot", e);
			return null;
		}
		finally
		{
			if (result != null)
			{	// Release ResultSet.
				try
				{
					result.close();
				}
				catch (SQLException e) {}
				result = null;
			}
		}
		return bot;
	}
	
	/**
	 * Logs out a bot from the database by setting its status to 0.
	 * The bot to log out is identified by its nickname.
	 * @param nick the nickname of the bot to log out.
	 */
	public void logout(String nick)
	{
		try
		{	// UPDATE bots SET status = 0 WHERE nick = ?;
			// Here, we don't have to care about SQL injections because we're
			// disconnecting the bot using a prepared statement.
			logoutStmt.setString(1, nick);
			logoutStmt.executeUpdate();
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for logging a bot out", e);
		}
	}
	
	/**
	 * Attempts to create a new bot with the given nickname in the database.
	 * The nickname is supposed to be valid here.
	 * Returns null if the token is already used.
	 * @see Database/dbants.sql for the complete statement.
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
		{	// SELECT NewBot(?);
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
			if (result != null)
			{	// Release ResultSet.
				try
				{
					result.close();
				}
				catch (SQLException e) {}
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
		try
		{	// DELETE FROM bots WHERE token = ?;
			// Here, we don't have to care about SQL injections because we're removing the
			// bot using a prepared statement.
			removeBotStmt.setString(1, token);
			result = removeBotStmt.executeUpdate();
		}
		catch (SQLException e)
		{
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
		{	// UPDATE bots SET score = ? WHERE nick = ?;
			updateBotScoreStmt.setDouble(1, bot.getScore());
			updateBotScoreStmt.setString(2, bot.getNick());
			updateBotScoreStmt.executeUpdate();
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for updating bot score", e);
		}
	}
	
	/**
	 * Resets the status of every bot to "logged out".
	 * If the server previously crashed, it is strongly possible that some bots are still
	 * referred as "logged in" in the database.
	 * Should be called once at the start-up of the database connection to prevent unfair
	 * impossible logins.
	 */
	private void resetBotStatus()
	{
		try
		{	// UPDATE bots SET status = 0;
			resetBotStatusStmt.executeUpdate();
		}
		catch (SQLException e)
		{
			logSQLException("Cannot execute the SQL statement for cleaing bot status", e);
		}
	}
}
