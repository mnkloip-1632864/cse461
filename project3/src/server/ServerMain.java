package server;

import java.io.IOException;

import utils.ApplicationFields;

public class ServerMain {

	public static void main(String[] args) {
		try {
			ApplicationFields.readProperties();
		} catch (IOException e) {
			System.out.println("Properties file is missing.");
			return;
		} catch (Exception e) {
			System.out.println("Properties file is improperly formated.");
			return;
		}
		
		ConnectionPool pool = new ConnectionPool();
		pool.start();
	}

}
