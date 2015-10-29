package nl.saxion.server.DNS;

//	 <--- highByte					   lowByte --->
//	.7..6..5..4..3..2..1..0...7..6..5..4..3..2..1..0 
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+
//	|QR|   Opcode  |AA|TC|RD||RA|    Z   |   RCODE   |
//	+--+--+--+--+--+--+--+--||--+--+--+--+--+--+--+--+

// QR: 		Question/Response: 		0 = question, 1 = response
// Opcode: 	0 = standard message
// AA: 		Authorative answer: 
//				1 = this server has authority over domain 
//				0 = this server does not have authority over domain
// TC:		TrunCation: 			not important for now -> 0
// RD:		Recursion Desired:		on/off
// RA:		Recursion available: 	on/off
// Z:		reserved bytes
// RCODE:	

public class Flags {
	
	private byte highByte;
	private byte lowByte;
	
	public Flags(byte[] flags){
		highByte = flags[0];
		lowByte = flags[1];
		/*
		byte opcode = (highByte & 0x78) >> 3;
		(opcode & 0x0F) << 3*/
	}
	
	public Flags(byte second, byte first) {
		highByte = second;
		lowByte = first;
	}

	public void setAnswer(){
		//highByte = (byte) (highByte | (1 <<7));
		highByte = setBit(highByte,  7);
	}
	
	public void setQuestion(){
		//highByte = (byte) (highByte & ~(0 <<7));
		highByte = unSetBit(highByte,  7);
	}
		
	public boolean isQuestion(){
		//check if the packet is a 
		if (isBitSet(highByte ,7)) {
			System.out.println("\nPacket is a question");
			return false;
		} else {
			System.out.println("\nPacket is a answer");
			return true;
		}
	}
	
	public void setAuthorative(){
		highByte = setBit(highByte, 2);
	}
	
	public void setNonAuthorative(){
		highByte = unSetBit(highByte, 2);
	}
	
	public boolean isAuthorative(){
		return isBitSet(highByte, 2);
	}
	
	public void setRecursionDesired(){
		highByte = setBit(highByte, 0);
	}
	
	public void setRecursionNotDesired(){
		highByte = unSetBit(highByte, 0);
	}
	
	public boolean isRecursionDesired(){
		return isBitSet(highByte, 0);
	}
	
	public void setRecursionAvailable(){
		lowByte = setBit(lowByte, 7);
	}
	
	public void SetRecursionUnavailable(){
		lowByte = unSetBit(lowByte, 7);
	}
	
	public boolean isRecursionAvailable(){
		return isBitSet(lowByte, 2);
	}
	
	//TODO bitshift zonder overwrites
//	//3 tm 6
//	public void setOpCode(byte opcode){
//		boolean first = isBitSet(highByte, 3);
//		boolean second = isBitSet(highByte, pos);
//		
////		1 1 1 1 1 1 1 1
////		0 1 1 1 1 0 0 0
////		1 0 0 0 0 1 1 1
//		
////		1 0 1 0 1 0 1 0 
////		1 0 0 0 0 1 1 1 
////		1 0 0 0 0 0 1 0 AND
//		
//		//OPCODE
////		0 0 1 0 
////		0 0 0 1 0 0 0 0
////		1 0 1 0 1 0 1 0
//		
//		//(byte) ((opcode & 0x0F) << 3);
//		
//	}
	
	public int getOpcode(){
		return (highByte & 0x78) >> 3;
	}
	
	public void setNameNotFound(){
		lowByte = 3;
	}

		
	private byte setBit(byte b, int pos){
		b = (byte) (b | (1 << pos));
		return b;
	}
	
	private byte unSetBit(byte b, int pos){
		b = (byte) (b &~ (1 << pos));
		return b;
	}
	
	private boolean isBitSet(byte b, int pos){
		return (b & (1 << pos)) != 0;	
	}
	
	public byte[] getBytes(){
		byte[] bytes = {highByte, lowByte};
		return bytes;
	}
}
