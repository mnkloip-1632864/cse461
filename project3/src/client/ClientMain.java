package client;

import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientMain {

	private static final Lock lock;
	private static final Condition fileTransferNotDone;
	
	static {
		lock = new ReentrantLock();
		fileTransferNotDone = lock.newCondition();
	}
	
	private static ClientModel clientModel;
	private static ClientView clientView;
	private static ClientPanel clientPanel;
	private static boolean waitingForFile;

	public static void main(String[] args) {
		// Start the local FileServer
		FileServer fs = new FileServer();
		fs.start();

		// setup the Client Model
		clientModel = new ClientModel();
		
		// setup the Client View
		clientView = new CommandLineView();		
		try {
			Set<String> filesAvailable = clientModel.getAvailableFiles();
			while (true) {
				clientView.displayAvailableFiles(filesAvailable);
				String fileToGet = clientView.retrieveFilenameRequest();
				if (fileToGet == null) {
					// Client is done, terminate
					break;
				}

				try {
					clientModel.checkFileExists(fileToGet, filesAvailable);
				} catch (FileRetrievalException e) {
					clientView.displayError(e.getLocalizedMessage());
					continue;
				}
				
				String nodeIp = "";
				try {
					nodeIp = clientModel.getNodeWithFile(fileToGet);
					clientView.displayMessage("Retrieving: " + fileToGet + " from: " + nodeIp);
				} catch (FileRetrievalException e) {
					clientView.displayError(e.getLocalizedMessage());
					continue;
				}
				
				
				FileReceiverTask fileReceiver = new FileReceiverTask(nodeIp, fileToGet);
				waitingForFile = true;
				clientView.registerFileReceiver(fileReceiver);
				waitForCompleteFileTransfer();
				clientView.displayMessage("File transfer of " + fileToGet + " complete!\n");
			}
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
	 * Gets a FileMapping for the client.
	 */
	public static FileMapping getFileMapping() {
		return clientModel.getFileMapping();
	}

	public static void reportError(String message) {
		clientView.displayError(message);
	}

}
