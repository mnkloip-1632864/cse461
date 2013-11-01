package project1;

import java.net.Socket;

public class TCPConnection implements Connection {

	private Socket socket;
	
	public TCPConnection(int port) {
		
	}
	
	@Override
	public void send(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] receive(int bufferLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
