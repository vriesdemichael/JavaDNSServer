import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import nl.saxion.server.Main;

public class Gui extends JFrame  {

	private JPanel contentPane;
	private JTextField textField;
	JLabel amountReq;
	Main m;
	JList<String> list;
	JButton addRecord;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton startServerButton = new JButton("Start DNS Server");
		startServerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new Thread(){
				    public void run(){
				    	try {
				    		m = new Main();
				    		
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				  }.start();
				  
				startServerButton.setText("Server has Started");
			}
		});
		 
		
		startServerButton.setBounds(0, 0, 484, 30);
		contentPane.add(startServerButton);
		
		
		list = new JList();
		list.setBounds(10, 32, 358, 136);
		contentPane.add(new JScrollPane(list));
		
		textField = new JTextField();
		textField.setBounds(10, 206, 358, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		addRecord = new JButton("Add Record");
		addRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(checkInput(textField.getText())) {
					addLineToRecords(textField.getText());
				} else {
					System.out.println("I WANNA KNOW WHAT LOVE IS!!! I WANT YOU TO SHOOOOOOOOOOOW ME!");
					new ErrorDialog();
				}
			}
		});
		addRecord.setBounds(375, 205, 89, 23);
		contentPane.add(addRecord);
		
		JLabel lblAddRecordAs = new JLabel("Add record as: hostname ip. Eg: nu.nl 127 0 0 1");
		lblAddRecordAs.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAddRecordAs.setBounds(10, 179, 358, 30);
		contentPane.add(lblAddRecordAs);
		
		JLabel lblNewLabel = new JLabel("Request Handled:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(10, 270, 160, 30);
		contentPane.add(lblNewLabel);
		
		amountReq = new JLabel("");
		amountReq.setFont(new Font("Tahoma", Font.PLAIN, 15));
		amountReq.setBounds(146, 273, 69, 24);
		contentPane.add(amountReq);
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				update();
			}
		});
		refreshButton.setBounds(375, 276, 89, 23);
		contentPane.add(refreshButton);

	}


	public void update() {
		Main.loadRecords();
		this.amountReq.setText(Main.amountOfRequests+"");
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		
		for(String s : Main.getRecords()) {
			listModel.addElement(s);
		}

 
        //create the list
        list = new JList<>(listModel);
        getContentPane().add(list); 
        list.setBounds(10, 32, 358, 136);
		contentPane.add(list);
		
		//list = new JList();
	}
	
	private void addLineToRecords(String line) {
		try(PrintWriter output = new PrintWriter(new FileWriter("records.txt",true))) 
		{
		    output.printf("%s\r\n", line);
		} 
		catch (Exception e) {}
	}
	
	private boolean checkInput(String s) {
		Scanner sc = new Scanner(s);
		try {
			String domain = sc.next();
			String n1 = sc.next();
			String n2 = sc.next();
			String n3 = sc.next();
			String n4 = sc.next();
		
			if(Integer.parseInt(n1) > 255 || Integer.parseInt(n1) < 0) {return false; }
			if(Integer.parseInt(n2) > 255 || Integer.parseInt(n2) < 0) {return false; }
			if(Integer.parseInt(n3) > 255 || Integer.parseInt(n3) < 0) {return false; }
			if(Integer.parseInt(n4) > 255 || Integer.parseInt(n4) < 0) {return false; }
		} catch (Exception e) {
			return false;
		}
		
		
		return true;
	}
}
