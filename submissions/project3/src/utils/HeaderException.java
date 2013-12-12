package utils;

public class HeaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public HeaderException() {
		super();
	}
	
	public HeaderException(String s) {
		super(s);
	}
	
	public HeaderException(Throwable throwable) {
		super(throwable);
	}
	
	public HeaderException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
}
