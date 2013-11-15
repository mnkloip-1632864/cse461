package project2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDPServerConnection {
	
	private DatagramSocket dataSocket;
	private int port;
	private InetAddress latestReceivedAddress;
	
	public UDPServerConnection(int port, boolean enableTimeout) {
		try {
			dataSocket = new DatagramSocket(port, InetAddress.getByName("localhost"));
			if (enableTimeout)
				dataSocket.setSoTimeout(ConnectionUtils.TTL);
			this.port = port;
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void send(byte[] message, InetAddress address) {
		try {
			DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
			dataSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		DatagramPacket packet = new DatagramPacket(buffer, bufferLength);
		try {
			dataSocket.receive(packet);
			latestReceivedAddress = packet.getAddress();
		} catch (SocketTimeoutException ste) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packet.getData();
	}

	public InetAddress getLatestAddress() {
		return latestReceivedAddress;
	}
	
	public void close() {
		dataSocket.close();
	}

}
