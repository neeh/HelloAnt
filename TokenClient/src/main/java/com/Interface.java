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

package com;

import java.awt.Button;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * Class which permit to instance an interface for inscription.
 * There is a text field for the bot name  and one for the server address.
 * When the user click on the enter button, the bot name is sent to the game server via socket using the RegisterSocket class.
 * @see RagisterSocket
 * @author Benjamin
 *
 */
public class Interface 
{

	private JFrame window= new JFrame("Log the new bot");
	private JPanel pan= new JPanel();
	private Button buttonOk= new Button("Enter");
	@SuppressWarnings("unused")
	private Button buttonQuit=new Button("Exit");
	private Button buttonCopy=new Button("Copy token to clipboard");
	private JLabel botNameLab=new JLabel("Enter the bot name :");
	private JLabel serverAddress=new JLabel("Enter the server adress :");
	private JTextField botNameField=new JTextField(20);
	private JTextField serverAddressField=new JTextField(20);
	private JLabel tokenLab=new JLabel("Token : ");
	private JLabel tokenField=new JLabel("");
	
	public Interface()
	{
		
		GroupLayout layout = new GroupLayout(pan);
		pan.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(botNameLab)
						.addComponent(serverAddress)
						.addComponent(tokenLab))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(botNameField)
						.addComponent(serverAddressField)
						.addComponent(tokenField))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(buttonOk)
						.addComponent(buttonCopy))
						);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(botNameLab)
						.addComponent(botNameField))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(serverAddress)
						.addComponent(serverAddressField)
						.addComponent(buttonOk))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(tokenLab)
						.addComponent(tokenField)
						.addComponent(buttonCopy)));
		buttonCopy.addActionListener(new ListenerCopy());
		buttonOk.addActionListener(new ListenerSend());
		window.add(pan);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	private class ListenerCopy implements ActionListener{ 
    	
    	public void actionPerformed(ActionEvent e)
    	{
    		StringSelection stringSelection = new StringSelection (tokenField.getText());
    		Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
    		clpbrd.setContents (stringSelection, null);
    	}
	}
/**
 * Listener linked with the enter button.
 * It sends the botName with the server address given in serverAddressField.
 * It tests the conformity of the botname.
 * @uses isBotNameValid
 * @uses RegisterSocket
 * @author Benjamin
 *
 */

	private class ListenerSend implements ActionListener
	{ 
	
		public void actionPerformed(ActionEvent e)
		{
			String botNameString = new String (botNameField.getText());
			if (isBotNameValid(botNameString))
			{
				InetAddress serverIp=null;
				try
				{
					serverIp = InetAddress.getByName(serverAddressField.getText());
				}
				catch (UnknownHostException e1)
				{
					javax.swing.JOptionPane.showMessageDialog(null,e1.getMessage()+" Does not match");
					return;
				}
			
				try 
				{
					RegisterSocket test = new RegisterSocket(botNameString,serverIp);
					if (test.getResult()!=null)
					{
						tokenLab.setText(test.getResult());
					}
				
				} 
			
				catch (IOException e1) 
				{
					javax.swing.JOptionPane.showMessageDialog(null,e1.getMessage());
					return;
				}
				catch (JSONException e2) 
				{
					javax.swing.JOptionPane.showMessageDialog(null,e2.getMessage());
					return;
				}
			
			}
		}	
	}
	/**
	 * Checks if the bot name is correct. It must uses alphanumeric characters. 3 minimum, 16 max.
	 * If the botName is not correct, a dialog window pops-up to tell the user how to make a proper botName.
	 * @param botName
	 * @return true if the bot name is correct.
	 * @see nickname specifications
	 */
	public boolean  isBotNameValid(String botName)
	{
		if(botName.length()<3 ||botName.length()>16)
		{
			javax.swing.JOptionPane.showMessageDialog(null,"The botName must contains between 3 and 16 caracters.");
			return false;
		}
		
		for(int i=0;i<botName.length();++i)
		{
			if(!(Character.isAlphabetic(botName.charAt(i))||Character.isDigit(botName.charAt(i))))
			{
				javax.swing.JOptionPane.showMessageDialog(null,"The bot name must contains alphanumeric caracters.");
				return false;
			}
		}
		
		return true;
	}
	
}