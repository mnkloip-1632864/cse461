package utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerConnection {

	private ServerSocket serverSocket;
	private boolean closed;
	
	public TCPServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
			closed = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Waits for a client to connect to this server, creating a 
	 * TCPConnection from it to be returned to the caller.
	 * @return A TCPConnection for the client that connected to
	 *         the server. Returns null if there is a problem.
	 */
	public TCPConnection accept() {
		try {
			Socket clientSocket = serverSocket.accept();
			return new TCPConnection(clientSocket);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Closes this ServerConnection, cleaning up underlying state.
	 */
	public void close() {
		if(!closed) {
			try {
				if(serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			closed = true;
		}
	}

}
