package server;

import java.nio.ByteBuffer;
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
	public void run() {
		try {
			/*
			 * Process client requests. These requests
			 * include getting a list of available files, getting
			 * a node that has a specific file, or termination.
			 */
			boolean continueServing = true;
			while(continueServing) {
				continueServing = processClientRequest();
			}
			
		} catch (HeaderException e) {
			System.err.println(e.getMessage());
		} finally {
			cleanUp();
		}
	}

	/**
	 * Reads a request from the client and executes the request.
	 * @return false if the request was a terminate request or invalid. True otherwise.
	 */
	private boolean processClientRequest() {
		// get the header
		byte[] header = connection.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		
		// use the appropriate protocol.
		switch(type) {
		case MessageType.REQUEST_AVAILABLE_FILES:
			// return the available files
			sendAvailableFiles();
			break;
		case MessageType.REQUEST:
			// return a node with the requested file.
			String fileName = getClientFilenameRequest(payloadLen);				
			sendNodeAddressContainingFile(fileName);
			break;
		case MessageType.UPDATE_AVAILABLE_FILES:
			// Update the client node's sharing files in the FileFinder
			FileFinder finder = FileFinder.getInstance();
			finder.removeAddress(connection.getHostName());
			retrieveAndStoreFiles(payloadLen);
			break;
		default:
			return false;
		}
		return true;
	}

	private void sendNodeAddressContainingFile(String fileName) {
		String nodeAddress = FileFinder.getInstance().getAddressFor(fileName);
		if(nodeAddress == null) {
			nodeAddress = "null";
		}
		byte[] payload = nodeAddress.getBytes();
		byte[] header = ConnectionUtils.constructHeader(payload.length, MessageType.REQUEST);
		byte[] message = ConnectionUtils.merge(header, payload);
		connection.send(message);
	}

	/**
	 * @param payloadLen 
	 * @return A String representing the clients request for a file. 
	 *         Returns null if the client wishes to terminate.
	 */
	private String getClientFilenameRequest(int payloadLen) {
		byte[] fileName = connection.receive(payloadLen);
		return new String(fileName);
	}

	/**
	 * Send a Set of available files to the client.
	 */
	private void sendAvailableFiles() {
		FileFinder fileFinder = FileFinder.getInstance();
		Set<String> filesToGet = fileFinder.getFileNames();
		ConnectionUtils.sendFileList(connection, filesToGet);
	}

	/**
	 * Retrieves a list of files that the client is willing to share and store this in the FileFinder.
	 */
	private void retrieveAndStoreFiles(int payloadLen) {
		byte[] payload = connection.receive(payloadLen);
		Set<String> fileNames = ConnectionUtils.getFileNames(payload);
		// Add the fileNames to the FileFinder to be found at this client
		FileFinder fileFinder = FileFinder.getInstance();
		fileFinder.addFilesToAddress(connection.getHostName(), fileNames);
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
