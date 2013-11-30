package server;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import utils.ConnectionUtils;
import utils.HeaderException;
import utils.MessageType;
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
			 */
			// get the header to find out how big the list is.
			byte[] header = connection.receive(ConnectionUtils.HEADER_SIZE);
			ByteBuffer buf = ByteBuffer.wrap(header);
			int magic = buf.getInt(0);
			if(magic != ConnectionUtils.MAGIC) {
				throw new HeaderException("Magic number not correct.");
			}
			int payloadLen = buf.getInt(4);
			byte type = buf.get(8);
			if(type != MessageType.LIST) {
				throw new HeaderException("need to send a list of elements to the Server first.");
			}
			// get the list
			byte[] list = connection.receive(payloadLen);
			Set<String> fileNames = getFileNames(list);
			// Add the fileNames to the FileFinder to be found at this client
			FileFinder fileFinder = FileFinder.getInstance();
			fileFinder.addFilesToAddress(connection.getHostName(), fileNames);
			
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
	 * Retrieves Strings representing fileNames from the array of bytes
	 * @param bytes the array of bytes containing filenames separated by '\0'
	 * 			    characters. 
	 * @return a Set containing all of the filenames embedded in the byte array.
	 */
	private Set<String> getFileNames(byte[] bytes) {
		Set<String> files = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			if(b == 0) {
				files.add(sb.toString());
				sb = new StringBuilder();
			} else {
				sb.append((char)b);
			}
		}
		return files;
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
