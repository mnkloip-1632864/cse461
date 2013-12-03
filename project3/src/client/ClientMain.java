package client;

import java.io.File;
import java.util.Scanner;

public class ClientMain {
	
	public static final String FILE_LOCATION="../files";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    sendListFiles();
	    getAvailableFiles();
		/*while (true) {
			String command = getCommand();
			switch (command) {
			}
		}*/
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
	public static void sendListFiles() {
		File dir = new File(FILE_LOCATION);
		File[] availableFiles = dir.listFiles();
		// try to put all files in the same directory for simplicity
		for (File file: availableFiles) {
			System.out.println("File name is: " + file.getName());
		}
	}
	
	public static void getAvailableFiles() {
		
	}

}
