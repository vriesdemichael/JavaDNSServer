package nl.saxion.server.DNS;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class DNSPacket{
	private byte[] data = new byte[1024];
	private DatagramPacket packet;
	/**
	 * data[0&1]
	 */
	private TwoByteValue identifier;
	/**
	 * data[2&3]
	 */
	private byte[] flags = new byte[2];
	/**
	 * data[4&5]
	 */
	private TwoByteValue totalQuestions;
	/**
	 * data[6&7]
	 */
	private TwoByteValue totalAnswerRRs;
	/**
	 * data[8&9]
	 */
	private TwoByteValue totalAuthorityRRs;
	/**
	 * data[10&11]
	 */
	private TwoByteValue totalAdditionalRRs;
	/**
	 * data[12-packet.getLength()-12]
	 */
	private byte[] dnsBody;
	private QuestionRR[] questions;
	private AnswerRR[] answers;
	
	private int bodyIndex = 12;
	
	public DNSPacket(DatagramPacket packet ) {
		this.packet = packet;
		this.data = packet.getData();
		
		/* DNS header */
		identifier = new TwoByteValue(data[0], data[1]);
		//flags
		System.arraycopy(data, 2, flags, 0, 2);
		//total questions
		totalQuestions = new TwoByteValue(data[4], data[5]);
		//total answers
		totalAnswerRRs = new TwoByteValue(data[6], data[7]);
		//total authorities
		totalAuthorityRRs = new TwoByteValue(data[8], data[9]);
		//additionalRR
		totalAdditionalRRs = new TwoByteValue(data[10], data[11]);
		
		/* DNS body */
		dnsBody = new byte[packet.getLength()-12];
		System.arraycopy(data, 12, dnsBody, 0, packet.getLength()-12);
		
		questions = new QuestionRR[totalQuestions.getValue()];
		answers = new AnswerRR[totalAnswerRRs.getValue()];
		
		getRRs();
		System.out.println("/* Printing RRs */");
		questions[0].printName();
		printDatagram(questions[0].getBytes(), questions[0].getBytes().length);
		
	}
	
	private void getRRs(){
		 /* questions */
		for(int i = 0; i < totalQuestions.getValue(); i++){
			questions[i] = new QuestionRR(data, bodyIndex);
			bodyIndex = questions[i].getEndIndex() + 1;
		}
		
		/* answers */
//		for(int i = 0; i < totalAnswerRRs.getValue(); i++){
//			answers[i] = new AnswerRR(data, bodyIndex);
//			bodyIndex = answers[i].getEndIndex() + 1;
//		}
		
	}
	
	
	private void printDatagram( byte[] data, int realLength) {
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
}