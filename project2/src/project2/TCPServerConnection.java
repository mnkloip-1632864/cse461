package project2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;

public class TCPServerConnection {

	private ServerSocket serverSocket;
	
	public TCPServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(byte[] message) {
		try {
			OutputStream out = null; //TODO: serverSocket.getOutputStream();
			out.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public byte[] receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		try {
			InputStream message = null; // TODO: serverSocket.getInputStream();
			message.read(buffer, 0, bufferLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
