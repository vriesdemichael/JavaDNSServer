package nl.saxion.server.DNS.datatypes;

public class Segment {
	private String segment;
	
	public Segment(String segment){
		this.segment = segment;
	}
	
	public Segment(byte[] data, int startIndex){
		int segmentLength = data[startIndex];

		if(segmentLength < 0){
			startIndex = startIndex - segmentLength;
		}
		
		char[] charSegment = new char[segmentLength];
		
		for(int i = 0; i < segmentLength ; i++){
			charSegment[i] = (char) data[startIndex + i +1];
		}
		
		segment = new String(charSegment);
	}
	
	public String toString(){
		return segment;
	}
	
	public byte[] getBytes(){
		byte[] byteArray = new byte[segment.length() + 1];
		byteArray[0] = (byte) segment.length();
		for (int i = 0; i < segment.length(); i++){
			byteArray[i + 1] = (byte) segment.charAt(i);
		}
		return byteArray;
	}
	
	public void debug(){
		for(byte b: getBytes()){
			System.out.println("int " + b);
			System.out.println("hex " + Integer.toHexString(b));
			System.out.println("char " + ((char) b));
		}
	}
}