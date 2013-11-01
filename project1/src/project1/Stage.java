package project1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Stage {
	
	public static byte[] stageA() {
		byte[] payload = "hello world/0".getBytes();
		Connection udpConn = new UDPConnection(ConnectionUtils.INIT_UDP_PORT);
		udpConn.send(payload);
		byte[] receivePacket = udpConn.receive();
		udpConn.close();
		return receivePacket;
	}
	
	public static byte[] stageB(int num, int len, int udp_port) {
		byte[] payload;
		Connection udpConn = new UDPConnection(udp_port);
		for (int i = 0; i < num; i++) {
			payload = ByteBuffer.allocate(len + 4).order(ByteOrder.BIG_ENDIAN).putInt(i).array();
			udpConn.send(payload);
			int receivedPacketLength = ConnectionUtils.HEADER_LENGTH + 4;
			byte[] receivedPacket = udpConn.receive(receivedPacketLength);
			while (receivedPacket == null ||
				   ByteBuffer.wrap(receivedPacket, ConnectionUtils.HEADER_LENGTH, 4).getInt() != i) {
				udpConn.send(payload);
				receivedPacket = udpConn.receive(receivedPacketLength);
			}
		}
		byte[] received = udpConn.receive();
		udpConn.close();
		return received;
	}
	
	public static byte[] stageCAndD(int tcp_port) {
		Connection tcpConn = new TCPConnection(tcp_port);
		int receivedLength = ConnectionUtils.HEADER_LENGTH + 16;
		byte[] receivedPacket = tcpConn.receive(receivedLength);
		ByteBuffer actualData = ByteBuffer.wrap(receivedPacket, ConnectionUtils.HEADER_LENGTH, 13);
		int num2 = actualData.getInt();
		int len2 = actualData.getInt(4);
	}
	
	

}
