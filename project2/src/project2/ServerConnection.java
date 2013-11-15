package project2;

public interface ServerConnection {
	
	public static final String HOST="bicycle.cs.washington.edu";
	public static final int TTL = 500;

	/**
	 * Sends the given message to the server.
	 * @param message the message to send, whose length 
	 *        should be divisible by 4.
	 */
	public void send(byte[] message);
	
	/**
	 * Waits to receive a message from the server.
	 * @param bufferLength signifies the length of the buffer that we want
	 *                     to receive with.
	 * @return a byte[] representing the message that the server sent us.
	 *         If a message is not received within TTL, then null will be
	 *         returned.
	 */
	public byte[] receive(int bufferLength);

	/**
	 * Closes the connection
	 */
	public void close();
	
}

