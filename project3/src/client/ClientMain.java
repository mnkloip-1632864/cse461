package client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Set;

import utils.ConnectionUtils;
import utils.HeaderException;
import utils.MessageType;
import utils.TCPConnection;
import utils.TCPException;

public class ClientMain {

	private static final String INPUT_FILE_LOCATION = ".." + File.separator + "inputFiles";
	private static final String OUTPUT_FILE_LOCATION = ".." + File.separator + "receivedFiles";
	private static final String SERVER_ADDR = "108.179.184.20";
	private static final int NAMES_PER_LINE = 3;

	private static TCPConnection connectionToServer;
	private static TCPConnection connectionToPeer;
	private static FileMapping fileMap;
	
	private static PrintStream log; //TODO
	

	public static void main(String[] args) {
		
		try {
			log = new PrintStream(new File("log.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// Start the local FileServer
		FileServer fs = new FileServer();
		fs.start();

		// Setup the local fileMapping
		fileMap = new FileMapping();
		connectionToServer = new TCPConnection(SERVER_ADDR, ConnectionUtils.SERVER_PORT);
		populateFileMap();
		Set<String> localFiles = fileMap.getAvailableFilenames();

		// Send the server the list of files that we can share.
		ConnectionUtils.sendFileList(connectionToServer, localFiles);
		Set<String> filesAvailable = getAndShowAvailableFiles();
		Scanner s = new Scanner(System.in);
		while (true) {
			System.out.print("Please type in the name of the file you want " +
					"to get (Press Ctrl-D to quit the program): ");
			if (s.hasNext()) {
				String fileToGet = s.next();
				// Check that whether the user actually has the file locally. 
				if (localFiles.contains(fileToGet)) {
					System.out.println("The file is currently stored on your local machine.");
					//continue;
				} 
				// Check that the user requested file actually matches one of the files available to get
				else if (!filesAvailable.contains(fileToGet)) {
					System.out.println("The input file name does not match any of the available files.");
				}

				/*
				 * get the file location of the file user requested  
				 */
				requestFile(fileToGet);

				/*
				 * get the ip address of the node that has the requested file
				 */
				String nodeIp = getFileLocation();

				System.out.println("nodeIp = " + nodeIp);

				/*
				 * Retrive file from the peer
				 */
				connectionToPeer = new TCPConnection(nodeIp, ConnectionUtils.FILE_SERVER_PORT);
				sendFileName(fileToGet);

				byte[] fileMeta = getFileMeta();
				if (fileMeta == null) {
					connectionToPeer.close();
					break;
				}
				ByteBuffer data = ByteBuffer.wrap(fileMeta);

				long fileSize = data.getLong(0);
				int numChunks = data.getInt(8);
				int chunkSize = data.getInt(12);
				
				System.out.println("NumChunks = " + numChunks); //TODO
				
				try {
					if (fileSize <= 0) {
						throw new FileTransmissionException("File size received is incorrect.");

					}
					if (numChunks <= 0) {
						throw new FileTransmissionException("Num chunks received is incorrect.");
					}
					if (chunkSize <= 0) {
						throw new FileTransmissionException("Chunk size received is incorrect.");
					}
				} catch (FileTransmissionException e) {
					cleanupPeerConn("File meta data received is incorrect!");
					e.printStackTrace();
				}

				// retrieve the file and store it to disk
				if(!retrieveAndStoreFile(fileSize, fileToGet)) {
					// problem with file retrieval
					// TODO
				}

			} else {
				byte[] terminate = ConnectionUtils.constructTerminateMessage("User is done!");
				connectionToServer.send(terminate);
				connectionToServer.close();
				break;
			}
		}
		s.close();
	}

	/**
	 * Populates the fileMap to hold the files to be shared by this machine.
	 */
	private static void populateFileMap() {
		File dir = new File(INPUT_FILE_LOCATION);
		File[] availableFiles = dir.listFiles();
		for (File file : availableFiles) {
			fileMap.addFile(file.getName(), file.getPath());
		}
	}

	public static boolean retrieveAndStoreFile(long fileSize, String fileToGet) {
		BufferedOutputStream bufferedOut = null;
		try {
			FileOutputStream out = new FileOutputStream(OUTPUT_FILE_LOCATION + File.separator + fileToGet);
			bufferedOut = new BufferedOutputStream(out);
			
			long numBytesReceived = 0;
			while(numBytesReceived < fileSize) {
				long numBytesLeft = fileSize - numBytesReceived;
				int size = numBytesLeft > FileServer.CHUNK_SIZE ? FileServer.CHUNK_SIZE : (int) numBytesLeft;
				byte[] chunk = connectionToPeer.receive(size);
				bufferedOut.write(chunk);
				numBytesReceived += size;
			}
			
//			for (int i = 0; i < numChunks; i++) {
//				byte[] header = connectionToPeer.receive(ConnectionUtils.HEADER_SIZE);
//				
//				System.out.println(i + "th header received: " + Arrays.toString(header)); //TODO
//				
//				ByteBuffer buf = ByteBuffer.wrap(header);
//				ConnectionUtils.checkMagic(buf);
//				int payloadLen = buf.getInt(4);
//				byte type = buf.get(8);
//				byte[] message = connectionToPeer.receive(payloadLen);
//				
//				log.println(i + "th received:" + Arrays.toString(header) + Arrays.toString(message)); //TODO
//				
//				
//				if (type == MessageType.TERMINATE) {
//					System.out.println(new String(message));
//					return;
//				} else if (type == MessageType.FILE_DATA) {
//					bufferedOut.write(message);
//					bytesReceived += payloadLen;
//				} else {
//					throw new HeaderException("Wrong message type!");
//				}
//			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (TCPException e) {
			return false;
		} finally {
			if (bufferedOut != null) {
				try {
					bufferedOut.flush();
					bufferedOut.close();
				} catch (IOException e) {}
			}
		}
		return true;

	}

	public static byte[] getFileMeta() {
		byte[] header = connectionToPeer.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		byte[] message = connectionToPeer.receive(payloadLen);

		if (type == MessageType.TERMINATE) {
			System.out.println("Transmission terminated: " + new String(message));
		} else if (type == MessageType.FILE_META) {
			if (payloadLen != 16) {
				throw new HeaderException("Wrong payload length!");
			}
			return message;
		} else {
			System.out.println("Error in retrieving file meta data!");
		}
		return null;
	}

	public static void cleanupPeerConn(String message) {
		byte[] terminate = ConnectionUtils.constructTerminateMessage(message);
		connectionToPeer.send(terminate);
		connectionToPeer.close();
	}

	public static FileMapping getFileMapping() {
		return fileMap;
	}

	/**
	 * Send the file name to the source
	 * @param fileToGet
	 */
	public static void sendFileName(String fileToGet) {
		byte[] fileName = fileToGet.getBytes();
		byte[] header = ConnectionUtils.constructHeader(fileName.length, MessageType.REQUEST);
		byte[] message = ConnectionUtils.merge(header, fileName);
		connectionToPeer.send(message);
	}

	/**
	 * Get the IP address of the node that has the requested file. 
	 * @return the ip address of the node that has the requested file. 
	 */
	public static String getFileLocation() {
		byte[] header = connectionToServer.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		if (type == MessageType.REQUEST) {
			byte[] nodeIp = connectionToServer.receive(payloadLen);
			return new String(nodeIp);
		}
		return null;
	}

	/**
	 * Send server request to get ip of the node that has the file
	 * @param fileToGet the file name that the client wants
	 */
	public static void requestFile(String fileToGet) {
		byte[] header = ConnectionUtils.constructHeader(fileToGet.length(), MessageType.REQUEST);
		byte[] message = ConnectionUtils.merge(header, fileToGet.getBytes());
		connectionToServer.send(message);
	}

	/**
	 * Get the list of files that are available for download. 
	 * @return a set of String where each of the String represents the file
	 *         name of a file that is available in the remote machine. 
	 */
	public static Set<String> getAndShowAvailableFiles() {
		Set<String> fileNames = ConnectionUtils.getFileList(connectionToServer);
		System.out.println("Available files are shown below: ");
		int i = 0;
		for (String fileName: fileNames) {
			System.out.print(fileName);
			if (i != NAMES_PER_LINE - 1) {
				System.out.print("\t");
			} else {
				System.out.println();
				i = -1;
			}
			i++;
		}
		System.out.println();
		return fileNames;
	}



}
