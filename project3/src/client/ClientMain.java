package client;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import utils.ApplicationFields;

public class ClientMain {

	public static final Lock lock;
	private static final Condition fileTransferNotDone;
	private static final Condition userHasntSelected;
	public static final Condition fileReceiverNotInitialized;

	static {
		lock = new ReentrantLock();
		fileTransferNotDone = lock.newCondition();
		userHasntSelected = lock.newCondition();
		fileReceiverNotInitialized = lock.newCondition();
	}

	private static ClientModel clientModel;
	private static ClientView clientView;
	private static boolean waitingForFile;
	private static boolean waitingForClient;
	private static boolean errorOccurred;
	private static boolean terminated;

	public static void main(String[] args) {
		// Setup fields from property file
		try {
			ApplicationFields.readProperties();
		} catch (IOException e) {
			System.out.println("Properties file is missing.");
			return;
		} catch (Exception e) {
			System.out.println("Properties file is improperly formated.");
			return;
		}
		
		
		// Start the local FileServer
		FileServer fs = new FileServer();
		fs.start();

		// setup the Client Model
		clientModel = new ClientModel();
		setErrorOccurred(false);

		// setup the Client View
		if(ApplicationFields.getViewType().equals(ApplicationFields.CMD)) {
			clientView = new CommandLineView();
		} else if(ApplicationFields.getViewType().equals(ApplicationFields.GUI)) {
			clientView = new ClientPanel();	
		} else {
			System.err.println("Check properties file: ViewType should be either " 
					+ ApplicationFields.CMD + " or " + ApplicationFields.GUI);
		}

		terminated = false;

		try {
			while (!terminated) {
				clientView.unregisterFileReceiver();
				Set<String> filesAvailable = clientModel.getAvailableFiles();
				if(filesAvailable.isEmpty()) {
					// no-one else is online, wait until someone is online.
					clientView.displayWaitingMessage("I'm sorry, nobody else is online. Please wait...");
					Thread.sleep(1000);
					notifyFileReceiverTask(true);
					continue;
				}
				clientView.displayAvailableFiles(filesAvailable);

				// Wait for user input
				waitingForClient = true;
				clientView.tellWaiting();
				waitForUserInput();
				String fileToGet = clientView.retrieveFilenameRequest();
				if (fileToGet == null) {
					// Client is done, terminate
					break;
				}

				try {
					clientModel.checkFileExists(fileToGet, filesAvailable);
				} catch (FileRetrievalException e) {
					clientView.displayError(e.getLocalizedMessage());
					notifyFileReceiverTask(true);
					continue;
				}

				String nodeIp = "";
				try {
					nodeIp = clientModel.getNodeWithFile(fileToGet);
					clientView.displayMessage("Retrieving: " + fileToGet + " from: " + nodeIp);
				} catch (FileRetrievalException e) {
					clientView.displayError(e.getLocalizedMessage());
					notifyFileReceiverTask(true);
					continue;
				}


				FileReceiverTask fileReceiver = new FileReceiverTask(nodeIp, fileToGet);
				waitingForFile = true;
				clientView.registerFileReceiver(fileReceiver);
				notifyFileReceiverTask(false);
				waitForCompleteFileTransfer();
				clientView.displayMessage("File transfer of " + fileToGet + " complete!\n");
			}
		} catch(Exception e) {
			clientModel.cleanup(e.getLocalizedMessage());
		} finally {
			clientModel.cleanup("Client is done");
		}
	}

	/**
	 * Waits for file transfer to be complete.
	 * When this function returns, the file will either have been 
	 * transferred or a problem will have arisen.
	 */
	public static void waitForCompleteFileTransfer() {
		lock.lock();
		try {
			while (waitingForFile) {
				try {
					fileTransferNotDone.await();
				} catch (InterruptedException e) {}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Lets anyone waiting for the file transfer to be complete
	 * know that the file transfer is complete.
	 */
	public static void notifyFileTransferComplete() {
		lock.lock();
		try {
			waitingForFile = false;
			fileTransferNotDone.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Waits for file transfer to be complete.
	 * When this function returns, the file will either have been 
	 * transferred or a problem will have arisen.
	 */
	public static void waitForUserInput() {
		lock.lock();
		try {
			while (waitingForClient) {
				try {
					userHasntSelected.await();
				} catch (InterruptedException e) {}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Lets anyone waiting for the file transfer to be complete
	 * know that the file transfer is complete.
	 */
	public static void notifyUserInputed() {
		lock.lock();
		try {
			waitingForClient = false;
			userHasntSelected.signalAll();
		} finally {
			lock.unlock();
		}
	}


	public static void notifyFileReceiverTask(boolean error) {
		lock.lock();
		try {
			setErrorOccurred(error);
			fileReceiverNotInitialized.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets a FileMapping for the client.
	 */
	public static FileMapping getFileMapping() {
		return clientModel.getFileMapping();
	}

	public static void reportError(String message) {
		clientView.displayError(message);
	}

	public static void updateInputDirectory(String directory) {
		try {
			ApplicationFields.setInputDirectory(directory);
			clientModel.updateInputFiles();
		} catch(Exception e) {
			clientView.displayError("Error setting the input directory.");
		}
	}

	public static void updateOutputDirectory(String directory) {
		ApplicationFields.setOutputDirectory(directory);
	}

	public static boolean hasErrorOccurred() {
		return errorOccurred;
	}

	public static void setErrorOccurred(boolean errorOccurred) {
		ClientMain.errorOccurred = errorOccurred;
	}

	public static void terminate() {
		terminated = true;
		clientModel.cleanup("Client quit application.");
		System.exit(0);
	}

}
