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

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.AntGameServer;

public class App implements ActionListener
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	HashMap<Menu, AntGameServer> servers;
	
	private TrayIcon trayIcon;
	private PopupMenu menu;
	
	// TODO "Create server" button
	public App()
	{
		if (!SystemTray.isSupported())
		{
			LOGGER.error("Systray unsupported");
			return;
		}
		servers = new HashMap<Menu, AntGameServer>();
		SystemTray systray = SystemTray.getSystemTray();
		try
		{
			BufferedImage icon = ImageIO.read(new File("./res/imgs/icon.png"));
			trayIcon = new TrayIcon(icon, "HelloAnt");
			// Resize icon, see http://goo.gl/zq3G2J
			int size = trayIcon.getSize().width;
			trayIcon.setImage(icon.getScaledInstance(size, -1, Image.SCALE_SMOOTH));
			
			menu = new PopupMenu();
			
			menu.addSeparator();
			menu.add(createMenuItem("Application log", "fulllog"));
			menu.add(createMenuItem("Error log", "errlog"));
			menu.addSeparator();
			menu.add(createMenuItem("Exit", "exit"));
			trayIcon.setPopupMenu(menu);
			
			systray.add(trayIcon);
		}
		catch (IOException e)
		{
			LOGGER.error("Application icon not found");
			return;
		}
		catch (AWTException e)
		{
			LOGGER.error("SysTray icon creation impossible");
			return;
		}
		startServer(12345);
	}
	
	/**
	 * Creates a menu item bound to `this`.
	 * @param label Label of the MenuItem.
	 * @param command Specific action name associated with this MenuItem.
	 * @return
	 */
	private MenuItem createMenuItem(String label, String command)
	{
		MenuItem item = new MenuItem(label);
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}
	
	/**
	 * Update the tray icon tooltip.
	 */
	private void updateTooltip()
	{
		StringBuilder tooltip = new StringBuilder("HelloAnt");
		int serverCount = servers.size();
		if (serverCount > 0)
		{
			tooltip.append(" - " + serverCount + " server");
			if (serverCount > 1)
				tooltip.append("s");
			tooltip.append(" running");
		}
		trayIcon.setToolTip(tooltip.toString());
	}
	
	/**
	 * Starts a server on a specified port and adds it to the tray icon.
	 * @param port
	 */
	private void startServer(int port)
	{
		AntGameServer newServer;
		try
		{
			newServer = new AntGameServer(port);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.error("unable to start a server on port " + port);
			return;
		}
		Menu serverMenu = new Menu(":" + port);
		MenuItem closeMenuItem = createMenuItem("Close", "closeserver");
		// Uncomment to allow closing servers
		closeMenuItem.setEnabled(false);
		serverMenu.add(closeMenuItem);
		menu.insert(serverMenu, 0);
		servers.put(serverMenu, newServer);
		updateTooltip();
	}

	/**
	 * Called when a menu item is clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		MenuItem src = (MenuItem) e.getSource();
		String cmd = src.getActionCommand();
		if (cmd.equals("exit"))
		{
			System.exit(0);
		}
		else if (cmd.equals("fulllog"))
		{
			try
			{
				Desktop.getDesktop().open(new File("./logs/full.log"));
			}
			catch (IOException ex) {}
		}
		else if (cmd.equals("errlog"))
		{
			try
			{
				Desktop.getDesktop().open(new File("./logs/error.log"));
			}
			catch (IOException ex) {}
		}
		else if (cmd.equals("closeserver"))
		{
			Menu serverMenu = (Menu) src.getParent(); 
			servers.get(serverMenu).close();
			menu.remove(serverMenu);
		}
	}
	
    public static void main(String[] args)
    {
    	new App();
    }
}
