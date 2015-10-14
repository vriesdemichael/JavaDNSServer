package nl.saxion.server;

import java.util.ArrayList;

public class Packet {
	byte[] receiveData = new byte[2048];
	private ArrayList<String> words;

	public Packet(byte[] bs) {
		this.receiveData = bs;
	}
	
	//TODO class en type uitlezen
	//TODO name uitlezen in ene functie zetten
	public void printData() {
	
		words = new ArrayList<String>();
		String currentWord = "";
		
		
		for (int i = 11, wordIndex = 0 ; i < receiveData.length;i++) {
			if (!(receiveData[i] == 0)) {
				//set index if not set
				if(wordIndex == 0){
					//get word length
					wordIndex = receiveData[i];
					//if word length is still 0 break the loop
					if(wordIndex == 0){
						//name part is done
						//break;
						continue;
					}
					if(!currentWord.equals("")){
						words.add(currentWord.toString());
						currentWord = "";
					}
				}else if (wordIndex > 0) {
					currentWord += ((char) receiveData[i]);
					wordIndex--;
				}else if(wordIndex < 0){
					currentWord = "";
					int length = receiveData[i- receiveData[i]];
					int start = i- receiveData[i];
					for(int j = start; j < start+length; j++){
						currentWord += receiveData[j];
					}
					System.out.println("negative currentword");
					wordIndex = 0;
					continue;
				}

			}else{
				
			}
		}
//		
//		for(String s: words){
//			System.out.print(s+".");
//			
////			System.out.println("segment: " + s);
////			for(char c: s.toCharArray()){
////				System.out.println("Byte value for '"+ c +"': " +(int) c);	
////			}
//			
//			
//		}
		
	}
	
	public ArrayList<String> getWords(){
		return words;
	}
}
