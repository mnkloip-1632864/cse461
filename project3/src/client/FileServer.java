package client;

import utils.ConnectionUtils;
import utils.TCPConnection;
import utils.TCPServerConnection;

/**
 * The FileServer handles requests to the Client for a particular file.
 */
public class FileServer extends Thread {
	
	public static final int CHUNK_SIZE = 10000;
	
	private TCPServerConnection fileServer;
	
	public FileServer() {
		fileServer = new TCPServerConnection(ConnectionUtils.FILE_SERVER_PORT);
	}
	
	@Override
	public void run() {
		while(true) {
			TCPConnection connection = fileServer.accept();
			if(connection == null) {
				continue;
			}
			// Spawn a new thread with the given connection
			FileServerThread client = new FileServerThread(connection);
			client.start();
		}
	}
	
}
