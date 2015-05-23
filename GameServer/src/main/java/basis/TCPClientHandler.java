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

public interface TCPClientHandler
{
	/**
	 * Notifies the server that a client just connected on the server.
	 * @param newClient the client to add on the game server.
	 */
	public void handleClientConnected(TCPClientCommunicator newClient);
	
	/**
	 * Notifies the server that a client just disconnected from the server.
	 * @param oldClient the client to remove from the game server.
	 */
	public void handleClientDisconnected(TCPClientCommunicator oldClient);
	
	/**
	 * Notifies the game server that a client just logged as a bot.
	 * @param newBot the bot which logged in.
	 */
	public void handleBotLogin(Bot newBot);
	
	/**
	 * Notifies the game server a client just logged out its bot.
	 * @param oldBot the bot which logged out.
	 */
	public void handleBotLogout(Bot oldBot);
}
