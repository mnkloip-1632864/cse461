package project2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UDPServerConnection {
	
	private DatagramSocket dataSocket;
	boolean closed;
	
	public UDPServerConnection(int port) {
		try {
			dataSocket = new DatagramSocket(port);
			dataSocket.setSoTimeout(ConnectionUtils.TTL);
			closed = false;
		} catch (SocketException e) {
			e.printStackTrace();
		} 
	}
	
	public void send(byte[] message, InetAddress address, int port) {
		try {
			DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
			dataSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DatagramPacket receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		DatagramPacket packet = new DatagramPacket(buffer, bufferLength);
		try {
			dataSocket.receive(packet);
		} catch (SocketTimeoutException ste) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packet;
	}

	public void disableTimeout() {
		try {
			dataSocket.setSoTimeout(0);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		if(!closed) {
			dataSocket.close();
			closed = true;
		}
	}

}
