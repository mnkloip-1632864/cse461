package project1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Stage {
	private static final String HELLO_WORLD = "hello world\0";
	private static final int INT_SIZE = 4;
	private static Connection tcpConn;
	
	public static byte[] stageA() {
		byte[] payload = HELLO_WORLD.getBytes();
		int totalLength = payload.length;
		byte[] header = ConnectionUtils.constructHeader(totalLength, 0, (short)1);
		byte[] message = ConnectionUtils.merge(header, payload);
		Connection udpConn = new UDPConnection(ConnectionUtils.INIT_UDP_PORT, false);
		udpConn.send(message);
		int responseSize = ConnectionUtils.HEADER_LENGTH + 4 * INT_SIZE;
		byte[] receivePacket = udpConn.receive(responseSize);
		udpConn.close();
		return receivePacket;
	}
	
	public static byte[] stageB(int num, int len, int udp_port, int secretA) {
		byte[] payload;
		byte[] header = ConnectionUtils.constructHeader(len + 4, secretA, (short)1);;
		Connection udpConn = new UDPConnection(udp_port, true);
		// send packets to the server, re-sending if an acknowledgment isn't received
		for (int i = 0; i < num; i++) {
			payload = ByteBuffer.allocate(len + 4).order(ByteOrder.BIG_ENDIAN).putInt(i).array();
			byte[] message = ConnectionUtils.merge(header, payload);
			udpConn.send(message);
			int receivedPacketLength = ConnectionUtils.HEADER_LENGTH + INT_SIZE;
			byte[] receivedPacket = udpConn.receive(receivedPacketLength);
			while (receivedPacket == null ||
				   ByteBuffer.wrap(receivedPacket).getInt(12) != i) {
				udpConn.send(message);
				receivedPacket = udpConn.receive(receivedPacketLength);
			}
		}
		int responseSize = ConnectionUtils.HEADER_LENGTH + 2 * INT_SIZE;
		byte[] received = udpConn.receive(responseSize);
		udpConn.close();
		return received;
	}
	
	public static byte[] stageC(int tcp_port) {
		tcpConn = new TCPConnection(tcp_port);
		int receivedLength = ConnectionUtils.HEADER_LENGTH + 4 * INT_SIZE;
		byte[] receivedPacket = tcpConn.receive(receivedLength);
		return receivedPacket;
	}
	
	public static byte[] stageD(int num2, int len2, int secretC, byte c) {
		byte[] payload;
		byte[] header = ConnectionUtils.constructHeader(len2, secretC, (short)1);
		for (int i = 0; i < num2; i++) {
			ByteBuffer payloadBuffer = ByteBuffer.allocate(len2).order(ByteOrder.BIG_ENDIAN);
			for (int j = 0; j < len2; j++) {
				payloadBuffer.put(c);
			}
			payload = payloadBuffer.array();
			byte[] message = ConnectionUtils.merge(header, payload);
			tcpConn.send(message);
		}
		int responseSize = ConnectionUtils.HEADER_LENGTH + INT_SIZE;
		byte[] receivedPacket = tcpConn.receive(responseSize);
		tcpConn.close();
		return receivedPacket;
	}
}
