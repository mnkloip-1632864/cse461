package project1;

import java.nio.ByteBuffer;

/**
 * Utility class to allow the application to send and receive from the server.
 */
public class ConnectionUtils {
	public static final String HOST="bicycle.cs.washington.edu";
	public static final int INIT_UDP_PORT = 12235;
	public static final int HEADER_LENGTH = 12;
	public static final int STUDENT_NUM = 29;
	
	// only one question remain is how to represent
	// uint32_t in java. 
	public static int getPacketLength(int realLength) {
		int res = realLength / 4;
		if (realLength % 4 != 0)
			return 4*(res + 1);
		else
			return realLength;
	}
	
	/**
	 * merges 'first' and 'second' into one byte[] that has a length divisible by 4
	 * @param first the first array in the message
	 * @param second the next array in the message
	 * @return a byte[] that contains second concatenated to first followed by 0s 
	 *         to make the total length divisible by 4.
	 */
	public static byte[] merge(byte[] first, byte[] second) {
		int length = first.length + second.length;
		int padLen = 4 - length % 4;
		length += padLen == 4 ? 0 : padLen;
		byte[] combo = ByteBuffer.allocate(length).put(first).put(second).array();
		return combo;
	}
	
	public static int getProtocolNumber() {
		return 0;
	}

}
