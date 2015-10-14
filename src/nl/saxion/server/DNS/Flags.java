package nl.saxion.server.DNS;

//	 <--- highByte					   lowByte --->
//	.7..6..5..4..3..2..1..0..7..6..5..4..3..2..1..0 
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//	|QR|   Opcode  |AA|TC|RD|RA|    Z   |   RCODE   |
//	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

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
	}
	
	public Flags(byte second, byte first) {
		highByte = second;
		lowByte = first;
	}

	private void setAnswer(){
		highByte = (byte) (highByte | (1 <<7));
	}
	
	
	
	private boolean isAnswer(){
		//check if the packet is a 
		if (!isBitSet(highByte,7)) {
			System.out.println("\nPacket is a question");
			return true;
		} else {
			System.out.println("\nPacket is a answer");
			return false;
		}
	}
	
	private boolean isBitSet(byte b, int bit){
	    return (b & (1 << bit)) != 0;
	}
}
