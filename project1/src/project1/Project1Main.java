package project1;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Project1Main {

	public static final int DEBUG_LEVEL=2;
	
	public static void main(String[] args) {
		// Stage A
		byte[] receivedPacketA = Stage.stageA();
		ByteBuffer bb = ByteBuffer.wrap(receivedPacketA);
		int num = bb.getInt(12);
		int len = bb.getInt(16);
		int udp_port = bb.getInt(20);
		int secretA = bb.getInt(24);
		printResultsA(bb);
		
		// Stage B
		byte[] receivedPacketB = Stage.stageB(num, len, udp_port, secretA);
		bb = ByteBuffer.wrap(receivedPacketB);
		int tcp_port = bb.getInt(12);
		printResultsB(bb);
		
		// Stage C
		byte[] receivedPacketC = Stage.stageC(tcp_port);
		bb = ByteBuffer.wrap(receivedPacketC);
		int num2 = bb.getInt(12);
		int len2 = bb.getInt(16);
		int secretC = bb.getInt(20);
		byte c = bb.get(24);
		printResultsC(bb);
		
		// Stage D
		byte[] receivedPacketD = Stage.stageD(num2, len2, secretC, c);
		bb = ByteBuffer.wrap(receivedPacketD);
		printResultsD(bb);
	}
	
	/*
	 * The following helper methods serve as a way to print the results of 
	 * the different stages to standard out based upon the DEBUG_LEVEL.
	 * If DEBUG_LEVEL is 0, then all of the stages secret values will be printed
	 * If DEBUG_LEVEL is 1, then all of the fields returned in the packet from the
	 * server will be printed out as well as all the secret values
	 * If DEBUG_LEVEL is 2, then in addition to all the prints of DEBUG_LEVEL 1, the
	 * raw packet  returned from the server will be printed.
	 */
	
	private static void printResultsA(ByteBuffer bb) {
		System.out.println("Stage A: ");
		switch(DEBUG_LEVEL) {
		case 2:
			System.out.println(Arrays.toString(bb.array()));			
		case 1:
			int num = bb.getInt(12);
			int len = bb.getInt(16);
			int udp_port = bb.getInt(20);
			System.out.println("The num is: " + num);
			System.out.println("The len is: " + len);
			System.out.println("udp_port is: " + udp_port);			
		case 0:
		default:
			int secretA = bb.getInt(24);
			System.out.println("secretA is: " + secretA);
			break;
		}
	}
	
	private static void printResultsB(ByteBuffer bb) {
		System.out.println("Stage B: ");
		switch(DEBUG_LEVEL) {
		case 2:
			System.out.println(Arrays.toString(bb.array()));
		case 1:
			int tcp_port = bb.getInt(12);
			System.out.println("The tcp_port is: " + tcp_port);
		case 0:
		default:
			int secretB = bb.getInt(16);			
			System.out.println("secretB is: " + secretB);
			break;
		}
	}
	
	private static void printResultsC(ByteBuffer bb) {
		System.out.println("Stage C: ");
		switch (DEBUG_LEVEL) {
		case 2:
			System.out.println(Arrays.toString(bb.array()));
		case 1:
			int num2 = bb.getInt(12);
			int len2 = bb.getInt(16);
			byte c = bb.get(24);
			System.out.println("num2 is: " + num2);
			System.out.println("len2 is: " + len2);
			System.out.println("char c is: " + c);			
		case 0:
		default:
			int secretC = bb.getInt(20);
			System.out.println("secretC is: " + secretC);			
			break;
		}
	}
	
	private static void printResultsD(ByteBuffer bb) {
		System.out.println("Stage D: ");
		switch (DEBUG_LEVEL) {
		case 2:
			System.out.println(Arrays.toString(bb.array()));
		case 1:		
		case 0:
		default:
			int secretD = bb.getInt(12);
			System.out.println("secretD is: " + secretD);			
			break;
		}
	}
}
