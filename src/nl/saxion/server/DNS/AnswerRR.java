package nl.saxion.server.DNS;

import java.util.ArrayList;

public class AnswerRR {
	public int length;
	private ArrayList<Segment> nameSegments;
	private TwoByteValue answerType;
	private TwoByteValue answerClass;
	
	private int endIndex;
	
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
		
		answerType = new TwoByteValue();
		answerType.setValue(1);
		answerClass = new TwoByteValue();
		answerClass.setValue(0);
		endIndex = index + 4;
	}
	
	public byte[] getBytes(){
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
		//add TTL. TTL is always the same. So just insert it static
		for (int i = 0; i < 3; i++) {
			largeByte[byteCount] = (byte)0;
			byteCount++;
		}
		largeByte[byteCount] = (byte)5;
		byteCount++;
		
		//make a new byte array with the proper length
		byte[] question = new byte[byteCount];
		//copy the largeByte into the properly sized byte array
		System.arraycopy(largeByte, 0, question, 0, byteCount);
		return question;
	}
	
	public int getEndIndex(){
		return this.endIndex;
		
	}

}
