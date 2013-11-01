package project1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.sun.corba.se.pept.transport.Connection;

public class Stage {
	
	public static byte[] stageA() {
		byte[] payload = "hello world/0".getBytes();
		Connection udpConn = new UDPConnection(ConnectionUtils.INIT_UDP_PORT);
		udpConn.send(payload);
		byte[] receivePacket = udpConn.receive();
		return receivePacket;
	}
	
	public static byte[] stageB(int num, int len, int udp_port) {
		
	}
	

}
