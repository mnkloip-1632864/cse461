package project1;

import java.nio.ByteBuffer;

public class Project1Main {

	public static void main(String[] args) {
		byte[] receivedPacket = Stage.stageA();
		ByteBuffer bb = ByteBuffer.wrap(receivedPacket, ConnectionUtils.HEADER_LENGTH, 16);
		int num = bb.getInt();
		int len = bb.getInt(4);
		int udp_port = bb.getInt(8);
		int secretA = bb.getInt(12);
		System.out.println("The num is: " + num);
		System.out.println("The len is: " + len);
		System.out.println("udp_port is: " + udp_port);
		System.out.println("secretA is: " + secretA);
	}

}
