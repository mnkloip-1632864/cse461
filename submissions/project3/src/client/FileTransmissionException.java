package client;

public class FileTransmissionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileTransmissionException() {
		super();
	}
	
	public FileTransmissionException(String s) {
		super(s);
	}
	
	public FileTransmissionException(Throwable throwable) {
		super(throwable);
	}
	
	public FileTransmissionException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
