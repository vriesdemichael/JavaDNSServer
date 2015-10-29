package nl.saxion.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import nl.saxion.server.DNS.DNSPacket;
import nl.saxion.server.DNS.Ipv4;

public class Main extends Observable {
	
	private static Map<String,Ipv4> records = new HashMap<String,Ipv4>();
	public static int amountOfRequests = 0;

	
	public Main() throws IOException {
		Main.loadRecords();
		
		@SuppressWarnings("resource")
		DatagramSocket serverSocket = new DatagramSocket(53);
		byte[] receiveData = new byte[2048];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			System.out.println("stuff");
			serverSocket.receive(receivePacket);
			
			Main.amountOfRequests++;
			this.setChanged();
			this.notifyObservers();
			
			//System.out.println("Received datagram: " + receivePacket.getLength());
			

			//turn into DNSPacket
			DNSPacket dnsPacket = new DNSPacket(receivePacket);
			//flips QR to make it a answer
			dnsPacket.getFlags().setAnswer();
			
			//Create new datagram packet to send back
			DatagramPacket sendPacket = new DatagramPacket(dnsPacket.getBytes(), dnsPacket.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
			//System.out.println("/* Printing response packet to be sent to the client */");
			//System.out.println("Amount of answers: " + dnsPacket.getAmountOfAnswers());
			//System.out.println("First name segment: " + dnsPacket.getQuestions()[0].getName().get(0));
			printDatagram(dnsPacket.getBytes(), dnsPacket.getBytes().length);
			
			//send the packet back
			serverSocket.send(sendPacket);
			//System.out.println("--------\n\n\n");
		}
	}
	
	private static void printDatagram( byte[] data, int realLength) {
		//van Paul gekregen :)
		String hex = "";
		String ascii = "";
		for( int i = 0; i < realLength; i++ ) {
			byte b = data[i];
			hex += String.format("%02X ", b);

			if( (b >= 32) && (b <= 127) )
				ascii += (char) b;
			else
				ascii += '.';

			if( (i % 16) == 15 ) {
				System.out.println(hex + "   " + ascii);
				hex = "";
				ascii = "";
			}
		}

		//System.out.println(String.format("%-48s   %s", hex, ascii));
	}
	
	public static Ipv4 getIpv4FromHost(String domain) {
		System.out.println(domain);

		for (Map.Entry<String, Ipv4> entry : Main.records.entrySet()) {
			if (domain.equals(entry.getKey())) {
				return entry.getValue();
			}
		}


		return null;
	}
	
	public static ArrayList<String> getRecords() {
		ArrayList<String> rec = new ArrayList<String>();
		for (Map.Entry<String, Ipv4> entry : Main.records.entrySet()) {
			rec.add(entry.getKey()+" | ip: " + entry.getValue());
		}
		return rec;
	}
	
	private static void loadRecords() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("records.txt"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    
		    while (line != null) {
		    	
		    	Scanner sc = new Scanner(line);
		    	
		    	records.put(sc.next(), new Ipv4(sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt()));
		        line = br.readLine();
		    }
		} catch(Exception e) {
			
		} 
	}
	
}
