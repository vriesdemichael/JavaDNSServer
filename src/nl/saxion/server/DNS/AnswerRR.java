package nl.saxion.server.DNS;

import java.util.ArrayList;

public class AnswerRR {
	public int length;
	private ArrayList<Segment> nameSegments;
	private TwoByteValue answerType;
	private TwoByteValue answerClass;
	private String ip;
	byte rdataLength = (byte) 4;
	
	private int endIndex;
	
	public AnswerRR(byte[] data, int startIndex,String ip) {
		this.ip = ip;
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
		answerClass.setValue(1);
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
		
		
		byteCount++;
		
		
		//Now add an ip, no clue how this works tbh. Set TTL to 4 seconds. That should be enough to handle the request but give the control to us.
		largeByte[byteCount] = (byte)0;
		byteCount++;
		largeByte[byteCount] = (byte)4;
		byteCount++;
		String[] ipar = ip.split(".");
		byte[] rdata = {(byte)Integer.parseInt(ipar[0]),(byte)Integer.parseInt(ipar[1]),(byte)Integer.parseInt(ipar[2]),(byte)Integer.parseInt(ipar[3])};
		largeByte = combine(largeByte, rdata);
		byteCount = byteCount + 8;
		
		
		//make a new byte array with the proper length
		byte[] question = new byte[byteCount];
		//copy the largeByte into the properly sized byte array
		System.arraycopy(largeByte, 0, question, 0, byteCount);
		return question;
	}
	
	public int getEndIndex(){
		return this.endIndex;
		
	}
	
	public static byte[] combine(byte[] a, byte[] b){
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
