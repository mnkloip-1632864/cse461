package client;

import java.util.Scanner;

public class ClientMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		setup();
		while (true) {
			String commend = getCommend();
			switch (commend) {
			}
		}
	}
	
	public static String getCommend() {
		Scanner s = new Scanner(System.in);
		return s.next();
	}
	
	/**
	 * 
	 */
	public static void setup() {
		
	}

}
