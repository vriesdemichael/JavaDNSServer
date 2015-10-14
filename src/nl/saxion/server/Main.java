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
			
			
//			String sentence = new String(receivePacket.getData());
			Packet derpDerpDerp = new Packet(receivePacket.getData());
			
			derpDerpDerp.printData();
			boolean broken = false;
			for(String derp : derpDerpDerp.getWords()){
				if(!derp.equals("derp")){
					broken = true;
				}
			}
			
			if(broken){
				continue;
			}else{
				System.out.println(derpDerpDerp.getWords());
			}
			
			String ip = "109.72.82.220";
			byte[] dnsResponse = createDNSResponse(receiveData, ip.getBytes());
			
		
			
			
			//flip QR
			receiveData[2] = (byte) (receiveData[2] | (1 <<7));
			//set count to 1
			receiveData[5] = (byte) 1;
			
			receivePacket.getLength();
						
			
			
//			byte[] trimmedData = new byte[receivePacket.getLength()];
//			System.arraycopy(receiveData, 0, trimmedData, 0, receivePacket.getLength());
//			
//			//verwoede poging
//			byte[] answer = new byte[33];
//			answer[0]=2;
//			answer[1]='n';
//			answer[2]='u';
//			answer[3]=2;
//			answer[4]='n';
//			answer[5]='l';
//			answer[6]= 0;
//			answer[7]= 0;
//			answer[8]= 0x0001;
//			answer[9]= 0;
//			answer[11]=0x0001;
//			answer[12]=0;
//			answer[14]=1;
//			answer[16]=0;
//			answer[17]=60;
//			answer[18]=0;
//			answer[18]=14;
//			answer[19]=3;
//			answer[20]='1';
//			answer[21]='9';
//			answer[22]='2';
//			answer[23]=3;
//			answer[24]='1';
//			answer[25]='6';
//			answer[26]='8';
//			answer[27]=1;
//			answer[28]='1';
//			answer[29]=2;
//			answer[30]='1';
//			answer[31]='4';
//			answer[32]=0;
//
//			byte[] answerPacket = new byte[trimmedData.length + answer.length];
//			System.arraycopy(trimmedData, 0, answerPacket, 0, trimmedData.length);
//			System.arraycopy(answer, 0, answerPacket, trimmedData.length, answer.length);

			
			//System.out.println(new String(nuNlRR));

			
			if (!isBitSet(dnsResponse[2],7)) {
				System.out.println("\nIs a question");
			} else {
				System.out.println("\nIK BEN EEN ANTWOORD!!! HEAR ME OUT!!!");
			}
				
	
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
//			String capitalizedSentence = sentence.toUpperCase();
//			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(dnsResponse, dnsResponse.length, IPAddress, port);
			serverSocket.send(sendPacket);
			System.out.println("-------");
		}


	}
	
	private static Boolean isBitSet(byte b, int bit){
	    return (b & (1 << bit)) != 0;
	}
}
