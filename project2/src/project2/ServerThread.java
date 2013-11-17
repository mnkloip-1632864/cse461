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
	private int udpPort;
	private TCPServerConnection tcpServerConn;
	private int num;

	private byte c;
	
	public ServerThread(short studentNo, byte[] responseMessage) {
		// Extract out the payload fields in the response message
		ByteBuffer bb = ByteBuffer.wrap(responseMessage);
		this.num = bb.getInt(12);
		int len = bb.getInt(16);
		this.udpPort = bb.getInt(20);
		int secretA = bb.getInt(24);
		this.headerExpectations = new HeaderExpectation(len + INT_SIZE, secretA,(short)1, studentNo);
		this.rand = new Random();
		this.tcpServerConn = null;
	}
	
	@Override
	public void run() {
		try {
			// Stage B
			UDPServerConnection udpServerConn = new UDPServerConnection(udpPort);
			if(!stageB(udpServerConn)) {
				udpServerConn.close();
				return;
			}
			udpServerConn.close();
			
			// Stage C
			if(!tcpServerConn.accept()) {
				tcpServerConn.close();
				return;
			}
			// Connection established, send info
			sendStageC();
			
			// Stage D
			stageD();
			tcpServerConn.close();
			
		} finally {
			Project2Main.threadExit();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////
	//								Stage D methods									  //
	////////////////////////////////////////////////////////////////////////////////////
	
	private void stageD() {
		int expectedTotalLength = ConnectionUtils.HEADER_LENGTH + headerExpectations.getPayloadLength();
		int expectedPacketLength = ConnectionUtils.getAlignedLength(expectedTotalLength);
		int numPacketsReceived = 0;
		while(numPacketsReceived != num) {
			byte[] message = tcpServerConn.receive(expectedPacketLength);
			ByteBuffer bb = ByteBuffer.wrap(message);
			if(!checkHeader(bb) || !checkPayloadD(bb, headerExpectations.getPayloadLength())) {
				System.err.println("receive error");
				return;
			}
			numPacketsReceived++;
		}
		byte[] response = createResponseForD();
		tcpServerConn.send(response);
	}

	private boolean checkPayloadD(ByteBuffer bb, int payloadLength) {
		for(int i = 0; i < payloadLength; i++) {
			byte b = bb.get(ConnectionUtils.HEADER_LENGTH + i);
			if(b != c) {
				return false;
			}
		}
		return true;
	}
	
	private byte[] createResponseForD() {
		int secretD = rand.nextInt(20) + 1; // [1, 20]
		byte[] payload = ByteBuffer.allocate(INT_SIZE).order(ByteOrder.BIG_ENDIAN).putInt(secretD).array();
		byte[] header = ConnectionUtils.constructHeader(payload.length, headerExpectations.getSecret(), 
													   (short)2, headerExpectations.getStudentNumber());
		return ConnectionUtils.merge(header, payload);
	}

	////////////////////////////////////////////////////////////////////////////////////
	//								Stage C methods									  //
	////////////////////////////////////////////////////////////////////////////////////

	private void sendStageC() {
		int capacity = ConnectionUtils.getAlignedLength(3 * INT_SIZE + 1);
		ByteBuffer payloadBuffer = ByteBuffer.allocate(capacity).order(ByteOrder.BIG_ENDIAN);
		this.num = rand.nextInt(30) + 1; // [1, 30]
		int len = rand.nextInt(20) + 1; // [1, 20]
		int secretC = rand.nextInt(20) + 1; // [1, 20]
		this.c = (byte) (rand.nextInt(255) + 1); // [1, 255]
		byte[] payload = payloadBuffer.putInt(num)
									  .putInt(len)
									  .putInt(secretC)
									  .put(c).array();
		int totalLength = payload.length;
		byte[] header = ConnectionUtils.constructHeader(totalLength, headerExpectations.getSecret(), 
													   (short)2, headerExpectations.getStudentNumber());
		byte[] message = ConnectionUtils.merge(header, payload);
		tcpServerConn.send(message);
		
		// Set up expected values for part d
		this.headerExpectations.setPayload(len);
		this.headerExpectations.setSecret(secretC);
		this.headerExpectations.setStepNumber((short)1);
		
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	//								Stage B methods									  //
	////////////////////////////////////////////////////////////////////////////////////

	private boolean stageB(UDPServerConnection serverConn) {
		int len =  headerExpectations.getPayloadLength() - INT_SIZE;
		int expectedPacketLength = ConnectionUtils.getAlignedLength(ConnectionUtils.HEADER_LENGTH + len + INT_SIZE);
		int seqNum = 0;
		while(true) {
			DatagramPacket message = serverConn.receive(expectedPacketLength + 4);
			if(message == null) {

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
					return true;
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
		int tcpPort = ConnectionUtils.getTCPPortNumber();
		int secretB = rand.nextInt(40) + 1; // [1, 40]
		byte[] payload = payloadBuffer.putInt(tcpPort).putInt(secretB).array();
		byte[] header = ConnectionUtils.constructHeader(payload.length, headerExpectations.getSecret(), 
													   (short)2, headerExpectations.getStudentNumber());
		this.headerExpectations.setSecret(secretB);
		tcpServerConn = new TCPServerConnection(tcpPort);
		
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
