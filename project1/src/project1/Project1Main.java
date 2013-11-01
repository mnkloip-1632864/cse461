package project1;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Project1Main {

	public static void main(String[] args) {
		// Stage A
		byte[] receivedPacketA = Stage.stageA();
		ByteBuffer bb = ByteBuffer.wrap(receivedPacketA);
		System.out.println(Arrays.toString(bb.array()));
		int num = bb.getInt(12);
		int len = bb.getInt(16);
		int udp_port = bb.getInt(20);
		int secretA = bb.getInt(24);
		System.out.println("Stage A: ");
		System.out.println("The num is: " + num);
		System.out.println("The len is: " + len);
		System.out.println("udp_port is: " + udp_port);
		System.out.println("secretA is: " + secretA);
		// Stage B
		byte[] receivedPacketB = Stage.stageB(num, len, udp_port, secretA);
		bb = ByteBuffer.wrap(receivedPacketB);
		int tcp_port = bb.getInt(12);
		int secretB = bb.getInt(16);
		System.out.println("Stage B: ");
		System.out.println("The tcp_port is: " + tcp_port);
		System.out.println("The secretB is: " + secretB);
	}

}
