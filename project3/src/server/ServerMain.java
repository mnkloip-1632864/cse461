package server;

public class ServerMain {

	public static void main(String[] args) {
		ConnectionPool pool = new ConnectionPool();
		pool.start();
	}

}
