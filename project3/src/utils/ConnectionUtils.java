package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

public class ConnectionUtils {

	public static final int PORT = 36777;
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
		return null;
	}
	
}
