package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

public class ConnectionUtils {

	public static final int MAGIC = 0xCAFEF00D;
	public static final int HEADER_SIZE = 9;
	
	/**
	 * merges 'first' and 'second' into one byte[] that has a length divisible by 4
	 * @param first the first array in the message
	 * @param second the next array in the message
	 * @return a byte[] that contains second concatenated to first followed by 0s 
	 *         to make the total length divisible by 4.
	 */
	public static byte[] merge(byte[] first, byte[] second) {
		int length = first.length + second.length;
		byte[] combo = ByteBuffer.allocate(length).put(first).put(second).array();
		return combo;
	}
	
	/**
	 * Constructs the header for a message.
	 * The header consists of the magic number at the beginning, followed by the
	 * length of the payload, ending with the type of contents in the payload.
	 * @param payload_len the length of the payload of the message
	 * @param type one of the MessageTypes constants containing the type of payload.
	 * @return a byte[] that contains the header for the packet to be sent.
	 */
	public static byte[] constructHeader(int payload_len, byte type) {
		ByteBuffer b = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.BIG_ENDIAN);
		b.putInt(MAGIC);
		b.putInt(payload_len);
		b.put(type);
		return b.array();
	}
	
	/**
	 * Retrieves Strings representing fileNames from the array of bytes
	 * @param bytes the array of bytes containing filenames separated by '\0'
	 * 			    characters. 
	 * @return a Set containing all of the filenames embedded in the byte array.
	 */
	public static Set<String> getFileNames(byte[] bytes) {
		Set<String> files = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			if(b == 0) {
				files.add(sb.toString());
				sb = new StringBuilder();
			} else {
				sb.append((char)b);
			}
		}
		return files;
	}
	
	/**
	 * Construct a byte array containing the Strings in fileNames
	 * @param fileNames a Set of Strings to be put into the byte array.
	 * @return a '\0' separated array of bytes representing the fileNames.
	 */
	public static byte[] makeFileBytes(Set<String> fileNames) {
		StringBuilder sb = new StringBuilder();
		for (String fileName : fileNames) {
			sb.append(fileName + "\0");
		}
		return sb.toString().getBytes();
	}
	
	public static Set<String> getFileList (TCPConnection connection) {
		// get the header to find out how big the list is.
		byte[] header = connection.receive(ConnectionUtils.HEADER_SIZE);
		ByteBuffer buf = ByteBuffer.wrap(header);
		checkMagic(buf);
		int payloadLen = buf.getInt(4);
		byte type = buf.get(8);
		if(type != MessageType.LIST) {
			throw new HeaderException("Need to send a list of elements to the Server first.");
		}
		// get the list
		byte[] list = connection.receive(payloadLen);
		return getFileNames(list);
	}
	
	public static void sendFileList(TCPConnection connection, Set<String> fileNames) {
		byte[] files = ConnectionUtils.makeFileBytes(fileNames);
		byte[] header = ConnectionUtils.constructHeader(files.length, MessageType.LIST);
		byte[] message = ConnectionUtils.merge(header, files);		
		connection.send(message);
	}
	
	/**
	 * Constructs a termination message with the given message.
	 * @param message the message to send along with the termination.
	 * 		  Can be an error message, or some sort of debugging message.
	 * @return a byte[] that contains the header and the message.
	 */
	public static byte[] constructTerminateMessage(String message) {
		byte[] payload = message.getBytes();
		byte[] header = constructHeader(payload.length, MessageType.TERMINATE);
		return merge(header, payload);
	}
	
	public static void checkMagic(ByteBuffer buf) {
		int magic = buf.getInt(0);
		if(magic != MAGIC) {
			throw new HeaderException("Magic number not correct. Expected: " + MAGIC + ", but received: " + magic);
		}
	}
	
}
