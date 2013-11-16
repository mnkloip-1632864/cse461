package project2;

/**
 * Provides the values that the server expects in the headers.
 * This involves the length of the payload, the secret in the payload,
 * the step number, and the student number.
 */
public class HeaderExpectation {
	
	public static final HeaderExpectation INITIAL; 
	
	static {
		INITIAL = new HeaderExpectation(Project2Main.HELLO.length(), 0,(short) 1,(short) -1);
	}
	
	private int payload;
	private int secret;
	private short stepNumber;
	private short studentNumber;
	
	
	public HeaderExpectation(int payload, int secret, short stepNumber, short studentNumber) {
		this.payload = payload;
		this.secret = secret;
		this.stepNumber = stepNumber;
		this.studentNumber = studentNumber;
	}

	public boolean checkPayloadLength(int clientLength) {
		return payload == clientLength;
	}

	public void setPayload(int payload) {
		this.payload = payload;
	}
	
	public boolean checkSecret(int clientSecret) {
		return secret == clientSecret;
	}
	
	public void setSecret(int secret) {
		this.secret = secret;
	}

	public boolean checkStepNumber(short clientStepNumber) {
		return stepNumber == clientStepNumber;
	}

	public void setStepNumber(short stepNumber) {
		this.stepNumber = stepNumber;
	}
	
	public boolean checkStudentNumber(short clientStudentNumber) {
		return studentNumber == clientStudentNumber;
	}
	
	public void setStudentNumber(short studentNumber) {
		this.studentNumber = studentNumber;
	}
}
