package nl.saxion.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

	/**
	 * Le roy
	 */
	
	private static byte[] createDNSResponse(byte[] quest, byte[] ips) {
	     int start = 0;
	     byte[] response = new byte[4096];

	     int[] DNS_HEADERS = { 0, 0, 0x81, 0x80, 0, 0, 0, 0, 0, 0, 0, 0 };
	     int[] DNS_PAYLOAD = { 0xc0, 0x0c, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x3c, 0x00, 0x04 };
	     
	     for (int val : DNS_HEADERS) {
	      response[start] = (byte) val;
	      start++;
	     }

	     System.arraycopy(quest, 0, response, 0, 2); /* 0:2 | NAME */
	     System.arraycopy(quest, 4, response, 4, 2); /* 4:6 -> 4:6 | TYPE */
	     System.arraycopy(quest, 4, response, 6, 2); /* 4:6 -> 7:9 | CLASS */
	     /* 10:14 | TTL */
	     System.arraycopy(quest, 12, response, start, quest.length - 12); /* 12: -> 15: */
	     start += quest.length - 12;

	     for (int val : DNS_PAYLOAD) {
	      response[start] = (byte) val;
	      start++;
	     }

	     /* IP address in response */
	     for (byte ip : ips) {
	      response[start] = ip;
	      start++;
	     }

	     byte[] result = new byte[start];
	     System.arraycopy(response, 0, result, 0, start);

	     return result;
	 }
	
	/**
	 * Einde roy
	 */
	
	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		DatagramSocket serverSocket = new DatagramSocket(53);
		byte[] receiveData = new byte[2048];

//		byte[] sendData;
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			System.out.println("Received datagram: " + receivePacket.getLength());
			
			byte[] data = receivePacket.getData();
			printDatagram(data, receivePacket.getLength());
			
			

//          Werkt niet, iets met de header probably
//			
//			Answer Resource Record			
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//			| 												|
//			/ 												/
//			/ NAME 											/
//			| 												|
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//			| TYPE 											|
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//			| CLASS 										|
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//			| TTL											|
//			| 												|
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//			| RDLENGTH 										|
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
//			/ RDATA 										/
//			/ 												/
//			+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

			/* type */			
//			This field specifies the meaning of the data in
//			the RDATA field. You should be prepared to interpret type 0x0001 (A record) and type 0x0005
//			(CNAME). If you are completing the graduate version of this project, you should also be prepared
//			to accept type 0x0002 (name servers) and 0x000f (mail servers).

			byte[] answer = {
					//name
					4, 'd', 'e', 'r', 'p', 4, 'd', 'e', 'r', 'p', 4, 'd', 'e', 'r', 'p',
					//end of variable length block name
					0, 
					// type 
					0x0001,
					// class
					0x0001,
					//TTL in seconds, 16 bit block
					0, 30,
					//RDLENGTH
					4,
					(byte) //RDATA (if type is 0x0001 --> 4 bits containing ip
					192, (byte) 168, 1, 14
//					// answer ip
//					3, '1', '9', '2', 3, '1', '6', '8', 1, '1', 2, '1', '4', 
					};
			
			setAnswer(data);
			setAnswerCount(data, 1);
			addAnswer(data, answer, receivePacket.getLength());
			
			System.out.println("---- answer ----");
			printDatagram(data, receivePacket.getLength() + answer.length);
			System.out.println("---- /answer ----");
			
			
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
//			String capitalizedSentence = sentence.toUpperCase();
//			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(data, receivePacket.getLength()+answer.length, IPAddress, port);
			serverSocket.send(sendPacket);
			System.out.println("-------");
		}


	}
	
	private static void addAnswer(byte[] data, byte[] answer, int realLength) {
		for(int i = 0; i< answer.length; i++){
			data[realLength+i] = answer[i];
		}
	}

	private static int doubleByteToInt(byte low, byte high){
		int lowInt = (int) low;
		int highInt = (int) high;
		return	lowInt+ highInt*256;	
	}
	
	private static byte[] intToDoubleByte(int i){
		int lowInt = i%256;
		int highInt = i/256;
		byte[] byteVersion= {((byte) highInt), ((byte) lowInt)};
		return byteVersion;
	}
	
	private static void setAnswerCount(byte[] data, int count){
		byte[] newValue = intToDoubleByte(count);
		data[6] = newValue[0];
		data[7] = newValue[1];
	}
	
	private static void setAnswer(byte[] data){
		//flip QR
		data[2] = (byte) (data[2] | (1 <<7));
		
	}
	
private static boolean isAnswer(byte[] data){
		//check if the packet is a 
		if (!isBitSet(data[2],7)) {
			System.out.println("\nPacket is a question");
			return true;
		} else {
			System.out.println("\nPacket is a answer");
			return false;
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
	private static Boolean isBitSet(byte b, int bit){
	    return (b & (1 << bit)) != 0;
	}
	
	
	/**
	 * Slechte pogingen :)
	 * 
	 */
	
//	byte[] trimmedData = new byte[receivePacket.getLength()];
//	System.arraycopy(receiveData, 0, trimmedData, 0, receivePacket.getLength());
//	
//	//verwoede poging
//	byte[] answer = new byte[33];
//	answer[0]=2;
//	answer[1]='n';
//	answer[2]='u';
//	answer[3]=2;
//	answer[4]='n';
//	answer[5]='l';
//	answer[6]= 0;
//	answer[7]= 0;
//	answer[8]= 0x0001;
//	answer[9]= 0;
//	answer[11]=0x0001;
//	answer[12]=0;
//	answer[14]=1;
//	answer[16]=0;
//	answer[17]=60;
//	answer[18]=0;
//	answer[18]=14;
//	answer[19]=3;
//	answer[20]='1';
//	answer[21]='9';
//	answer[22]='2';
//	answer[23]=3;
//	answer[24]='1';
//	answer[25]='6';
//	answer[26]='8';
//	answer[27]=1;
//	answer[28]='1';
//	answer[29]=2;
//	answer[30]='1';
//	answer[31]='4';
//	answer[32]=0;
//
//	byte[] answerPacket = new byte[trimmedData.length + answer.length];
//	System.arraycopy(trimmedData, 0, answerPacket, 0, trimmedData.length);
//	System.arraycopy(answer, 0, answerPacket, trimmedData.length, answer.length);

	
	//System.out.println(new String(nuNlRR));
}
