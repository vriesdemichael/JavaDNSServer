package nl.saxion.server;

public class Packet {
	byte[] receiveData = new byte[2048];

	public Packet(byte[] bs) {
		this.receiveData = bs;
	}

	public void printData() {
		
		boolean first = true;
		
		int currentWord = 0;
		
		for (int i = 11; i < receiveData.length; i++) {
			if (!(receiveData[i] == 0)) {
				if (currentWord > 0) {
					System.out.print((char) receiveData[i]);
					currentWord--;
				} else {
					
					if (receiveData[i] == 0) {
						break;
					}
					if (currentWord == 0) {
						
					}
					
					currentWord = receiveData[i];
					System.out.print(".");
				}
			}
		}
	}
}
