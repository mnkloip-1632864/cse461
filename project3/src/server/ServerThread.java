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
			 * Step 1: ask the client for a list of files it's
			 * willing to share.
			 */
			System.out.println("Connection established with client. Requesting files.");
			retrieveAndStoreFiles();
			
			/* 
			 * Step 2: provide the client with a list of files
			 * that it can get from other nodes.
			 */
			
			sendAvailableFiles();
			
			/*
			 * Step 3: get a filename the client wants.
			 * Then return the address of a node that has the
			 * file to the client.
			 */
			
			String fileName = getClientFilenameRequest();
			while(fileName != null) {
				sendNodeAddressContainingFile(fileName);
				fileName = getClientFilenameRequest();
			}
			
		} catch (HeaderException e) {
			System.err.println(e.getMessage());
		} finally {
			cleanUp();
		}
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
	 * @return A String representing the clients request for a file. 
	 *         Returns null if the client wishes to terminate.
	 */
	private String getClientFilenameRequest() {
		byte[] header = connection.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		if(type == MessageType.TERMINATE) {
			return null;
		} else if(type == MessageType.REQUEST) {
			byte[] fileName = connection.receive(payloadLen);
			return new String(fileName);
		}
		return null;
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
	private void retrieveAndStoreFiles() {
		Set<String> fileNames = ConnectionUtils.getFileList(connection);
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
