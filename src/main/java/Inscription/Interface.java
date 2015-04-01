package Inscription;
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
public class Interface {

	private JFrame window= new JFrame("Log the new bot");
	private JPanel pan= new JPanel();
	private Button buttonOk= new Button("Enter");
	private Button buttonQuit=new Button("Exit");
	private Button buttonCopy=new Button("Copy token to clipboard");
	private JLabel botNameLab=new JLabel("Enter the bot name :");
	private JTextField botNameField=new JTextField(20);
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
						.addComponent(tokenLab))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(botNameField)
						.addComponent(tokenField))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(buttonOk)
						.addComponent(buttonCopy))
						);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(botNameLab)
						.addComponent(botNameField)
						.addComponent(buttonOk))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(tokenLab)
						.addComponent(tokenField)
						.addComponent(buttonCopy)));
		buttonCopy.addActionListener(new ListenerCopy());
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
}
