package client;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utils.ConnectionUtils;
import utils.MessageType;
import utils.TCPConnection;

public class ClientMain {
	
	private static final String FILE_LOCATION = "../files";
	private static final String SERVER_ADDR = "localhost";
	private static final int NAMES_PER_LINE = 3;
	private static TCPConnection connection;

	public static void main(String[] args) {
		connection = new TCPConnection(SERVER_ADDR, ConnectionUtils.SERVER_PORT);
	    Set<String> filesHad = sendFileList();
	    Set<String> filesAvailable = getAndShowAvailableFiles();
	    Scanner s = new Scanner(System.in);
		while (true) {
			System.out.print("Please type in the name of the file you want " +
					"to get (Press Ctrl-D to quit the program): ");
			if (s.hasNext()) {
				String fileToGet = s.next();
				// Check that whether the user actually has the file locally. 
				if (filesHad.contains(fileToGet)) {
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
								
			} else {
				byte[] header = ConnectionUtils.constructHeader(0, MessageType.TERMINATE);
				connection.send(header);
				connection.close();
				break;
			}
		}
		s.close();
	}
	
	/**
	 * Get the IP address of the node that has the requested file. 
	 * @return the ip address of the node that has the requested file. 
	 */
	public static String getFileLocation() {
		byte[] header = connection.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		if (type == MessageType.REQUEST) {
			byte[] nodeIp = connection.receive(payloadLen);
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
		connection.send(message);
	}
	
	/**
	 * Send the available files in the client. 
	 * @return a set of String where each of the String represents the file
	 *         name of a file that currently available on the client
	 */
	public static Set<String> sendFileList() {
		File dir = new File(FILE_LOCATION);
		File[] availableFiles = dir.listFiles();
		Set<String> fileNames = new HashSet<String>();
		// try to put all files in the same directory for simplicity
		for (File file: availableFiles) {
			fileNames.add(file.getName());
		}
		ConnectionUtils.sendFileList(connection, fileNames);
		return fileNames;
	}
	
	/**
	 * Get the list of files that are available for download. 
	 * @return a set of String where each of the String represents the file
	 *         name of a file that is available in the remote machine. 
	 */
	public static Set<String> getAndShowAvailableFiles() {
		Set<String> fileNames = ConnectionUtils.getFileList(connection);
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
