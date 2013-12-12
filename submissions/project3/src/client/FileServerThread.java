package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import utils.ApplicationFields;
import utils.ConnectionUtils;
import utils.HeaderException;
import utils.MessageType;
import utils.TCPConnection;
import utils.TCPException;

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

		} catch(FileRetrievalException e) {
			// the file the client requested cannot be found, tell the client
			sendFileNotFound();
		} catch(FileTransmissionException e) {
			sendFileTransmissionError();
		} catch(HeaderException e) {
			System.err.println("HeaderError: " + e.toString());
		} catch(TCPException e) {
			System.err.print("TCP error");
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
	 * @throws FileRetrievalException if transmission fails.
	 */
	private void transmitFile(File f) throws FileTransmissionException {
		// Construct and send the file metadata
		long fileSize = f.length();
		int numChunks = (int) (fileSize / ApplicationFields.chunkSize) + 1;
		ByteBuffer buf = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
		buf.putLong(fileSize).putInt(numChunks).putInt(ApplicationFields.chunkSize);
		byte[] payload = buf.array();
		byte[] header = ConnectionUtils.constructHeader(payload.length, MessageType.FILE_META);
		byte[] message = ConnectionUtils.merge(header, payload);
		connection.send(message);
		// send the file in CHUNK_SIZE chunks.
		sendFileContents(f);

	}
	/**
	 * Sends the contents of the File in numChunks number of chunks.
	 * @throws FileTransmissionException if a problem with I/O occurs.
	 */
	private void sendFileContents(File f)
			throws FileTransmissionException {
		InputStream input = null;
		BufferedInputStream bufInput = null;
		try {
			input = new FileInputStream(f);
			bufInput = new BufferedInputStream(input); 
			long numBytesSent = 0;
			while(numBytesSent < f.length()) {
				long numBytesLeft = f.length() - numBytesSent;
				int size = numBytesLeft > ApplicationFields.chunkSize ? ApplicationFields.chunkSize : (int) numBytesLeft;
				// try to get a chunk of data from the file to send
				byte[] chunk = new byte[size];
				int numBytesRead = bufInput.read(chunk, 0, chunk.length);
				if(numBytesRead == -1) {
					break;
				}
				// send the chunk to the client
				connection.send(chunk, numBytesRead);
				
				// update the number of bytes sent
				numBytesSent += numBytesRead;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
			}
		} catch (IOException e) {
			throw new FileTransmissionException();
		} finally {
			if(input != null) {
				if(bufInput != null) {
					try {
						bufInput.close();
					} catch (IOException e) {}
				} else {
					try {
						input.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	/**
	 * Sends a termination message to the client stating that there was an I/O problem
	 */
	private void sendFileTransmissionError() {
		byte[] message = ConnectionUtils.constructTerminateMessage("Error: I/O problems on source node.");
		connection.send(message);
	}

	/**
	 * Lets the client know that the requested file could not be found.
	 */
	private void sendFileNotFound() {
		byte[] message = ConnectionUtils.constructTerminateMessage("Error: File Not Found");
		connection.send(message);
	}

}
