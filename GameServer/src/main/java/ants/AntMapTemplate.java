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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Cell;

/**
 * The AntMapTemplate class represents a template of an ant game map.
 * It's responsible for holding map data like interactive game objects and cell type in
 * memory. The content of this class is not supposed to be modified during runtime.
 * The class may be accessed in the following cases :
 * - creation of a new game based on the number of required bots;
 * - initiation of a game (setup game objects, spawns, etc...).
 * The class also interfaces with map files (loading/saving).
 * @see Documentation/specifications/mapformat.html
 * @class
 * @author Nicolas
 */
public class AntMapTemplate
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AntMapTemplate.class);
	
	/** The minimum number of bots a map can take in. */
	private static final int MIN_BOTCOUNT = 2;
	
	/** The maximum number of bots a map can have. */
	// Warning: the maximum number of bots should be less than or equals to 10.
	//          this constraint is imposed by the map file format.
	private static final int MAX_BOTCOUNT = 10;
	
	/** The minimum number of columns a map can have. */
	private static final int MIN_COLS = 30;
	
	/** The maximum number of columns a map can have. */
	private static final int MAX_COLS = 900;
	
	/** The minimum number of rows a map can have. */
	private static final int MIN_ROWS = 30;
	
	/** The maximum number of rows a map can have. */
	private static final int MAX_ROWS = 900;
	
	/**
	 * The number of bots the map is designed for.
	 * The game manager is supposed to supply this exact amount of bots to use this map.
	 * @note the bot count should be in [MIN_BOTCOUNT; MAX_BOXCOUNT].
	 */
	private int botCount;
	
	/**
	 * The number of columns of the map.
	 * @note should be in [MIN_COLS; MAX_COLS]
	 */
	private int cols;
	
	/**
	 * The number of rows of the map.
	 * @note should be in [MIN_ROWS; MAX_ROWS]
	 */
	private int rows;
	
	/**
	 * The list of ant hills for each bot on the map.
	 */
	private ArrayList<ArrayList<Cell>> hills;
	
	/**
	 * The list of food spawns placed on the map.
	 */
	private ArrayList<Cell> foodSpawns;
	
	/**
	 * The list of walls on the map.
	 */
	private ArrayList<Cell> walls;
	
	/**
	 * Creates a new ant map template.
	 * There is no parameter for this constructor because the map template is supposed to
	 * be loaded from a external file or generated internally.
	 * @constructor
	 */
	public AntMapTemplate()
	{
		botCount = 0;
		cols = 0;
		rows = 0;
	}
	
	/**
	 * Generates a map template.
	 * @param botCount the number of bots the map is designed for.
	 * @param cols the desired number of columns.
	 * @param rows the desired number of rows.
	 */
	public void generate(int botCount, int cols, int rows)
	{
		// TODO: for future projects, implement this method.
	}
	
	/**
	 * Loads an ant map from a file. All the map parameters previously present in the
	 * structure will be replaced if the parsing is successful.
	 * @see Documentation/specifications/mapformat.html
	 * @param filename the name of the map file.
	 */
	public void loadFromFile(String filename)
			throws IOException, InvalidMapFormatException
	{
		// Get the path of the map file.
		Path file = FileSystems.getDefault().getPath("res/maps/", filename);
		Charset charset = Charset.forName("utf-8");
		// Create temporary parameters.
		// If the file parsing fails, everything should be deleted.
		int _botCount = -1;
		int _cols = -1;
		int _rows = -1;
		ArrayList<ArrayList<Cell>> _hills = new ArrayList<ArrayList<Cell>>();
		ArrayList<Cell> _foodSpawns = new ArrayList<Cell>();
		ArrayList<Cell> _walls = new ArrayList<Cell>();
		// Map iterators:
		int i = 0;	// row iterator
		int j = 0;	// column iterator
		// Read the lines of the map file one by one.
		// http://goo.gl/PQDwqp
		BufferedReader reader = Files.newBufferedReader(file, charset);
		String line = reader.readLine();
		while (line != null)
		{
			if (line.startsWith("m "))
			{
				if (_botCount == -1 || _cols == -1 || _rows == -1)
				{
					throw new InvalidMapFormatException("'m' token found before 'players'"
							+ " or 'cols' or 'rows' token");
				}
				if (i == _rows)
				{
					throw new InvalidMapFormatException("There are more than the " + _rows
							+ " rows that were specified in the map file");
				}
				// Keep only map data
				line = line.substring(2);
				if (line.length() < _cols)
				{
					throw new InvalidMapFormatException("row " + i + ": some columns are "
							+ "missing");
				}
				// Extract all game objects of the current row:
				for (j = 0; j < _cols; j++)
				{
					// %=wall, *=foodSpawn, 0-9=hill (sorted by frequency) + .=ground
					char c = line.charAt(j);
					if (c == '%')
					{
						_walls.add(new Cell(j, i));
					}
					else if (c == '*')
					{
						_foodSpawns.add(new Cell(j, i));
					}
					else if (c >= '0' && c <= '9')
					{
						// hill identifier
						int id = c - '0';
						// Check that we are not creating a hill for a bot that does
						// not exist.
						if (id >= _botCount)
						{
							throw new InvalidMapFormatException("cell " + j + ", " + i
									+ ": The hill identifier is greater than the number "
									+ "of bot");
						}
						// Add the hill for the bot 'id'.
						_hills.get(id).add(new Cell(j, i));
					}
					else if (c != '.')
					{
						// Warning: incorrect cell character found!
						// assuming that was a ground cell...
					}
				}
				i++;
			}
			else if (line.startsWith("players "))
			{
				_botCount = Integer.parseInt(line.substring(8));
				// Check that the number of bots is in the specified bounds.
				if (_botCount > MAX_BOTCOUNT)
				{
					throw new InvalidMapFormatException("Number of players is too high");
				}
				else if (_botCount < MIN_BOTCOUNT)
				{
					throw new InvalidMapFormatException("Number of players is too low");
				}
				// Create a list of hills for every bot.
				for (int b = 0; b < _botCount; b++)
				{
					_hills.add(new ArrayList<Cell>());
				}
			}
			else if (line.startsWith("rows "))
			{
				_rows = Integer.parseInt(line.substring(5));
				// Check that the number of rows is in the specified bounds.
				if (_rows > MAX_ROWS)
				{
					throw new InvalidMapFormatException("Number of rows is too high");
				}
				else if (_rows < MIN_ROWS)
				{
					throw new InvalidMapFormatException("Number of rows is too low");
				}
			}
			else if (line.startsWith("cols "))
			{
				_cols = Integer.parseInt(line.substring(5));
				// Check that the number of columns is in the specified bounds.
				if (_cols > MAX_COLS)
				{
					throw new InvalidMapFormatException("Number of columns is too high");
				}
				else if (_cols < MIN_COLS)
				{
					throw new InvalidMapFormatException("Number of columns is too low");
				}
			}
			// Read the next line in the map file.
			line = reader.readLine();
		}
		// Map parsing ends here. Check that no row is missing:
		if (i < _rows)
		{
			// The number of missing rows.
			int miss = _rows - i;
			throw new InvalidMapFormatException(miss + " row"
					+ (miss > 1 ? "s are" : " is") + " missing from the map file");
		}
		// Ok, set the new map parameters.
		botCount = _botCount;
		cols = _cols;
		rows = _rows;
		hills = _hills;
		foodSpawns = _foodSpawns;
		walls = _walls;
		// The previous map parameters will be garbage collected soon.
	}
	
	/**
	 * Saves the ant map to a text file with respect to the map format conventions.
	 * If the file already exists, it will be overwritten.
	 * This method is provided to be used jointly to the map generation.
	 * @see Documentation/specifications/mapformat.html
	 * @param filename the name of the map file.
	 */
	public void saveToFile(String filename)
	{
		// Get the path of the map file.
		Path file = FileSystems.getDefault().getPath("res/maps/", filename);
		Charset charset = Charset.forName("utf-8");
		// http://goo.gl/PQDwqp
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset))
		{
			writer.write("# " + Calendar.getInstance().getTime() + "\r\n");
			writer.write("players " + botCount + "\r\n");
			writer.write("rows " + rows + "\r\n");
			writer.write("cols " + cols + "\r\n");
			// Call the _toStringArray that does all the map parsing.
			ArrayList<StringBuilder> stringBuilders = _toStringArray();
			// Write the rows one by one.
			Iterator<StringBuilder> stringBuilderIt = stringBuilders.iterator();
			StringBuilder stringBuilder;
			while (stringBuilderIt.hasNext())
			{
				stringBuilder = stringBuilderIt.next();
				writer.write("m " + stringBuilder.toString() + "\r\n");
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Cannot write to output file '" + filename + ".map': "
					+ e.getMessage());
		}
	}
	
	/**
	 * Exports the map as a JSON object containing the map data.
	 * This method is used to generate the content of the "map" attribute of the replay
	 * format of an ant game.
	 * @see Documentation/protocol/replayformat.html
	 * @return the JSONObject representing the map.
	 */
	public JSONObject toJSON()
	{
		// The object to return.
		JSONObject object = new JSONObject();
		// The map data, it's an array of string.
		JSONArray array = new JSONArray();
		// Call the _toStringArray that does all the map parsing.
		ArrayList<StringBuilder> stringBuilders = _toStringArray();
		// Insert the string representations of the map rows into the array.
		Iterator<StringBuilder> stringBuilderIt = stringBuilders.iterator();
		StringBuilder stringBuilder;
		while (stringBuilderIt.hasNext())
		{
			stringBuilder = stringBuilderIt.next();
			array.put(stringBuilder.toString());
		}
		try
		{	// Add the parameters of the "map".
			object.put("cols", cols);
			object.put("rows", rows);
			object.put("data", array);
		}
		catch (JSONException e)
		{
			LOGGER.error("Cannot create the json representation of a map template\n"
					+ "a void JSONObject was returned instead"
					+ e.getMessage());
		}
		return object;
	}
	
	/**
	 * Gets the string array representation of the map.
	 * This method is used to generate the map rows as string for other export methods.
	 * We use StringBuilder objects to simplify the cooking of the strings.
	 * @return a list of strings representing the rows of the map.
	 */
	private ArrayList<StringBuilder> _toStringArray()
	{
		// The string array to return.
		ArrayList<StringBuilder> stringBuilders = new ArrayList<StringBuilder>(rows);
		int i, j;
		// First, init all cells to ground
		for (i = 0; i < rows; i++)
		{
			StringBuilder rowBuilder = new StringBuilder(cols);
			for (j = 0; j < cols; j++)
			{
				rowBuilder.append('.');
			}
			stringBuilders.add(rowBuilder);
		}
		// Define a cell-iterator and a current cell
		Iterator<Cell> cellIt;
		Cell cell;
		// Place hills
		for (i = 0; i < botCount; i++)
		{
			// i is the bot index here
			cellIt = hills.get(i).iterator();
			while (cellIt.hasNext())
			{
				cell = cellIt.next();
				stringBuilders.get(cell.getRow()).setCharAt(cell.getCol(),
						(char) (i + '0'));
			}
		}
		// Place food spawns
		cellIt = foodSpawns.iterator();
		while (cellIt.hasNext())
		{
			cell = cellIt.next();
			stringBuilders.get(cell.getRow()).setCharAt(cell.getCol(), '*');
		}
		// Place walls
		// We do place walls at the end thereby other game objects cannot override walls.
		cellIt = walls.iterator();
		while (cellIt.hasNext())
		{
			cell = cellIt.next();
			stringBuilders.get(cell.getRow()).setCharAt(cell.getCol(), '%');
		}
		return stringBuilders;
	}
	
	/**
	 * Gets the number of bots the map is designed for.
	 * @return the number of concurrent bots the map accepts.
	 */
	public int getBotCount()
	{
		return botCount;
	}
	
	/**
	 * Gets the total number of columns of the map.
	 * @return the column count for this map.
	 */
	public int getCols()
	{
		return cols;
	}
	
	/**
	 * Gets the total number of rows of the map.
	 * @return the row count for this map.
	 */
	public int getRows()
	{
		return rows;
	}
	
	/**
	 * Gets the list of ant hills for each bot on the map.
	 * @return the list of ant hills.
	 */
	public ArrayList<ArrayList<Cell>> getHills()
	{
		return hills;
	}
	
	/**
	 * Gets the list of food spawns placed on the map.
	 * @return the list of food spawns.
	 */
	public ArrayList<Cell> getFoodSpawns()
	{
		return foodSpawns;
	}
	
	/**
	 * Gets the list of walls on the map.
	 * @return the list of walls.
	 */
	public ArrayList<Cell> getWalls()
	{
		return walls;
	}
}
