package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection {

	private Socket socket;
	
	public TCPConnection(String hostName, int port) {
		try {
			socket = new Socket(InetAddress.getByName(hostName), port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TCPConnection(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * @return the host name of the destination for this connection.
	 */
	public String getHostName() {
		String name = socket.getInetAddress().getHostName();
		if(name.equals("localhost")) {
			try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				System.err.println("Cannot find host. Try running client on different machine than server.");
			}
		}
		return name;
	}
	
	/**
	 * Sends the message to the recipient.
	 */
	public void send(byte[] message) {
		send(message, message.length);
	}
	
	public void send(byte[] message, int length) {
		try {
			OutputStream out = socket.getOutputStream();
			if(length < message.length) {
				byte[] smess = new byte[length];
				for (int i = 0; i < smess.length; i++) {
					smess[i] = message[i];
				}
				out.write(smess);
			} else {
				out.write(message);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receives a message of the given length. 
	 */
	public byte[] receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		try {
			InputStream message = socket.getInputStream();
			int readBytes = 0;
			while(readBytes != bufferLength) {
				int bytesRead = message.read(buffer, 0, bufferLength);
				if(bytesRead == -1) {
					System.out.println("Breaking early!");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
					break;
				}
				readBytes += bytesRead;
			}
		} catch (IOException e) {
			throw new TCPException(e);
		}
		return buffer;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
