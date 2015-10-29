package nl.saxion.server.DNS;

import java.util.ArrayList;

import nl.saxion.server.DNS.datatypes.FourByteValue;
import nl.saxion.server.DNS.datatypes.Segment;
import nl.saxion.server.DNS.datatypes.TwoByteValue;

//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//	| 												|
//	/ 												/
//	/ 					 NAME 						/
//	| 												|
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//	| 					 TYPE 						|
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//	| 					 CLASS 						|
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//	| 					 TTL 						|
//	| 												|
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//	| 					RDLENGTH 					|
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
//	/ 					 RDATA						/
//	/ 												/
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+


public class AnswerRR {
	public int length;
	private ArrayList<Segment> nameSegments;
	private ArrayList<Segment> rDataSegments;
	private TwoByteValue answerType;
	private TwoByteValue answerClass;
	private FourByteValue ttl;
	private int ipv4a = 0;
	private int ipv4b = 0;
	private int ipv4c = 0;
	private int ipv4d = 0;
	private int endIndex;
	
	/**
	 * Creates a answer from the entire DNS packet starting at index.
	 * @param data The body of the DNS packet or the entire packet, gathered from a DatagramPacket with getData()
	 * @param startIndex The starting position of the question in the byte array
	 */
	public AnswerRR(byte[] data, int startIndex) {
		nameSegments = new ArrayList<Segment>();
		int index = startIndex;
		int segmentLength = data[startIndex];
		
		//get name
		while(segmentLength != 0){
			
			if(segmentLength < 0){
				nameSegments.add(new Segment(data, index - segmentLength));
				//move to next segment
				
				index++;
			}else{
				nameSegments.add(new Segment(data, index));
				//move to the next segment
				index += (segmentLength + 1);
			}
			segmentLength = data[index];

		}
		//index was 0, move to next
		index++;
				
		//get type and class
		answerType = new TwoByteValue(data[index], data[index +1]);
		answerClass = new TwoByteValue(data[index + 2], data[index + 3]);
		index +=4;
		
		//get TTL
		ttl = new FourByteValue(data[index], data[index + 1], data[index +2], data[index + 3]);
		index += 4;
		
		//get rData length
		//TwoByteValue rDataLength = new TwoByteValue(data[index], data[index + 1]);
		index += 2;
		
		//get the data
		if(answerType.getValue() == 1 && answerClass.getValue() == 1){
			//rData == IPv4 address
			ipv4a = data[index];
			ipv4b = data[index + 1];
			ipv4c = data[index + 2];
			ipv4d = data[index + 3];
			endIndex = index + 4;
		} else {
			// rData != IPv4 address
			rDataSegments = new ArrayList<Segment>();
			int dataSegmentLength = data[index];
			
			while(dataSegmentLength != 0 && index < data.length){
				
				if(segmentLength < 0){
					rDataSegments.add(new Segment(data, index - segmentLength));
					//move to next segment
					index++;
				}else{
					rDataSegments.add(new Segment(data, index));
					//move to the next segment
					index += (segmentLength + 1);
				}
				segmentLength = data[index];
			}

			endIndex = index + 1;
		}
		
	}
	
	/**
	 * Create a Answer Response Record for the given question. <p>
	 * This does not supply a answer.
	 * @param question The QuestionRR to be answered
	 */
	public AnswerRR(QuestionRR question){
		this.nameSegments = question.getName();
		this.answerType = new TwoByteValue(question.getQuestionType());
		this.answerClass = new TwoByteValue(question.getQuestionClass());
		this.ttl = new FourByteValue(120);
	}
	
	/**
	 * Set the rData for this answer as a string.
	 * @param segmentsAsString
	 */
	public void setRRAnswer(String[] segmentsAsString){
		for(String str: segmentsAsString) {
			rDataSegments.add(new Segment(str));
		}			
	}
	
	/**
	 * Get the time to live in seconds.
	 * @return
	 */
	public long getTtl(){
		return ttl.getValue();
	}
	
	/**
	 * Set the time to live in seconds.
	 * @param ttl
	 */
	public void setTtl(int ttl){
		this.ttl = new FourByteValue(ttl);
	}
	
	/**
	 * Get the answer type.
	 * @return
	 */
	public int getAnswerType(){
		return answerType.getValue();
	}
	
	/**
	 * Set the answer type.
	 * @param type
	 */
	public void setAnswerType(int type){
		answerType.setValue(type);
	}
	
	/**
	 * Set a IPv4 as answer. <p> Enter the IP using this format: "a.b.c.d"
	 * @param a a
	 * @param b b
	 * @param c c
	 * @param d d
	 */
	public void setIPv4Answer(int a, int b, int c, int d){
		this.answerClass.setValue(1);
		this.answerType.setValue(1);
		this.ipv4a = a;
		this.ipv4b = b;
		this.ipv4c = c;
		this.ipv4d = d;		
	}
	
	/**
	 * Get the IPv4 answer attached to this question
	 * @return A IPv4 in the format "xxx.xxx.xxx.xxx"
	 */
	public String getIPv4Answer(){
		if(this.answerType.getValue() == 1 && this.answerClass.getValue() == 1){
			return ipv4a + "." + ipv4b + "." + ipv4c + "." + ipv4d;
		} else {
			return null;
		}
	}
	
	/**
	 * Generate a byte array for the entire AnswerRR according to RFC 1035.
	 * @return A byte array for this AnswerRR if valid, null if not.
	 */
	public byte[] getBytes(){
		if(!checkIfFilled()){
			return null;
		}
		
		int byteCount = 0;
		byte[] largeByte = new byte[1024];

		//add all segments
		for(Segment s: nameSegments){
			byte[] segmentBytes = s.getBytes();
			for(byte b : segmentBytes){
				largeByte[byteCount] = b;
				byteCount ++;
			}
		}
		
		//add 0 byte as seperator
		largeByte[byteCount] = 0;
		byteCount ++;
		
		//add type
		for (byte b : answerType.getBytes()) {
			largeByte[byteCount] = b;
			byteCount ++;
		}
		
		//add class
		for (byte b : answerClass.getBytes()) {
			largeByte[byteCount] = b;
			byteCount ++;
		}
		
		//add TTL
		for(byte b: ttl.getBytes()){
			largeByte[byteCount] = b;
			byteCount ++;
		}
		
		//add rData
		if((answerClass.getValue() == 1 && answerType.getValue() == 1)){
			//add rData length
			for(byte b: new TwoByteValue(4).getBytes()){
				largeByte[byteCount] = b;
				byteCount ++;
			}
			//add the 4 bytes for the IPv4 address
			largeByte[byteCount] = (byte) ipv4a;
			byteCount ++;
			largeByte[byteCount] = (byte) ipv4b;
			byteCount ++;
			largeByte[byteCount] = (byte) ipv4c;
			byteCount ++;
			largeByte[byteCount] = (byte) ipv4d;
			byteCount ++;
		} else {
			//TODO
			return null;
		}
				
		//make a new byte array with the proper length
		byte[] question = new byte[byteCount];
		//copy the largeByte into the properly sized byte array
		System.arraycopy(largeByte, 0, question, 0, byteCount);
		return question;
	}
	
	/**
	 * Check if the AnswerRR has the right variables set to generate a valid AnswerRR byte array.
	 * @return true if valid variables, false if not.
	 */
	private boolean checkIfFilled() {
		if(answerClass == null || answerType == null || nameSegments == null || ttl == null){
			return false;
		}
		if(nameSegments.size() < 1){
			return false;
		}
		if((answerClass.getValue() == 1 && answerType.getValue() == 1) && getIPv4Answer().equals("0.0.0.0")){
			return false;
		}
		if((answerClass.getValue() != 1 && answerType.getValue() != 1)){
			if(rDataSegments == null){
				return false;
			}else if(rDataSegments.size() < 1){
				return false;
			}
		}
		
		
		return true;
	}

	/**
	 * Get the last byte that belongs to this RR. <p> This only works if the RR was generated from a packet.
	 * @return The index of the last byte of this RR in the DNSpacket data byte array. 
	 */
	public int getEndIndex(){
		return this.endIndex;
		
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
	
	/**
	 * Print the name of this question.
	 * @return The segments of name concatenated with ".".
	 */
	public String printName(){
		String[] names = new String[nameSegments.size()];
		for(int i = 0; i < nameSegments.size(); i++){
			names[i] = nameSegments.get(i).toString();
		}
		System.out.println("Question name: " + String.join(".", names));
		return 	String.join(".", names);
	}
}
