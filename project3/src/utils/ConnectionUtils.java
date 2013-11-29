package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ConnectionUtils {

	public static final int PORT = 36777;
	
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
	
}
