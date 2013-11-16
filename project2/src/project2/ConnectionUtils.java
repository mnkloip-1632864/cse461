package project2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to allow the application to send and receive from the server.
 */
public class ConnectionUtils {
	public static final int INIT_UDP_PORT = 12235;
	public static final int HEADER_LENGTH = 12;
	public static final int TTL = 3000;
	
	private static AtomicInteger udpPort;
	private static AtomicInteger tcpPort;
	
	static {
		udpPort = new AtomicInteger(INIT_UDP_PORT + 1);
		tcpPort = new AtomicInteger(12345);
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
	
	/**
	 * Constructs the header for a message.
	 * @param payload_len the length of the payload of the message
	 * @param psecret the secret value for the previous stage
	 * @param step the current step number this message is being sent for
	 * @param studentNo the student number to use 
	 * @return a byte[] that contains the header for the packet to be sent.
	 */
	public static byte[] constructHeader(int payload_len, int psecret, short step, short studentNo) {
		ByteBuffer b = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN);
		b.putInt(payload_len);
		b.putInt(psecret);
		b.putShort(step);
		b.putShort(studentNo);
		return b.array();
	}
	
	/**
	 * @return a unique UDP port number for the server to use.
	 */
	public static int getUDPPortNumber() {
		return udpPort.getAndIncrement();
	}
	
	/**
	 * @return a unique TCP port number for the server to use.
	 */
	public static int getTCPPortNumber() {
		return tcpPort.getAndIncrement();
	}

}
