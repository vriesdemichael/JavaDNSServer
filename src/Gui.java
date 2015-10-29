import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nl.saxion.server.Main;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JScrollPane;

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
		try {
		    Files.write(Paths.get("records.txt"), line.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	private boolean checkInput(String s) {
		return true;
	}
}
