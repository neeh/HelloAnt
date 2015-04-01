package com.polytech.di4.TokenClient;
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


public class Interface {

	private JFrame window= new JFrame("Log the new bot");
	private JPanel pan= new JPanel();
	private Button buttonOk= new Button("Enter");
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


	private class ListenerSend implements ActionListener{ 
	
		public void actionPerformed(ActionEvent e)
		{
			String botNameString = new String (botNameField.getText());
			//String serverAddressString = new String (serverAddressField.getText());
			
			InetAddress serverIp=null;
			try
			{
				serverIp = InetAddress.getByName(serverAddressField.getText());
			}
			catch (UnknownHostException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				RegisterSocket test = new RegisterSocket(botNameString,serverIp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				javax.swing.JOptionPane.showMessageDialog(null,e1.getMessage());
			}
			 catch (JSONException e2) {
				javax.swing.JOptionPane.showMessageDialog(null,e2.getMessage());
			 }
		}
	}
}