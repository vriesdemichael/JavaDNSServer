package nl.saxion.server.DNS;

import java.util.ArrayList;

public class QuestionRR {
	public int length;
	private ArrayList<Segment> nameSegments;
	private TwoByteValue questionType;
	private TwoByteValue questionClass;
	/**
	 * the last byte of this question
	 */
	private int endIndex;
	
	
	public QuestionRR(byte[] bodyData, int startIndex){
		nameSegments = new ArrayList<Segment>();
		int index = startIndex;
		int segmentLength = bodyData[startIndex];
		
		//get name
		while(segmentLength != 0){
			
			if(segmentLength < 0){
				nameSegments.add(new Segment(bodyData, index - segmentLength));
				//move to next segment
				index++;
			}else{
				nameSegments.add(new Segment(bodyData, index));
				//move to the next segment
				index += (segmentLength + 1);
			}
			segmentLength = bodyData[index];

		}
		//index was 0, move to next
		index++;
		
		questionType = new TwoByteValue(bodyData[index], bodyData[index + 1]);
		questionClass = new TwoByteValue(bodyData[index + 2], bodyData[index + 3]);
		endIndex = index + 4;
	
	}
	
	public int getEndIndex(){
		return endIndex;
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
		for (byte b : questionType.getBytes()) {
			largeByte[byteCount] = b;
			byteCount ++;
		}
		
		//add class
		for (byte b : questionClass.getBytes()) {
			largeByte[byteCount] = b;
			byteCount ++;
		}
		
		//make a new byte array with the proper length
		byte[] question = new byte[byteCount];
		//copy the largeByte into the properly sized byte array
		System.arraycopy(largeByte, 0, question, 0, byteCount);
		return question;
	}
	
	public ArrayList<Segment> getName(){
		return nameSegments;
	}
	
	public int getQuestionType(){
		return questionType.getValue();
	}
	
	public int getQuestionClass(){
		return questionClass.getValue();
	}
	
	public void printName(){
		String[] names = new String[nameSegments.size()];
		for(int i = 0; i < nameSegments.size(); i++){
			names[i] = nameSegments.get(i).toString();
		}
		String.join(".", names);
		//System.out.println("Question name: " + String.join(".", names));
	}
	
	public String getNames() {
		String[] names = new String[nameSegments.size()];
		for(int i = 0; i < nameSegments.size(); i++){
			names[i] = nameSegments.get(i).toString();
		}
		return String.join(".", names);
	}
}
