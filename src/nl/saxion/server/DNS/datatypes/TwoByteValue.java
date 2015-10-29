package nl.saxion.server.DNS.datatypes;

public class TwoByteValue {

//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
//	|		high byte		||		low byte		 |
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
	
	public int length = 2;
	private int intValue;
	
	public TwoByteValue(byte[] value){
		int highByte = value[0];
		int lowByte = value[1];
		
		intValue = lowByte + 256* highByte;
	}
	
	public TwoByteValue(byte highByte, byte lowByte){
		intValue = ((int) lowByte) + 256 * ((int) highByte);
	}
	
	public TwoByteValue() {
		
	}
	
	public TwoByteValue(int value) {
		this.intValue = value;
	}

	public void setValue(int newValue){
		this.intValue = newValue;
	}
	
	public int getValue(){
		return intValue;
	}
	
	public byte[] getBytes(){
		int highByte = intValue / 256;
		int lowByte = intValue % 256;
		byte[] byteArray = {(byte) highByte, (byte) lowByte};
		return byteArray;
	}
	
}
