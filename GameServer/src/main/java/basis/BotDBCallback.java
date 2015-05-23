package basis;

/**
 * This interface is used by the Bot class to call back the database for certain
 * operations on its data.
 * @interface
 * @author Nicolas
 */
public interface BotDBCallback
{
	/**
	 * Updates the score of a bot in the database.
	 * @param bot the bot whose score needs an update.
	 */
	public void updateBotScore(Bot bot);
}
