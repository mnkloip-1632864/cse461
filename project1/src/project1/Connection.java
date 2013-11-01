package project1;

public interface Connection {
	
	public static final String HOST="bicycle.cs.washington.edu";

	/**
	 * Sends the given message to the server.
	 * @param message the message to send, whose length 
	 *        should be divisible by 4.
	 */
	public void send(byte[] message);
	
	/**
	 * Waits to receive a message from the server.
	 * @return a byte[] representing the message that the server sent us.
	 */
	public byte[] receive();

}

