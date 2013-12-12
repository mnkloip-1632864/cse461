package client;

import java.util.Scanner;
import java.util.Set;

public class CommandLineView implements ClientView {

	private static final int NAMES_PER_LINE = 3;
	
	private Scanner input;
	
	public CommandLineView() {
		input = new Scanner(System.in);
	}
	
	@Override
	public void displayAvailableFiles(Set<String> fileNames) {
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
	}

	@Override
	public String retrieveFilenameRequest() {
		System.out.print("Please type in the name of the file you want " +
				"to get (Press Ctrl-D to quit the program): ");
		if(input.hasNextLine()) {
			String fileToGet = input.nextLine();
			return fileToGet;
		}
		return null;
	}

	@Override
	public void displayError(String error) {
		System.err.println(error);
	}

	@Override
	public void displayMessage(String message) {
		System.out.println(message);
	}
	
	@Override
	public void displayWaitingMessage(String message) {
		System.out.println(message);		
	}

	@Override
	public void registerFileReceiver(FileReceiverTask fileReceiver) {
		fileReceiver.execute();
	}
	
	@Override
	public void unregisterFileReceiver() {		
	}

	@Override
	public void tellWaiting() {
		ClientMain.notifyUserInputed();
	}

}
