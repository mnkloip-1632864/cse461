package simple;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;

import project1.ConnectionUtils;

public class BasicClient {

	private static final String HOST = "192.168.0.2";
	
	public static void main(String[] args) throws Exception {
		
		Random r = new Random();
		System.out.println((byte) r.nextInt(256));
		
		
		/*DatagramSocket ds = new DatagramSocket();
		ds.connect(InetAddress.getByName(HOST),	ConnectionUtils.INIT_UDP_PORT);
		byte[] payload = "hello world\0".getBytes();
		byte[] header = ConnectionUtils.constructHeader(payload.length, 0, (short)1);
		byte[] buf = ConnectionUtils.merge(header, payload);
		System.out.println(Arrays.toString(buf));
		DatagramPacket pkt = new DatagramPacket(buf, buf.length);
		ds.send(pkt);
		
		ds.close();*/
	}

}
