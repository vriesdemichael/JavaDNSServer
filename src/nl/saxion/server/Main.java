package nl.saxion.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import nl.saxion.server.DNS.DNSPacket;

public class Main {

	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		DatagramSocket serverSocket = new DatagramSocket(53);
		byte[] receiveData = new byte[2048];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			System.out.println("Received datagram: " + receivePacket.getLength());
			

			//turn into DNSPacket
			DNSPacket dnsPacket = new DNSPacket(receivePacket);
			//flips QR to make it a answer
			dnsPacket.getFlags().setAnswer();
			
			//Create new datagram packet to send back
			DatagramPacket sendPacket = new DatagramPacket(dnsPacket.getBytes(), dnsPacket.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
			System.out.println("/* Printing response packet to be sent to the client */");
			System.out.println("Amount of answers: " + dnsPacket.getAmountOfAnswers());
			System.out.println("First name segment: " + dnsPacket.getQuestions()[0].getName().get(0));
			printDatagram(dnsPacket.getBytes(), dnsPacket.getBytes().length);
			
			//send the packet back
			serverSocket.send(sendPacket);
			System.out.println("--------\n\n\n");
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

		System.out.println(String.format("%-48s   %s", hex, ascii));
	}
	
//	private static void addAnswer(byte[] data, byte[] answer, int realLength) {
//		for(int i = 0; i< answer.length; i++){
//			data[realLength+i] = answer[i];
//		}
//	}

//	private static int doubleByteToInt(byte low, byte high){
//		int lowInt = (int) low;
//		int highInt = (int) high;
//		return	lowInt+ highInt*256;	
//	}
	
//	private static byte[] intToDoubleByte(int i){
//		int lowInt = i%256;
//		int highInt = i/256;
//		byte[] byteVersion= {((byte) highInt), ((byte) lowInt)};
//		return byteVersion;
//	}
	
//	private static void setAnswerCount(byte[] data, int count){
//		byte[] newValue = intToDoubleByte(count);
//		data[6] = newValue[0];
//		data[7] = newValue[1];
//	}
	
//	private static void setAnswer(byte[] data){
//		//flip QR
//		data[2] = (byte) (data[2] | (1 <<7));
//		
//	}
	
//private static boolean isAnswer(byte[] data){
//		//check if the packet is a 
//		if (!isBitSet(data[2],7)) {
//			System.out.println("\nPacket is a question");
//			return true;
//		} else {
//			System.out.println("\nPacket is a answer");
//			return false;
//		}
//	}
	
//	private static Boolean isBitSet(byte b, int bit){
//	    return (b & (1 << bit)) != 0;
//	}
	
}
