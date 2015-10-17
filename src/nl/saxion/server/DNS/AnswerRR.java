package nl.saxion.server.DNS;

import java.util.ArrayList;

public class AnswerRR {
	public int length;
	private ArrayList<Segment> nameSegments;
	private ArrayList<Segment> rDataSegments;
	private TwoByteValue answerType;
	private TwoByteValue answerClass;
	private long ttl;
	private String ip;
	private int ipv4a;
	private int ipv4b;
	private int ipv4c;
	private int ipv4d;
	private int rDataLength;
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
		//index was 0, move to next
		index++;
				
		answerType = new TwoByteValue(data[index], data[index +1]);
		answerClass = new TwoByteValue(data[index + 2], data[index + 3]);
		index +=4;
		
		TwoByteValue ttlHigh = new TwoByteValue(data[index], data[index+1]);
		TwoByteValue ttlLow = new TwoByteValue(data[index + 2], data[index +3]);
		ttl = ttlLow.getValue() + ttlHigh.getValue()*256;
		index +=4;
		
		rDataLength = data[index];
		index++;
		
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
	
	public long getTtl(){
		return ttl;
	}
	
	public void setTtl(int ttl){
		this.ttl = ttl;
	}
	
	public int getType(){
		return answerType.getValue();
	}
	
	public void setType(int type){
		answerType.setValue(type);
	}
	
	public void setIPv4Answer(int a, int b, int c, int d){
		this.answerClass.setValue(1);
		this.answerType.setValue(1);
		this.rDataLength = 4;
		this.ipv4a = a;
		this.ipv4b = b;
		this.ipv4c = c;
		this.ipv4d = d;		
	}
	
	public String getIPv4Answer(){
		if(this.answerType.getValue() == 1 && this.answerClass.getValue() == 1){
			return ipv4a + "." + ipv4b + "." + ipv4c + "." + ipv4d;
		} else {
			return null;
		}
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
		
		largeByte[byteCount] = (byte) (ttl / 4096);
		byteCount ++;
		largeByte[byteCount] = (byte) (ttl / 256);
		
		
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
