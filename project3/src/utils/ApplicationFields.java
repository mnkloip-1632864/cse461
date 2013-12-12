package utils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ApplicationFields {
	
	private static final String propertyFile = "fields.props";
	public static final String GUI = "GUI";
	public static final String CMD = "CMD";
	
	public static int serverPort = 36777;
	public static int fileServerPort = 36877;
	
	public static int chunkSize = 10000;
	
	private static String serverAddress = "192.168.0.2";
	private static String inputDirectory = ".." + File.separator + "inputFiles";
	private static String outputDirectory = ".." + File.separator + "receivedFiles";
	private static String viewType = GUI;
	
	public static String getServerAddress() {
		return serverAddress;
	}

	public static void setServerAddress(String serverAddress) {
		ApplicationFields.serverAddress = serverAddress;
	}
	
	public static String getInputDirectory() {
		return inputDirectory;
	}
	
	public static void setInputDirectory(String path) {
		inputDirectory = path;
	}
	
	public static String getOutputDirectory() {
		return outputDirectory;
	}
	
	public static void setOutputDirectory(String outputDir) {
		ApplicationFields.outputDirectory = outputDir;
	}
	
	public static String getViewType() {
		return viewType;
	}
	
	public static void setViewType(String viewType) {
		ApplicationFields.viewType = viewType;
	}

	private static void readProperties() throws IOException {
		File f = new File(propertyFile);
		Scanner fileScanner = new Scanner(f);
		Scanner lineScanner = new Scanner(fileScanner.nextLine());
		lineScanner.next();
		serverAddress = lineScanner.next();
		lineScanner.close();

		lineScanner = new Scanner(fileScanner.nextLine());
		lineScanner.next();
		serverPort = lineScanner.nextInt();
		lineScanner.close();
		
		lineScanner = new Scanner(fileScanner.nextLine());
		lineScanner.next();
		fileServerPort = lineScanner.nextInt();
		lineScanner.close();

		lineScanner = new Scanner(fileScanner.nextLine());
		lineScanner.next();
		chunkSize = lineScanner.nextInt();
		lineScanner.close();
		
		lineScanner = new Scanner(fileScanner.nextLine());
		lineScanner.next();
		setViewType(lineScanner.next());
		lineScanner.close();
		
		if(fileScanner.hasNextLine()) {
			lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.next();
			inputDirectory = lineScanner.next();
			lineScanner.close();
		}
		
		if(fileScanner.hasNextLine()) {
			lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.next();
			outputDirectory = lineScanner.next();
			lineScanner.close();
		}
		lineScanner.close();
		fileScanner.close();
		
	}
	
	/**
	 * Tries to setup the fields in ApplicationFields, returns false if something bad happened.
	 */
	public static boolean setUpApplicationUtils() {
		try {
			readProperties();
		} catch (IOException e) {
			System.out.println("Properties file is missing.");
			return false;
		} catch (Exception e) {
			System.out.println("Properties file is improperly formated.");
			return false;
		}
		return true;
	}
}
