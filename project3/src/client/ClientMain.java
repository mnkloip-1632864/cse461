package client;

import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private static boolean anyInput;

	public static void main(String[] args) {
		// Start the local FileServer
		FileServer fs = new FileServer();
		fs.start();

		// setup the Client Model
		clientModel = new ClientModel();
		setErrorOccurred(false);

		// setup the Client View
		//		clientView = new CommandLineView();
		clientView = new ClientPanel();	

		terminated = false;
		anyInput = true;

		try {
			while (!terminated) {
				clientView.unregisterFileReceiver();
				Set<String> filesAvailable = clientModel.getAvailableFiles();
				clientView.displayAvailableFiles(filesAvailable);

				// Wait for user input
				waitingForClient = true;
				clientView.tellWaiting();
				waitForUserInput();
				if(!anyInput) {
					break;
				}
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
			ClientModel.setInputFileDirectory(directory);
			clientModel.updateInputFiles();
		} catch(Exception e) {
			clientView.displayError("Error setting the input directory.");
		}
	}

	public static void updateOutputDirectory(String directory) {
		FileReceiverTask.updateOutputDirectory(directory);
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
		anyInput = false;
		notifyUserInputed();
		notifyFileTransferComplete();
	}

}
