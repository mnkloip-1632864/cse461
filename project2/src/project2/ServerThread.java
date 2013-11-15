package project2;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ServerThread interacts with one client, implementing the protocols for
 * the server side.
 */
public class ServerThread extends Thread {

	private static final String HELLO = "hello world\0";
	
	private static AtomicInteger udpPort;
	private static AtomicInteger tcpPort;
	
	static {
		udpPort = new AtomicInteger(ConnectionUtils.INIT_UDP_PORT + 1);
		tcpPort = new AtomicInteger(12345);
	}
	
	private byte[] initialMessage;
	private InetAddress clientAddress;
	private int expectedLength;
	private int expectedSecret;
	private short expectedStep;
	private short expectedStudentNo;
	private Random rand;
	
	public ServerThread(byte[] initialMessage, InetAddress clientAddress) {
		this.initialMessage = initialMessage;
		this.clientAddress = clientAddress;
		this.expectedLength = HELLO.length();
		this.expectedSecret = 0;
		this.expectedStep = 1;
		this.expectedStudentNo = -1;
		this.rand = new Random();
	}
	
	@Override
	public void run() {
		// Stage A
		ByteBuffer bb = ByteBuffer.wrap(initialMessage);
		if(!(checkHeader(bb) && checkPayloadA(bb))) {
			return;
		}
		ByteBuffer payloadBuffer = ByteBuffer.allocate(4 * 4).order(ByteOrder.BIG_ENDIAN);
		int num = rand.nextInt(30) + 1; // [1, 30]
		int len = rand.nextInt(20) + 1; // [1, 20]
		int port = udpPort.getAndIncrement();
		int secretA = rand.nextInt(20) + 1; // [1, 20]
		byte[] payload = payloadBuffer.putInt(num)
									  .putInt(len)
									  .putInt(port)
									  .putInt(secretA).array();
		int totalLength = payload.length;
		byte[] header = ConnectionUtils.constructHeader(totalLength, 0, (short)1, expectedStudentNo);
		byte[] message = ConnectionUtils.merge(header, payload);
		UDPServerConnection udpConn = new UDPServerConnection(port, true);
		udpConn.send(message, clientAddress);
		
	}

	/**
	 * Checks to make sure that the header of the ByteBuffer is valid.
	 * It is valid if the contents consist of the expected payload length
	 * followed by the expected secret, the expected step and the 
	 * expected studentNumber.
	 * @param bb the ByteBuffer containing the header to be checked.
	 * @return true if the header is correctly formatted and contains the
	 * 		   expected field values.
	 * 		   false is returned if anything is incorrect or unexpected.
	 */
	private boolean checkHeader(ByteBuffer bb) {
		if(bb.getInt(0) != expectedLength) {
			return false;
		}
		if(bb.getInt(4) != expectedSecret) {
			return false;
		}
		if(bb.getShort(8) != expectedStep) {
			return false;
		}
		short studentNo = bb.getShort(10);
		if(expectedStudentNo == -1) {
			expectedStudentNo = studentNo;
		} else if(studentNo != expectedStudentNo) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks to make sure that the bb contains HELLO in the payload
	 * section of the message.
	 * @param bb the bytebuffer containing a message from a client at
	 * 			 stage A
	 * @return true if HELLO is in the payload, false if it is not.
	 */
	private boolean checkPayloadA(ByteBuffer bb) {
		for(int i = 0; i < HELLO.length(); i++) {
			if(bb.getChar(i + ConnectionUtils.HEADER_LENGTH) != HELLO.charAt(i)) {
				return false;
			}
		}
		return true;
	}

}
