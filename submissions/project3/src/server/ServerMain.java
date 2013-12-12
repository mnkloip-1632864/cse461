package server;

import utils.ApplicationFields;

public class ServerMain {

	public static void main(String[] args) {
		if(!ApplicationFields.setUpApplicationUtils()) {
			return;
		}
		
		ConnectionPool pool = new ConnectionPool();
		pool.start();
	}

}
