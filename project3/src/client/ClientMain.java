package client;

import java.io.File;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utils.ConnectionUtils;
import utils.MessageType;
import utils.TCPConnection;

public class ClientMain {
	
	private static final String FILE_LOCATION = "../files";
	private static final String SERVER_ADDR = "";
	private static final int NAMES_PER_LINE = 3;
	private static TCPConnection connection;

	public static void main(String[] args) {
		connection = new TCPConnection(SERVER_ADDR, ConnectionUtils.SERVER_PORT);
	    sendFileList();
	    getAndShowAvailableFiles();
		while (true) {
			String command = getCommand();
			switch (command) {
			
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getCommand() {
		Scanner s = new Scanner(System.in);
		return s.next();
	}
	
	/**
	 * Send the available files in the client. 
	 */
	public static void sendFileList() {
		File dir = new File(FILE_LOCATION);
		File[] availableFiles = dir.listFiles();
		Set<String> fileNames = new HashSet<String>();
		// try to put all files in the same directory for simplicity
		for (File file: availableFiles) {
			fileNames.add(file.getName());
		}
		ConnectionUtils.sendFileList(connection, fileNames);
	}
	
	/**
	 * 
	 */
	public static void getAndShowAvailableFiles() {
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
	}

}
