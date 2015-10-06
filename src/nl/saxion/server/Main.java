package nl.saxion.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

	public static void main(String args[]) throws Exception {

		DatagramSocket serverSocket = new DatagramSocket(53);
		byte[] receiveData = new byte[2048];

		byte[] sendData = new byte[1024];
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			System.out.println("Received datagram: " + receivePacket.getLength());
			
			
			String sentence = new String(receivePacket.getData());
			
			new Packet(receivePacket.getData()).printData();
			
			receiveData[2] = (byte) (receiveData[2] | (1 <<7));
			
			if (!isBitSet(receiveData[2],7)) {
				System.out.println("Is a question");
			} else {
				System.out.println("IK BEN EEN ANTWOORD!!! HEAR ME OUT!!!");
			}
				
				
	
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}

	}
	
	private static Boolean isBitSet(byte b, int bit){
	    return (b & (1 << bit)) != 0;
	}
}
