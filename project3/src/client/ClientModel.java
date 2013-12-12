package client;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Set;

import utils.ConnectionUtils;
import utils.MessageType;
import utils.TCPConnection;

public class ClientModel {

	private static final String SERVER_ADDR = "108.179.185.89";

	private static String inputDirectory = ".." + File.separator + "inputFiles";

	private TCPConnection connectionToServer;
	private FileMapping fileMap;

	public ClientModel() {
		connectionToServer = new TCPConnection(SERVER_ADDR, ConnectionUtils.SERVER_PORT);
		updateInputFiles();
	}

	/**
	 * Populates the fileMap to hold the files to be shared by this machine.
	 */
	private void populateFileMap() {
		File dir = new File(inputDirectory);
		File[] availableFiles = dir.listFiles();
		for (File file : availableFiles) {
			fileMap.addFile(file.getName(), file.getPath());
		}
	}
	
	/**
	 * Sends the files that this server is hosting to the server.
	 */
	public void sendAvailableFilesToServer() {
		Set<String> localFiles = fileMap.getAvailableFilenames();
		ConnectionUtils.sendFileList(connectionToServer, localFiles);
	}
	
	/**
	 * Get the list of files that are available for download. 
	 * @return a set of String where each of the String represents the file
	 *         name of a file that is available in the remote machine. 
	 */
	public Set<String> getAvailableFiles() {
		// Request a list of available files
		byte[] header = ConnectionUtils.constructHeader(0, MessageType.REQUEST_AVAILABLE_FILES);
		connectionToServer.send(header);
		// Get the response
		Set<String> fileNames = ConnectionUtils.getFileList(connectionToServer);
		return fileNames;
	}
	
	/**
	 * Checks for the file in the files that are currently owned and the files that are currently available.
	 * Throwing an exception if there is a problem (we already own the file or the file isn't available).
	 */
	public void checkFileExists(String fileToGet, Set<String> filesAvailable) throws FileRetrievalException {
		// Check that whether the user actually has the file locally. 
		if (fileMap.getPath(fileToGet) != null) {
			throw new FileRetrievalException("The file is currently stored on your local machine.");
		} 
		// Check that the user requested file actually matches one of the files available to get
		else if (!filesAvailable.contains(fileToGet)) {
			throw new FileRetrievalException("The input file name does not match any of the available files.");
		}
	}
		
	/**
	 * Retrieves a FileMapping for the current client.
	 */
	public FileMapping getFileMapping() {
		return fileMap;
	}
	
	/** 
	 * Retrieves a node that contains the given file.
	 */
	public String getNodeWithFile(String fileToGet) throws FileRetrievalException {
		requestFile(fileToGet);
		String s = getFileLocation();
		if(s.equals("null")) {
			throw new FileRetrievalException("File could not be found by the server.");
		}
		return s;
	}
	
	/**
	 * Send server request to get ip of the node that has the file
	 * @param fileToGet the file name that the client wants
	 */
	private void requestFile(String fileToGet) {
		byte[] header = ConnectionUtils.constructHeader(fileToGet.length(), MessageType.REQUEST);
		byte[] message = ConnectionUtils.merge(header, fileToGet.getBytes());
		connectionToServer.send(message);
	}
	
	/**
	 * Get the IP address of the node that has the requested file. 
	 * @return the ip address of the node that has the requested file. 
	 */
	private String getFileLocation() {
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
	 * Cleans up the connection to the Server by sending a termination message and then closing
	 * the connection.
	 */
	public void cleanup(String message) {
		byte[] terminate = ConnectionUtils.constructTerminateMessage(message);
		connectionToServer.send(terminate);
		connectionToServer.close();		
	}
	
	public static void setInputFileDirectory(String path) {
		inputDirectory = path;
	}
	
	public void updateInputFiles() {
		fileMap = new FileMapping();
		populateFileMap();
		updateServerFiles();
	}
	
	private void updateServerFiles() {
		Set<String> fileNames = fileMap.getAvailableFilenames();
		byte[] files = ConnectionUtils.makeFileBytes(fileNames);
		byte[] header = ConnectionUtils.constructHeader(files.length, MessageType.UPDATE_AVAILABLE_FILES);
		byte[] message = ConnectionUtils.merge(header, files);		
		connectionToServer.send(message);
	}
	
	
}
