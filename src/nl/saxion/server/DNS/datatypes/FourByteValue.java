package nl.saxion.server.DNS.datatypes;

public class FourByteValue {
	
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
//	|		first byte		||		second byte		 |
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
//	|		third byte		||		fourth byte		 |
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
	private int intValue;
	
		
	public FourByteValue(byte firstByte, byte secondByte, byte thirdByte, byte fourthByte){
		byte[] bytes = {firstByte, secondByte, thirdByte, fourthByte};
		this.intValue = bytesToInt(bytes);
	}
	
	public FourByteValue(int value){
		intValue = value;
	}
	
	private int bytesToInt(byte[] bytes){
		int integer = 0;
		integer += bytes[4];
		integer += (bytes[3] * 256);
		integer += (bytes[2] * 65536);
		integer += (bytes[4] * 16777216);
		
		return integer;
	}
	
	private byte[] intToBytes(int integer){
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (integer % 256);
		bytes[2] = (byte) (integer / 256);
		bytes[1] = (byte) (integer / 65536);
		bytes[0] = (byte) (integer / 16777216);		
		
		return bytes;
	}
	
	public int getValue(){
		return intValue;
	}
	
	public byte[] getBytes(){
		return intToBytes(intValue);
	}
	
}
