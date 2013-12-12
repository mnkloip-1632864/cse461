package client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.SwingWorker;

import utils.ApplicationFields;
import utils.ConnectionUtils;
import utils.HeaderException;
import utils.MessageType;
import utils.TCPConnection;
import utils.TCPException;

public class FileReceiverTask extends SwingWorker<Void, Void>{
	
	private String nodeIp;
	private String fileToGet;
	private TCPConnection connectionToPeer;

	
	public FileReceiverTask(String nodeIp, String fileToGet) {
		this.nodeIp = nodeIp;
		this.fileToGet = fileToGet;
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			setUpConnection(nodeIp);

			sendFileName(fileToGet);

			byte[] fileMeta = getFileMeta();
			if (fileMeta == null) {
				closeClient();
			}
			ByteBuffer data = ByteBuffer.wrap(fileMeta);

			long fileSize = data.getLong(0);

			// retrieve the file and store it to disk
			try {
				retrieveAndStoreFile(fileSize, fileToGet);
			} catch (FileRetrievalException e) {
				// problem with file retrieval
				ClientMain.reportError("File transfer failed:\n" + e.getLocalizedMessage());
			}
			
		} finally {
			ClientMain.notifyFileTransferComplete();			
		}
		return null;
	}
	
	private void setUpConnection(String nodeIp) {
		connectionToPeer = new TCPConnection(nodeIp, ApplicationFields.fileServerPort);
	}

	private void retrieveAndStoreFile(long fileSize, String fileToGet) throws FileRetrievalException {
		BufferedOutputStream bufferedOut = null;
		try {
			FileOutputStream out = new FileOutputStream(ApplicationFields.getOutputDirectory() + File.separator + fileToGet);
			bufferedOut = new BufferedOutputStream(out);

			long numBytesReceived = 0;
			while(numBytesReceived < fileSize) {
				long numBytesLeft = fileSize - numBytesReceived;
				int size = numBytesLeft > ApplicationFields.chunkSize ? ApplicationFields.chunkSize : (int) numBytesLeft;
				byte[] chunk = new byte[size];
				int count = connectionToPeer.receiveAsync(chunk);
				bufferedOut.write(chunk, 0, count);
				numBytesReceived += count;
				int progress = (int)((double) numBytesReceived / ((double)fileSize) * 100.0);
				setProgress(progress);
			}
		} catch (FileNotFoundException e) {
			throw new FileRetrievalException("Output directory could not be found.");
		} catch (IOException e) {
			throw new FileRetrievalException("I/O Exception occurred.");
		} catch (TCPException e) {
			throw new FileRetrievalException("Problem connecting to the source.");
		} catch (Exception e) {
			throw new FileRetrievalException("Problem connecting to the source.");
		} finally {
			if (bufferedOut != null) {
				try {
					bufferedOut.flush();
					bufferedOut.close();
				} catch (IOException e) {}
			}
		}
	}

	private byte[] getFileMeta() {
		byte[] header = connectionToPeer.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		ConnectionUtils.checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		byte[] message = connectionToPeer.receive(payloadLen);

		if (type == MessageType.TERMINATE) {
			System.out.println("Transmission terminated: " + new String(message));
		} else if (type == MessageType.FILE_META) {
			if (payloadLen != 16) {
				throw new HeaderException("Wrong payload length!");
			}
			return message;
		} else {
			System.out.println("Error in retrieving file meta data!");
		}
		return null;
	}

	/**
	 * Send the file name to the source
	 * @param fileToGet
	 */
	private void sendFileName(String fileToGet) {
		byte[] fileName = fileToGet.getBytes();
		byte[] header = ConnectionUtils.constructHeader(fileName.length, MessageType.REQUEST);
		byte[] message = ConnectionUtils.merge(header, fileName);
		connectionToPeer.send(message);
	}
	
	private void closeClient() {
		connectionToPeer.close();
	}
	
}
