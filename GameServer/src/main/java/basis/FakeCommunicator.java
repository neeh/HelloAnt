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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.AntGameMapView;
import ants.AntGameObject;

/**
 * Create a fake communicator to use in training mode
 * @class
 */
public class FakeCommunicator extends TCPClientCommunicator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(
			FakeCommunicator.class);
	
	private Bot bot;
	private AntGameMapView mapView;
	
	/**
	 * Create a fake communicator and a training bot using it
	 * @param mapView a view of the map of the currentGame
	 */
	public FakeCommunicator(AntGameMapView mapView)
	{
		super(null, null);
		this.bot = new Bot(this, "Training Bot", BotMode.TRAINING, 0, null);
		this.mapView = mapView;
	}
	
	@Override
	public void run()
	{
		// Do nothing
	}
	
	@Override
	public Boolean isBotLoggedIn()
	{
		return true;
	}
	
	@Override
	public void sendGameStart(JSONObject content)
	{
		
	}
	
	/**
	 * Get objects one cell away in a direction from a specified cell
	 * @param row row of the origin cell
	 * @param col column of the origin cell
	 * @param direction direction from the origin
	 * @return a list of AntGameObjects
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
		return mapView.getGameObjectsAt(row, col);
	}
	
	/**
	 * Override the communicator sendGameState function
	 * Does not send any message but instead tells immediately the game that a game action is received
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
				if(object.getString(0).equals("a") && object.getInt(3) == 0)
				{
					myAnts.add(object);
					StringBuilder untriedMoves = new StringBuilder("NEWS");
					char move = 'N';
					while(untriedMoves.length() > 0)
					{
						// Take a random move not already tried
						int index = rand.nextInt(untriedMoves.length());
						move = untriedMoves.charAt(index);
						untriedMoves.deleteCharAt(index);
						// Get objects in this direction
						List<AntGameObject> objectsAt = getObjectsAround(object.getInt(1), object.getInt(2), move);
						boolean moveAvailable = true;
						for (AntGameObject antGameObject : objectsAt)
						{
							// Solid object : we can't go there
							if (!antGameObject.isCollideable())
							{
								moveAvailable = false;
								break;
							}
						}
						// If this zone empty, it's ok
						if (moveAvailable)
						{
							break;
						}
					}
					myMoves.add(move);
				}
			}
			
			// Fill the result with the computed moves 
			JSONArray moves = new JSONArray();
			
			for (int i = 0; i < myAnts.size(); i++)
			{
				JSONObject move = new JSONObject();
				move.put("col", myAnts.get(i).getInt(2));
				move.put("row", myAnts.get(i).getInt(1));
				move.put("move", myMoves.get(i));
				moves.put(move);
			}
			
			JSONObject actions = new JSONObject();
			actions.put("moves", moves);
			
			// "Send" the result to the game
			bot.getGame().receiveActions(bot, actions);
		}
		catch (JSONException e)
		{
			LOGGER.error("Error while taking actions\n" + e.getMessage());
		}
	}
	
	@Override
	public void sendGameEnd(JSONObject content)
	{
		
	}
	
	@Override
	public void sendGameMute(JSONObject content)
	{
		
	}
	
	@Override
	public Bot getBot()
	{
		return bot;
	}
}
