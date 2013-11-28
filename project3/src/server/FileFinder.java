package server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The FileFinder provides a way for clients to find nodes that have a 
 * particular file to download.
 */
public class FileFinder {

	// There is only one instance of the FileFinder
	private static volatile FileFinder INSTANCE = null;
	
	private Map<String, List<InetAddress>> files; // Filename -> hosts
	private Map<InetAddress, List<String>> hosts; // Host -> filenames
	
	private FileFinder() {
		files = new HashMap<String, List<InetAddress>>();
		hosts = new HashMap<InetAddress, List<String>>();
	}
	
	public static FileFinder getInstance() {
		if(INSTANCE == null) {
			synchronized(FileFinder.class) {
				if(INSTANCE == null) {
					INSTANCE = new FileFinder();
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * Returns a host address that contains fileName. Returns null if
	 * the filename is not found.
	 */
	public synchronized InetAddress getAddressFor(String fileName) {
		//TODO: implement
		return null;
	}
	
	public synchronized void removeAddress(InetAddress address) {
		//TODO: implement
	}
	
}
