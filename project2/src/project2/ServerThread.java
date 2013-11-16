package project2;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * A ServerThread interacts with one client, implementing the protocols for
 * the server side.
 */
public class ServerThread extends Thread {
	
	
	private HeaderExpectation headerExpectations;
	private Random rand;

	// The initial response message contains the fields for part B 
	private byte[] initialResponse;
	
	public ServerThread(short studentNo, byte[] responseMessage) {
		this.headerExpectations = new HeaderExpectation(0, 0,(short) 0, studentNo);
		this.rand = new Random();
		this.initialResponse = responseMessage;
	}
	
	@Override
	public void run() {
		// Extract out the payload fields in the initial response
		ByteBuffer bb = ByteBuffer.wrap(initialResponse);
		int num = bb.getInt(12);
		int len = bb.getInt(16);
		int udp_port = bb.getInt(20);
		int secretA = bb.getInt(24);
		
		// Stage B
		headerExpectations.setPayload(len + 4);
		headerExpectations.setSecret(secretA);
		headerExpectations.setStepNumber((short)1);
		UDPServerConnection serverConn = new UDPServerConnection(udp_port);
		for (int i = 0; i < num; i++) {
			DatagramPacket message = serverConn.receive(
					ConnectionUtils.getAlignedLength(ConnectionUtils.HEADER_LENGTH, len + 4));
			byte[] data = message.getData();
			bb = ByteBuffer.wrap(message.getData());
			if (!checkHeader(bb) || !checkPacketLength(message.getData())) {
				System.err.println("receive error");
				serverConn.close();
			}
			// try to determine whether to send the ack packet or not
			if (rand.nextBoolean()) {
				
			}
		}
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
		return  headerExpectations.checkPayloadLength(bb.getInt(0)) &&
				headerExpectations.checkSecret(bb.getInt(4)) &&
				headerExpectations.checkStepNumber(bb.getShort(8)) &&
				headerExpectations.checkStudentNumber(bb.getShort(10));
	}
	
	/**
	 * Checks to make sure the length of the packet is 4 byte aligned
	 * @param data the received packet
	 * @return true if the packet is 4 byte aligned
	 *         false if the packet is not 4 byte aligned
	 */
	private boolean checkPacketLength(byte[] data) {
		return data.length % 4 == 0;
	}

}
