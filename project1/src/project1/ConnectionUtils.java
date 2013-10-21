package project1;

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

}
