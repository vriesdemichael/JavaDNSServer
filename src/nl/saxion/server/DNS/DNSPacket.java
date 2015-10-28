package nl.saxion.server.DNS;

import java.net.DatagramPacket;

import nl.saxion.server.Main;

public class DNSPacket{
	private byte[] data = new byte[1024];
	/**
	 * data[0&1]
	 */
	private TwoByteValue identifier;
	/**
	 * data[2&3]
	 */
	private Flags flags;
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

		this.data = packet.getData();
		this.answers = new AnswerRR[0];
		
		/* DNS header */
		identifier = new TwoByteValue(data[0], data[1]);
		//flags
		flags = new Flags(data[2], data[3]);
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
		
		
		getRRs();
		//System.out.println("/* Printing RRs */");
		//questions[0].printName();
		printDatagram(questions[0].getBytes(), questions[0].getBytes().length);
	}
	
	public AnswerRR[] getAnswers(){
		return answers;
	}
	
	public QuestionRR[] getQuestions(){
		return questions;
	}
	
	public int getAmountOfQuestion(){
		return totalQuestions.getValue();
	}
	
	public int getAmountOfAnswers(){
		return totalAnswerRRs.getValue();
	}
	
	private void getRRs(){
		 /* questions */
		for(int i = 0; i < totalQuestions.getValue(); i++){
			questions[i] = new QuestionRR(data, bodyIndex);
			bodyIndex = questions[i].getEndIndex() + 1;
		}
		if(flags.isQuestion()){
			//answers the questions
			for(QuestionRR q : questions){
				AnswerRR a = new AnswerRR(q);
				//System.out.println(q.getNames());
				Ipv4 ip = Main.getIpv4FromHost(q.getNames());
				//System.out.println(ip.getS1());
				a.setIPv4Answer(ip.getS1(), ip.getS2(), ip.getS3(), ip.getS4());
				addAnswer(a);
			}		
		}else{
			//get the answers from the packets
			for(int i = 0; i < totalAnswerRRs.getValue(); i++){
				answers[i] = new AnswerRR(data, bodyIndex);
				bodyIndex = answers[i].getEndIndex() + 1;
			}
		}		
	}
	
	public Flags getFlags(){
		return flags;
	}
	
	public void addAnswer(AnswerRR answer){
		AnswerRR[] tempArray = new AnswerRR[(answers.length +1)];
		System.arraycopy(answers, 0, tempArray, 0, answers.length);
		tempArray[answers.length] = answer;
		answers = tempArray;
		totalAnswerRRs.setValue(totalAnswerRRs.getValue() +1);
	}
	
	public byte[] getOriginalData() {
		return this.data;
	}
	
	public byte[] getBytes(){
		int totalLength = 
				identifier.getBytes().length +
				flags.getBytes().length +
				totalQuestions.getBytes().length +
				totalAnswerRRs.getBytes().length +
				totalAuthorityRRs.getBytes().length +
				totalAdditionalRRs.getBytes().length;
		
		if(questions != null){
			for (QuestionRR r : questions) {
				totalLength += r.getBytes().length;
			}
		}
		if (answers != null) {
			for (AnswerRR a : answers) {
				totalLength += a.getBytes().length;
			}
		}
						
		byte[] bytes = new byte[totalLength];
		int byteCount = 0;
		for(byte b: identifier.getBytes()){
			bytes[byteCount] = b;
			byteCount ++;
		}
		for(byte b: flags.getBytes()){
			bytes[byteCount] = b;
			byteCount ++;
		}
		for(byte b: totalQuestions.getBytes()){
			bytes[byteCount] = b;
			byteCount ++;
		}
		for(byte b: totalAnswerRRs.getBytes()){
			bytes[byteCount] = b;
			byteCount ++;
		}
		for(byte b: totalAuthorityRRs.getBytes()){
			bytes[byteCount] = b;
			byteCount ++;
		}
		for(byte b: totalAdditionalRRs.getBytes()){
			bytes[byteCount] = b;
			byteCount ++;
		}
		for(QuestionRR q: questions){
			for(byte b: q.getBytes()){
				bytes[byteCount] = b;
				byteCount ++;
			}	
		}
		for(AnswerRR r: answers){
			for(byte b: r.getBytes()){
				bytes[byteCount] = b;
				byteCount ++;
			}	
		}
		
		return bytes;
	}
	
	/**
	 * Combine two byte arrays into one.
	 * @param a The first byte array.
	 * @param b The second byte array.
	 * @return A byte array starting with a ending with b.
	 */
	public static byte[] combine(byte[] a, byte[] b){
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
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

		//System.out.println(String.format("%-48s   %s", hex, ascii));
	}
}
