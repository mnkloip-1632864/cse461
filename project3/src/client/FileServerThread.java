package client;

import java.io.File;
import java.nio.ByteBuffer;

import utils.ConnectionUtils;
import utils.HeaderException;
import utils.MessageType;
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
		} catch(HeaderException he) {
			System.err.println("HeaderError: " + he.toString());
		} finally {
			connection.close();
		}
	}

	/**
	 * Receives a fileName from the client. 
	 * @return the name of the file to transmit.
	 */
	private String receiveFileName() {
		byte[] header = connection.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer bb = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(bb);
		int payloadLen = bb.getInt(4);
		byte type = bb.get(8);
		if(type != MessageType.REQUEST) {
			throw new HeaderException("Need to send a filename request to the source first.");
		}
		byte[] filename = connection.receive(payloadLen);
		return new String(filename);
	}
	
	/**
	 * Looks up the fileName, returning a File instance for the file if one exists.
	 * @param fileName the name of the file to look up.
	 * @return a File object that refers to the specified filename.
	 * @throws FileRetrievalException if the file could not be found (a mapping doesn't
	 * 		   exist or the file is no longer on disk at the mapped location).
	 */
	private File retrieveFile(String fileName) throws FileRetrievalException {
		FileMapping fileMap = ClientMain.getFileMapping();
		String filePath = fileMap.getPath(fileName);
		if(filePath == null) {
			throw new FileRetrievalException();
		}
		return new File(filePath);
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
