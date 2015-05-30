package com.test.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.test.loginmodule.KillbotTest;
import com.test.loginmodule.LoginTest;
import com.test.loginmodule.LogoutTest;
import com.test.loginmodule.SetmodeTest;
import com.test.loginmodule.TokenTest;
import com.test.message.MessageParse;

public class ClientFrame extends JFrame
{
	private PrintWriter pw;
	private JFrame frame;
	private JPanel pane_buttom;
	private JSplitPane pane_center;	
	private JSplitPane pane_top;
	
	
	//show content textbox, enter content textbox, send message button
	private JScrollPane pane_showWindow;
	private JScrollPane pane_parseWindow;
	private JScrollPane pane_inputWindow;
	private JTextArea area_showWindow;
	private JTextArea area_parseWindow;
	private JTextArea area_inputWindow;
	private JButton btn_send;
	//set area_showWindow size
	private Dimension dimension;
	
	private String ip = null;
	private int port;
	
	public ClientFrame()
	{
		frame = new JFrame();
		pane_buttom = new JPanel();
		pane_showWindow = new JScrollPane();
		pane_parseWindow = new JScrollPane();
		pane_inputWindow = new JScrollPane();
		area_showWindow = new JTextArea();
		area_showWindow.setFont(new Font("Dialog", 1, 15));
		//area_showWindow.setText("Dialog Space");
		area_parseWindow = new JTextArea();
		area_parseWindow.setFont(new Font("Dialog", 1, 15));
		//area_parseWindow.setText("Parse Space");
		area_inputWindow = new JTextArea();
		area_inputWindow.setFont(new Font("Dialog", 1, 15));
		//area_inputWindow.setText("Input Space");
		pane_top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, pane_showWindow, pane_parseWindow);
		pane_center = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, pane_top, pane_inputWindow);
		btn_send = new JButton("send message");
		dimension = new Dimension(600, 300);
	}
	
	public ClientFrame(String ip, int port)
	{
		this();
		this.ip = ip;
		this.port = port;
	}
	
	public void showClientFrame()
	{
		initFrame();
		initMessageTextArea();
		initButton();
		btn_send();
		socket(ip, port);
	}
	
	public void initFrame()
	{
		frame.setTitle("client");
		int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setBounds(width / 2, height / 2, 1200, 600);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void initMessageTextArea()
	{
		pane_showWindow.getViewport().add(area_showWindow);
		pane_parseWindow.getViewport().add(area_parseWindow);
		pane_inputWindow.getViewport().add(area_inputWindow);
		
		area_showWindow.setEditable(false);
		
		pane_showWindow.setMinimumSize(dimension);
		frame.add(pane_center, BorderLayout.CENTER);
	}
	
	public void initButton()
	{
		pane_buttom.add(btn_send);
		frame.add(pane_buttom, BorderLayout.SOUTH);
	}
	
	public void btn_send()
	{
		btn_send.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
				area_parseWindow.setText("");
				String info = area_inputWindow.getText();
				info = StringParse.stringFormat(info);
				System.out.println("info : : " + info);
				MessageParse.sendMessageJSONObject(info, area_parseWindow);
				area_showWindow.append("client:" + info + "\r\n");
				pw.println(info);
				area_inputWindow.setText("");
				
			}
			
		});
	}
	
	public void socket(String ip, int port)
	{
		try
		{
			Socket s = new Socket(ip, port);
			System.out.println("Clientip:"+ip+",Clientport:"+port);
			InputStreamReader isr = new InputStreamReader(s.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			pw = new PrintWriter(s.getOutputStream(), true);
			while(true)
			{
				String info = br.readLine();
				MessageParse.reciveMessageJSONObject(info.trim(), area_parseWindow);
				area_showWindow.append("server:" + info + "\r\n");
			}
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void handleFirstConnect(BufferedReader br)
	{
		try
		{
			//br.readLine();
			System.out.println("firstMessage: " + br.readLine());
			//br.close();
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args)
	{
		String ip = "127.0.0.1";
		int port = 12345;
		ClientFrame cf = new ClientFrame(ip, port);
		//cf.showClientFrame();
		try {
			Socket s = new Socket(ip, port);
			InputStreamReader isr = new InputStreamReader(s.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
			cf.handleFirstConnect(br);
			KillbotTest.testKillbotError101(printWriter, br);
			//KillbotTest.testKillbotError102(printWriter, br);
			LoginTest.testLoginError101(printWriter, br);
			TokenTest.testTokenError102(printWriter, br);
			TokenTest.testTokenError103(printWriter, br);
			//SetmodeTest.testSetmodeError101(printWriter, br);
			TokenTest.testTokenError101(printWriter, br);
			//KillbotTest.testKillbotError102(printWriter, br);
			//LoginTest.testLoginNormal(printWriter, br);
			LoginTest.testLoginError103(printWriter, br);
			//LogoutTest.TestLogoutNormal(printWriter, br);
		} catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(java.lang.StackOverflowError e)
		{
			e.printStackTrace();
		}
		
	}
}
