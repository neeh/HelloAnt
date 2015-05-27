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

package ants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basis.Bot;
import basis.FakeCommunicator;

/**
 * This class represents a fake communicator that aims to play with a bot in training
 * mode. Messages are not sent over the network. The communicator immediately handles game
 * state messages.
 * @class
 * @author JMN
 */
public class AntFakeCommunicator extends FakeCommunicator
{
	private static final Logger LOGGER =
			LoggerFactory.getLogger(AntFakeCommunicator.class);
	
	/**
	 * The interface giving a reading access to the game map.
	 */
	private AntGameMapView mapView;
	
	/**
	 * Creates a fake communicator that will send game actions to a training game.
	 * @constructor
	 * @param mapView the interface giving a reading access to the game map.
	 */
	public AntFakeCommunicator(AntGameMapView mapView)
	{
		super();
		this.mapView = mapView;
	}
	
	@Override
	public void run()
	{
		// Do nothing
	}
	
	/**
	 * Gets game objects one cell away in a direction from a specified cell.
	 * @param row the row index of the origin cell.
	 * @param col the column index of the origin cell.
	 * @param direction the direction token from the origin.
	 * @return a list of game objects.
	 */
	private List<AntGameObject> getObjectsAround(int row, int col, char direction)
	{
		switch (direction)
		{
		case 'N':
			row -= 1;
			break;
		case 'E':
			col += 1;
			break;
		case 'W':
			col -= 1;
			break;
		case 'S':
			row += 1;
			break;
		}
		return mapView.getGameObjectsAt(col, row);
	}
	
	/**
	 * This method overrides the communicator's sendGameState method.
	 * No message is sent over the network. The fake communicator reads the input and
	 * returns its game actions immediately.
	 * @see Documentation/protocol/gamestate.html
	 * @param content the content of the "gamestate" message.
	 */
	@Override
	public void sendGameState(JSONObject content)
	{
		Random rand = new Random();
		try
		{
			ArrayList<JSONArray> myAnts = new ArrayList<JSONArray>();
			ArrayList<Character> myMoves = new ArrayList<Character>();
			
			JSONArray objects = content.getJSONArray("gameobjects");
			for (int i = 0; i < objects.length(); i++)
			{
				JSONArray object = objects.getJSONArray(i);
				String type = object.getString(0).toLowerCase();
				if ((type.equals("a") || type.equals("b")) && object.getInt(3) == 0)
				{
					myAnts.add(object);
					StringBuilder untriedMoves = new StringBuilder("NEWS");
					char move = 'N';
					while (untriedMoves.length() > 0)
					{
						// Take a random move not already tried
						int index = rand.nextInt(untriedMoves.length());
						move = untriedMoves.charAt(index);
						untriedMoves.deleteCharAt(index);
						// Get objects in this direction
						List<AntGameObject> objectsAt = getObjectsAround(
								object.getInt(1), object.getInt(2), move);
						boolean moveAvailable = true;
						for (AntGameObject gob : objectsAt)
						{
							// Solid object : we can't go there
							if (gob.isCollideable())
							{
								moveAvailable = false;
								break;
							}
						}
						// If this zone empty, it's ok
						if (moveAvailable) break;
					}
					myMoves.add(move);
				}
			}
			
			// Fill the result with the computed moves.
			JSONArray moves = new JSONArray();
			
			for (int i = 0; i < myAnts.size(); i++)
			{
				JSONArray move = new JSONArray();
				move.put(0, myAnts.get(i).getInt(1)); // row
				move.put(1, myAnts.get(i).getInt(2)); // col
				move.put(2, myMoves.get(i)); // move
				moves.put(move);
			}
			
			JSONObject actions = new JSONObject();
			actions.put("moves", moves);
			
			// "Send" the result to the game.
			bot.getGame().receiveActions(bot, actions);
		}
		catch (JSONException e)
		{
			LOGGER.error("Error while taking actions\n" + e.getMessage());
		}
	}
	
	@Override
	public void sendGameStart(JSONObject content)
	{
		// Do nothing
	}
	
	@Override
	public void sendGameEnd(JSONObject content)
	{
		// Do nothing
	}
	
	@Override
	public void sendGameMute(JSONObject content)
	{
		// Do nothing
	}
	
	/**
	 * Sets the bot of the fake communicator.
	 * The bot constructor requires a communicator so this method is provided to set the
	 * bot of the fake communicator after it has been created.
	 * { @code
	 *   AntFakeCommunicator fakeCom = new AntFakeCommunicator(...);
	 *   Bot fakeBot = new Bot(fakeCom, ...);
	 *   fakeCom.setBot(fakeBot); }
	 * @param bot the bot controlled by the communicator.
	 */
	public void setBot(Bot bot)
	{
		this.bot = bot;
	}
}
