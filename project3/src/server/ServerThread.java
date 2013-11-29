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
			/* 
			 * Step 1: ask the client for a list of files it's
			 * willing to share.
			 * TODO
			 */
			
			/* 
			 * Step 2: provide the client with a list of files
			 * that it can get from other nodes.
			 * TODO
			 */
			
			/*
			 * Step 3: get a filename the client wants.
			 * Then return the address of a node that has the
			 * file to the client.
			 * TODO
			 */
			
			/*
			 * Step 4: wait for another filename, then proceed 
			 * with step 3 or receive a disconnect message and
			 * cleanup.
			 * TODO
			 */
			
		} finally {
			cleanUp();
		}
	}
	
	/**
	 * Cleans up the state of this thread and letting the server
	 * know that this client cannot transfer files.
	 */
	private void cleanUp() {
		FileFinder finder = FileFinder.getInstance();
		finder.removeAddress(connection.getHostName());
		connection.close();
	}

}
