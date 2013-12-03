package client;

import java.io.File;

import utils.TCPConnection;

/**
 * The FileServerThread keeps track of a connection to a particular client
 * and their respective file request.
 */
public class FileServerThread extends Thread {

	private TCPConnection connection;

	public FileServerThread(TCPConnection connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		try {
			// Receive the name of the file to transmit
			String fileName = receiveFileName();
			
			// Get the file path on the local machine, letting the user 
			// know if the file does not exist.
			File f = retrieveFile(fileName);
			
			// If the file exists, transmit it.
			transmitFile(f);
			
		} catch(FileRetrievalException fre) {
			// the file the client requested cannot be found, tell the client
			sendFileNotFound();
		} finally {
			connection.close();
		}
	}

	/**
	 * Receives a fileName from the client. 
	 * @return the name of the file to transmit.
	 */
	private String receiveFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Looks up the fileName, returning a File instance for the file if one exists.
	 * @param fileName the name of the file to look up.
	 * @return a File object that refers to the specified filename.
	 * @throws FileRetrievalException if the file could not be found (a mapping doesn't
	 * 		   exist or the file is no longer on disk at the mapped location).
	 */
	private File retrieveFile(String fileName) throws FileRetrievalException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Transmits the contents of 'f' to the client. It sends the file to the client
	 * in CHUNK_SIZE chunks.
	 * @param f the file to transmit
	 */
	private void transmitFile(File f) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Lets the client know that the requested file could not be found.
	 */
	private void sendFileNotFound() {
		// TODO Auto-generated method stub
		
	}

}
