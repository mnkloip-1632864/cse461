package simple;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import project1.ConnectionUtils;

public class BasicRunner {
	public static void main(String[] args) throws Exception {
		DatagramSocket server = new DatagramSocket(ConnectionUtils.INIT_UDP_PORT, InetAddress.getByName("localhost"));
		DatagramPacket pkt = new DatagramPacket(new byte[24], 24);
		server.receive(pkt);
		System.out.println(Arrays.toString(pkt.getData()));
	}
	
	public static byte[] concat(byte[] first, byte[] second) {
		int length = first.length + second.length;
		int padLen = 4 - length % 4;
		length += padLen == 4 ? 0 : padLen;
		byte[] combo = ByteBuffer.allocate(length).put(first).put(second).array();
		return combo;
	}
}
