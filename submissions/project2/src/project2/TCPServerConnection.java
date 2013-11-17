package project2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPServerConnection {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private boolean closed;
	
	public TCPServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(ConnectionUtils.TTL);
			closed = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean accept() {
		try {
			clientSocket = serverSocket.accept();
		} catch (SocketTimeoutException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void send(byte[] message) {
		try {
			OutputStream out = clientSocket.getOutputStream();
			out.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public byte[] receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		try {
			InputStream message = clientSocket.getInputStream();
			message.read(buffer, 0, bufferLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public void close() {
		if(!closed) {
			try {
				if(serverSocket != null) {
					serverSocket.close();
				}
				if(clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			closed = true;
		}
	}

}
