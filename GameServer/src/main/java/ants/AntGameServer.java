package ants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import basis.GameServer;

/**
 * This class implements a game server specific to the game of ants.
 * It's responsible for managing high-level interactions between clients and games.
 * @author Nicolas
 */
public class AntGameServer extends GameServer
{
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
		AntGameManager gameManager = new AntGameManager(this, mapTemplates);
		// Schedule game creation task
		Timer timer = new Timer();
		long delay = 1000L; // 1s
		long period = 3000L; // 3s
		timer.scheduleAtFixedRate(gameManager, delay, period);
	}
	
	/**
	 * Loads all maps files available in GameServer/res/maps/.
	 * @return map templates loaded from the map folder.
	 */
	private ArrayList<AntMapTemplate> loadMapFiles()
	{
		LOGGER.info("working directory: " + System.getProperty("user.dir"));
		ArrayList<AntMapTemplate> mapTemplates = new ArrayList<AntMapTemplate>();
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
        			LOGGER.error("Cannot parse file '" + filename + "': " + e.getMessage());
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
