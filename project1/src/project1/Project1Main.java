package project1;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Project1Main {

	public static void main(String[] args) {
		byte[] receivedPacket = Stage.stageA();
		ByteBuffer bb = ByteBuffer.wrap(receivedPacket);
		System.out.println(Arrays.toString(bb.array()));
		int num = bb.getInt(12);
		int len = bb.getInt(16);
		int udp_port = bb.getInt(20);
		int secretA = bb.getInt(24);
		System.out.println("The num is: " + num);
		System.out.println("The len is: " + len);
		System.out.println("udp_port is: " + udp_port);
		System.out.println("secretA is: " + secretA);
	}

}
