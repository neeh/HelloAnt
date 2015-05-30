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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import basis.GameServer;

/**
 * This class implements a game server specific to the game of ants.
 * It's responsible for managing high-level interactions between clients and games.
 * @class
 * @author Nicolas
 */
public class AntGameServer extends GameServer
{
	/**
	 * The timer used to schedule the game management.
	 */
	private Timer managerTimer;
	
	/**
	 * Creates a new game server for an ant game.
	 * @param port the port to listen for client interactions.
	 */
	public AntGameServer(int port)
	{
		super(port);
		// Load map files.
		ArrayList<AntMapTemplate> mapTemplates = loadMapFiles();
		if (mapTemplates.size() < 1)
		{	// The server needs at least one map to work normally.
			LOGGER.error("No map could be loaded");
			System.exit(-1);
		}
		gameManager = new AntGameManager(this, mapTemplates);
		// Schedule game creation task
		managerTimer = new Timer();
		long delay = 1000L; // 1s
		long period = 3000L; // 3s
		managerTimer.scheduleAtFixedRate(gameManager, delay, period);
	}
	
	/**
	 * Closes the server and every associated thread.
	 */
	@Override
	public void close()
	{
		managerTimer.cancel();
		super.close();
	}
	
	/**
	 * Loads all maps files available in GameServer/res/maps/.
	 * @return map templates loaded from the map folder.
	 */
	private ArrayList<AntMapTemplate> loadMapFiles()
	{
		LOGGER.info("working directory: " + System.getProperty("user.dir"));
		ArrayList<AntMapTemplate> mapTemplates = new ArrayList<AntMapTemplate>();
		
		// !!!!! DEBUG !!!!!
		/*AntMapTemplate debug_map = new AntMapTemplate();
		try
		{
			debug_map.loadFromFile("debug/2.map");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidMapFormatException e)
		{
			e.printStackTrace();
		}
		mapTemplates.add(debug_map);
		if(true) return mapTemplates;*/
		// !!!!! /DEBUG !!!!!
		
		// Get all the files in GameServer/res/maps/
		File f = new File("./res/maps/");
		File[] files = f.listFiles();
		for (File file : files)
		{
			if (file.isDirectory() == false)
			{
				String filename = file.getName();
				LOGGER.info("map loaded: " + filename);
				try
				{	// Attempt to parse the file.
					AntMapTemplate map = new AntMapTemplate();
					map.loadFromFile(filename);
					mapTemplates.add(map);
				}
				catch (InvalidMapFormatException e)
				{	// May not be a map file...
					LOGGER.error("Cannot parse file '" + filename + "': " +
							e.getMessage());
				}
				catch (IOException e)
				{
					LOGGER.error("Cannot read file '" + filename + "'");
				}
			}
		}
		return mapTemplates;
	}
}
