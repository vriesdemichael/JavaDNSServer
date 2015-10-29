package nl.saxion.server.DNS;

public class Ipv4 {

	private int s1;
	private int s2;
	private int s3;
	private int s4;
	
	public Ipv4(int s1, int s2, int s3, int s4) {
		super();
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s4 = s4;
	}
	public int getS1() {
		return s1;
	}
	public void setS1(int s1) {
		this.s1 = s1;
	}
	public int getS2() {
		return s2;
	}
	public void setS2(int s2) {
		this.s2 = s2;
	}
	public int getS3() {
		return s3;
	}
	public void setS3(int s3) {
		this.s3 = s3;
	}
	public int getS4() {
		return s4;
	}
	public void setS4(int s4) {
		this.s4 = s4;
	}
	
	public String toString() {
		return s1+"."+s2+"."+s3+"."+s4;
	}
}
