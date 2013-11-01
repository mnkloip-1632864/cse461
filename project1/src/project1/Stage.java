package project1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Stage {
	private static Connection tcpConn;
	
	public static byte[] stageA() {
		byte[] payload = "hello world\0".getBytes();
		int totalLength = payload.length;// + ConnectionUtils.HEADER_LENGTH;
		byte[] header = ConnectionUtils.constructHeader(totalLength, 0, (short)1);
		byte[] message = ConnectionUtils.merge(header, payload);
		Connection udpConn = new UDPConnection(ConnectionUtils.INIT_UDP_PORT, false);
		udpConn.send(message);
		byte[] receivePacket = udpConn.receive(ConnectionUtils.HEADER_LENGTH + 16);
		udpConn.close();
		return receivePacket;
	}
	
	public static byte[] stageB(int num, int len, int udp_port, int secretA) {
		byte[] payload;
		byte[] header = ConnectionUtils.constructHeader(len + 4, secretA, (short)1);;
		Connection udpConn = new UDPConnection(udp_port, true);
		for (int i = 0; i < num; i++) {
			payload = ByteBuffer.allocate(len + 4).order(ByteOrder.BIG_ENDIAN).putInt(i).array();
			byte[] message = ConnectionUtils.merge(header, payload);
			udpConn.send(message);
			int receivedPacketLength = ConnectionUtils.HEADER_LENGTH + 4;
			byte[] receivedPacket = udpConn.receive(receivedPacketLength);
			while (receivedPacket == null ||
				   ByteBuffer.wrap(receivedPacket).getInt(12) != i) {
				udpConn.send(message);
				receivedPacket = udpConn.receive(receivedPacketLength);
			}
		}
		byte[] received = udpConn.receive(ConnectionUtils.HEADER_LENGTH + 8);
		udpConn.close();
		return received;
	}
	
	public static byte[] stageC(int tcp_port) {
		tcpConn = new TCPConnection(tcp_port);
		int receivedLength = ConnectionUtils.HEADER_LENGTH + 16;
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
		byte[] receivedPacket = tcpConn.receive(ConnectionUtils.HEADER_LENGTH + 4);
		tcpConn.close();
		return receivedPacket;
	}
}
