package com.test.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectFrame extends JFrame
{
	private JFrame frame;
	private JTextField ipField;
	private JTextField portField;
	private JLabel connectIP;
	private JLabel connectPort;
	private JButton confirm;
	private JButton concel;
	private JPanel jp1, jp2, jp3;
	public ClientFrame clientFrame;
	//private String ip, port;
	
	public ConnectFrame()
	{
		frame = new JFrame();
		ipField = new JTextField(13);
		portField = new JTextField(13);
		connectIP = new JLabel("IP address:");
		connectPort = new JLabel("Port number:");
		confirm = new JButton("confirm");
		concel = new JButton("concel");
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();
	}
	
	public void showConnectFrame()
	{
		initFrame();
		initJP1();
		initJP2();
		initJP3();
	}
	
	public void initFrame()
	{
		frame.setTitle("Connect to Server");
		int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setBounds(width / 2, height / 2, 400, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new GridLayout(3, 1));
		//this.add(jp1);
		//this.add(jp2);
		//this.add(jp3);
		
		//this.setTitle("Connect to Server");
		//initButtons(this);
		
		/*frame.setTitle("client");
		int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setBounds(width / 2, height / 2, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);*/
	}
	
	public void initJP1()
	{
		jp1.add(connectIP);
		jp1.add(ipField);
		frame.add(jp1);
	}
	
	public void initJP2()
	{
		jp2.add(connectPort);
		jp2.add(portField);
		frame.add(jp2);
	}
	
	public void initJP3()
	{
		initButtons();
		jp3.add(confirm);
		jp3.add(concel);
		frame.add(jp3);
	}
	
	public void initButtons()
	{
		confirm.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String ip = ipField.getText().trim();
				int port = Integer.parseInt(portField.getText().trim());
				System.out.println("ip:"+ip+",port:"+port);
				clientFrame = new ClientFrame(ip, port);
				frame.setVisible(false);
				System.out.println("before show!!!");
				clientFrame.showClientFrame();
				System.out.println("after show!!!");
				
			}
			
		});
		
		concel.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				ipField.setText("");
				portField.setText("");
			}
			
		});
	}
	
	public static void main(String[] args)
	{
		ConnectFrame cf = new ConnectFrame();
		cf.showConnectFrame();
	}
}
