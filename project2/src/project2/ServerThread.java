package project2;

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
		
		// Stage B
		
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
