package project2;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
 * A ServerThread interacts with one client, implementing the protocols for
 * the server side.
 */
public class ServerThread extends Thread {
	
	private static final int INT_SIZE = 4;
	
	private HeaderExpectation headerExpectations;
	private Random rand;
	private int tcpPort;

	// The initial response message contains the fields for part B 
	private byte[] initialResponse;
	
	public ServerThread(short studentNo, byte[] responseMessage) {
		this.headerExpectations = new HeaderExpectation(0, 0,(short) 0, studentNo);
		this.rand = new Random();
		this.tcpPort = 0;
		this.initialResponse = responseMessage;
	}
	
	@Override
	public void run() {
		try {
			// Extract out the payload fields in the initial response
			ByteBuffer bb = ByteBuffer.wrap(initialResponse);
			int num = bb.getInt(12);
			int len = bb.getInt(16);
			int udp_port = bb.getInt(20);
			int secretA = bb.getInt(24);
			
			// Stage B
			headerExpectations.setPayload(len + INT_SIZE);
			headerExpectations.setSecret(secretA);
			headerExpectations.setStepNumber((short)1);
			UDPServerConnection serverConn = new UDPServerConnection(udp_port);
			if(!stageB(serverConn, len, num)) {
				serverConn.close();
				return;
			}
			
			// Stage C
			
			
		} finally {
			Project2Main.threadExit();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////
	//								Stage B methods									  //
	////////////////////////////////////////////////////////////////////////////////////
	
	private boolean stageB(UDPServerConnection serverConn, int len, int num) {
		int expectedPacketLength = ConnectionUtils.getAlignedLength(ConnectionUtils.HEADER_LENGTH + len + INT_SIZE);
		int seqNum = 0;
		while(true) {
			DatagramPacket message = serverConn.receive(expectedPacketLength + 4);
			if(message == null) {
				if (seqNum == num) {
					// The client is rightfully done sending messages
					return true;
				}

				System.err.println("Timeout waiting for part B packet.");
				return false;
			}
			if(!checkMessageLength(message, expectedPacketLength)) {
				System.err.println("Length is incorrect.");
				return false;
			}
			InetAddress clientAddr = message.getAddress();
			int port = message.getPort();
			byte[] data = message.getData();
			ByteBuffer bb = ByteBuffer.wrap(data);
			if (!checkHeader(bb) || !checkPayloadB(bb, len, seqNum)) {
				System.err.println("receive error");
				return false;
			}
			if (rand.nextBoolean()) {
				// send the ack packet
				int sNum = bb.getInt(ConnectionUtils.HEADER_LENGTH);
				byte[] ack = createAckForB(sNum);
				serverConn.send(ack, clientAddr, port);
				if(sNum == seqNum) {
					seqNum++;
				} // else the packet's seqNum < 'seqNum'
				if (seqNum == num) {
					// send ending packet
					byte[] response = createResponseForB();
					serverConn.send(response, clientAddr, port);
				}
			}
		}
	}
	
	/**
	 * Checks to make sure that the payload of the byte buffer has the appropriate
	 * length of 0's and sequence number
	 * @param bb     the client's message
	 * @param len    the expected number of 0's in the payload
	 * @param seqNum the expected sequence number in the payload
	 * @return true if the payload is formatted correctly. False otherwise.
	 */
	private boolean checkPayloadB(ByteBuffer bb, int len, int seqNum) {
		int sNum = bb.getInt(ConnectionUtils.HEADER_LENGTH);
		if(sNum > seqNum) {
			return false;
		}
		for(int i = 0; i < len; i++) {
			if(bb.get(ConnectionUtils.HEADER_LENGTH + INT_SIZE + i) != (byte)0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates an acknowledgment packet for stage B
	 * @param seqNum the sequence number of the packet this acknowledgment is meant for.
	 * @return the byte[] containing the header and payload of the packet.
	 */
	private byte[] createAckForB(int seqNum) {
		byte[] payload = ByteBuffer.allocate(INT_SIZE).order(ByteOrder.BIG_ENDIAN).putInt(seqNum).array();
		byte[] header = ConnectionUtils.constructHeader(payload.length, headerExpectations.getSecret(), 
				(short)1, headerExpectations.getStudentNumber());
		return ConnectionUtils.merge(header, payload);
	}

	/**
	 * @return a response packet for stage B that is sent after all packets are sent.
	 */
	private byte[] createResponseForB() {
		ByteBuffer payloadBuffer = ByteBuffer.allocate(2 * INT_SIZE).order(ByteOrder.BIG_ENDIAN);
		this.tcpPort = ConnectionUtils.getTCPPortNumber();
		int secretB = rand.nextInt(40) + 1; // [1, 40]
		byte[] payload = payloadBuffer.putInt(this.tcpPort).putInt(secretB).array();
		byte[] header = ConnectionUtils.constructHeader(payload.length, headerExpectations.getSecret(), 
													   (short)2, headerExpectations.getStudentNumber());
		this.headerExpectations.setSecret(secretB);
		return ConnectionUtils.merge(header, payload);
	}

	////////////////////////////////////////////////////////////////////////////////////
	//							General purpose methods								  //
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * checks to make sure that the length of the message received by the server is
	 * the length that it expects
	 * @param message			   the message returned from the client
	 * @param expectedPacketLength the length expected by the server (needs to be 4 byte aligned)
	 * @return true if the length of the message is the same as the expected length
	 * 		   false otherwise.
	 */
	private boolean checkMessageLength(DatagramPacket message,
			int expectedPacketLength) {
		return message.getLength() == expectedPacketLength;
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

}
