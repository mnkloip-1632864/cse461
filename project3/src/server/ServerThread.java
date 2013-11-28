package server;

import utils.TCPConnection;

/**
 * The ServerThread does the work for a particular connection.
 * It asks the client for the files it is willing to share as
 * well as the files it wants to acquire.
 */
public class ServerThread extends Thread {

	private TCPConnection connection;
	
	
	public ServerThread(TCPConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public void start() {
		try {
			
		} finally {
			cleanUp();
		}
	}
	
	/**
	 * Cleans up the state of this thread and letting the server
	 * know that this client cannot transfer files.
	 */
	private void cleanUp() {
		
	}

}
