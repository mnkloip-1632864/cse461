package client;

import utils.ApplicationFields;
import utils.TCPConnection;
import utils.TCPServerConnection;

/**
 * The FileServer handles requests to the Client for a particular file.
 */
public class FileServer extends Thread {
	
	private TCPServerConnection fileServer;
	
	public FileServer() {
		fileServer = new TCPServerConnection(ApplicationFields.fileServerPort);
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
