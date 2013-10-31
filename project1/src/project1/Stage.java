package project1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Stage {
	
	private DatagramSocket dataSocket;
	private DatagramPacket dataPacket;
	
	public Stage() {
		
	}
	
	public void send() {
		try{
			dataSocket = new DatagramSocket(ConnectionUtils.INIT_UDP_PORT);
			byte[] payload = "hello world/0".getBytes();
			int unpadLength = ConnectionUtils.HEADER_LENGTH + payload.length;
			byte[] header = ConnectionUtils.constructHeader(unpadLength, 0, (short)1);
			byte[] packet = new byte[10];
			dataPacket = new DatagramPacket(packet,
					                        packet.length,
					                        InetAddress.getByName(ConnectionUtils.HOST),
					                        ConnectionUtils.INIT_UDP_PORT);
			dataSocket.send(dataPacket);
			
		} catch (SocketException se) {
			System.err.println(se.toString());
			se.printStackTrace();
		} catch (IOException ie) {
			System.err.println(ie.toString());
			ie.printStackTrace();
		}
	}
	
	public void receive() {
		
	}
	

}
