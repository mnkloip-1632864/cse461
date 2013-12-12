package client;

public class FileRetrievalException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileRetrievalException() {
		super();
	}
	
	public FileRetrievalException(String s) {
		super(s);
	}
	
	public FileRetrievalException(Throwable throwable) {
		super(throwable);
	}
	
	public FileRetrievalException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
