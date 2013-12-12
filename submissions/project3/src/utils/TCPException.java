package utils;

public class TCPException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public TCPException() {
		super();
	}
	
	public TCPException(String s) {
		super(s);
	}
	
	public TCPException(Throwable throwable) {
		super(throwable);
	}
	
	public TCPException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
}
