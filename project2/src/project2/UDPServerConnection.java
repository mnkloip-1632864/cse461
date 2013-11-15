package project2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDPServerConnection implements ServerConnection {

	private DatagramSocket dataSocket;
	private int port;
	
	public UDPServerConnection(int port, boolean enableTimeout) {
		try {
			dataSocket = new DatagramSocket();
			dataSocket.connect(InetAddress.getByName(HOST), port);
			if (enableTimeout)
				dataSocket.setSoTimeout(ServerConnection.TTL);
			this.port = port;
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void send(byte[] message) {
		try {
			InetAddress addr = InetAddress.getByName(HOST);
			DatagramPacket packet = new DatagramPacket(message, message.length, addr, port);
			dataSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		DatagramPacket packet = new DatagramPacket(buffer, bufferLength);
		try {
			dataSocket.receive(packet);
		} catch (SocketTimeoutException ste) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packet.getData();
	}

	@Override
	public void close() {
		dataSocket.close();
	}

}
