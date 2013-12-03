package server;

import utils.ConnectionUtils;
import utils.TCPConnection;
import utils.TCPServerConnection;

/**
 * A ConnectionPool keeps track of the connections the server contains.
 * It waits for connections to the server and when a connection is 
 * established, it spawns a thread to handle the client's requests.
 */
public class ConnectionPool {
	
	private TCPServerConnection serverConnection;
	
	public ConnectionPool() {
		serverConnection = new TCPServerConnection(ConnectionUtils.SERVER_PORT);
	}
	
	public void start() {
		while(true) {
			TCPConnection connection = serverConnection.accept();
			if(connection == null) {
				continue;
			}
			// Spawn a new thread with the given connection
			ServerThread client = new ServerThread(connection);
			client.start();
		}
	}

}
