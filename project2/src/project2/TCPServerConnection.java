package project2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPServerConnection implements ServerConnection {

	private Socket socket;
	
	public TCPServerConnection(int port) {
		try {
			socket = new Socket(InetAddress.getByName(HOST), port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void send(byte[] message) {
		try {
			OutputStream out = socket.getOutputStream();
			out.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public byte[] receive(int bufferLength) {
		byte[] buffer = new byte[bufferLength];
		try {
			InputStream message = socket.getInputStream();
			message.read(buffer, 0, bufferLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
