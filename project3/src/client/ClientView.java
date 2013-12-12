package client;

import java.util.Set;

/**
 * Serves the purpose of a view for the client to the user.
 */
public interface ClientView {
	
	/**
	 * Display the set of filenames as the list of available files on the server.
	 */
	public void displayAvailableFiles(Set<String> fileNames);
	
	/**
	 * Get a file from the user. May block for some time.
	 */
	public String retrieveFilenameRequest();
	
	/**
	 * Displays the given error message on the view.
	 */
	public void displayError(String error);

	/**
	 * Displays a message to the user.
	 */
	public void displayMessage(String message);

	/**
	 * Registers the file receiver with this view, to execute in a
	 * separate thread.
	 */
	public void registerFileReceiver(FileReceiverTask fileReceiver);

	/**
	 * Unregisters the currently registered file receiver.
	 */
	public void unregisterFileReceiver();
	
	/**
	 * Tells the view that the main thread is going to wait for user input. 
	 */
	public void tellWaiting();
}
