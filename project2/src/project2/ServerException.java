package project2;

public class ServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ServerException() {
		super();
	}
	
	public ServerException(String s) {
		super(s);
	}
	
	public ServerException(Throwable throwable) {
		super(throwable);
	}
	
	public ServerException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
}
