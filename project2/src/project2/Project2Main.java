package project2;

public class Project2Main {

	public static void main(String[] args) {
		// set up UDP connection and listen for clients.
		// When a client sends a message, spawn a new thread
		// to handle the message and verify it is valid and 
		// set up a new UDP server on a unique port. 
		UDPServerConnection serverConn = new UDPServerConnection(ConnectionUtils.INIT_UDP_PORT, false);
		while(true) {
			byte[] message = serverConn.receive(ConnectionUtils.HEADER_LENGTH + 12);
			// TODO: may want to use a ThreadPool to limit the amount of traffic
			ServerThread thread = new ServerThread(message, serverConn.getLatestAddress());
			thread.start();
		}
	}

}
