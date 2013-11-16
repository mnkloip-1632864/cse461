package project2;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Project2Main {

	public static final String HELLO = "hello world\0";
	private static final int THREAD_POOL_SIZE = 30;
	private static AtomicInteger count;
	
	static {
		count = new AtomicInteger(0);
	}
	
	public static void main(String[] args) {
		// set up UDP connection and listen for clients.
		// When a client sends a message, spawn a new thread
		// to handle the message and verify it is valid and 
		// set up a new UDP server on a unique port. 
		UDPServerConnection serverConn = new UDPServerConnection(ConnectionUtils.INIT_UDP_PORT);
		serverConn.disableTimeout();
		while(true) {
			DatagramPacket message = serverConn.receive(ConnectionUtils.HEADER_LENGTH + 12);
			// Verify the message is valid and send a response.
			if(!verifyInitialMessage(message.getData())) {
				continue;
			}
			short studentNo = getStudentNumber(message.getData());
			byte[] response = createResponse(studentNo);
			serverConn.send(response, message.getAddress(), message.getPort());
			// TODO: may want to use a ThreadPool to limit the amount of traffic
			ServerThread thread = new ServerThread(studentNo, response);
			thread.start();
			count.incrementAndGet();	
		}
		
	}

	public static void threadExit() {
		count.decrementAndGet();
	}
	
	/**
	 * Creates a response to a client connecting to the server. The response will contain the
	 * number of messages to send, their length, what port to send the messages to, and a 
	 * secret number.
	 * @param studentNo the student number sent with the client's message.
	 * @return A byte array containing the message to be sent to the client.
	 */
	private static byte[] createResponse(short studentNo) {
		Random rand = new Random();
		ByteBuffer payloadBuffer = ByteBuffer.allocate(4 * 4).order(ByteOrder.BIG_ENDIAN);
		int num = rand.nextInt(30) + 1; // [1, 30]
		int len = rand.nextInt(20) + 1; // [1, 20]
		int port = ConnectionUtils.getUDPPortNumber();
		int secretA = rand.nextInt(20) + 1; // [1, 20]
		byte[] payload = payloadBuffer.putInt(num)
									  .putInt(len)
									  .putInt(port)
									  .putInt(secretA).array();
		int totalLength = payload.length;
		byte[] header = ConnectionUtils.constructHeader(totalLength, 0, (short)1, studentNo);
		return ConnectionUtils.merge(header, payload);
	}
	/**
	 * Retrieves the student number used in the header of the given message. 
	 * @param data the message that contains the student number.
	 * @return the student number extracted from 'data'
	 */
	private static short getStudentNumber(byte[] data) {
		return ByteBuffer.wrap(data).getShort(10);
	}

	////////////////////////////////////////////////////////////////
	// Functions to verify the first message received by a client //
	////////////////////////////////////////////////////////////////
	
	private static boolean verifyInitialMessage(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		return checkInitialHeader(bb) && checkInitialPayload(bb);
	}
	
	private static boolean checkInitialHeader(ByteBuffer bb) {
		HeaderExpectation exp = HeaderExpectation.INITIAL;
		return  exp.checkPayloadLength(bb.getInt(0)) &&
				exp.checkSecret(bb.getInt(4)) &&
				exp.checkStepNumber(bb.getShort(8));
	}

	private static boolean checkInitialPayload(ByteBuffer bb) {
		byte[] bytes = bb.array();
		byte[] helloBytes = HELLO.getBytes();
		for(int i = 0; i < HELLO.length(); i++) {
			byte bufferByte = bytes[i + ConnectionUtils.HEADER_LENGTH];
			if(bufferByte != helloBytes[i]) {
				return false;
			}
		}
		return true;
	}
}
