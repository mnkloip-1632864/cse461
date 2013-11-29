package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The FileFinder provides a way for clients to find nodes that have a 
 * particular file to download.
 */
public class FileFinder {

	// There is only one instance of the FileFinder
	private static volatile FileFinder INSTANCE = null;
	
	private Map<String, Queue<String>> files; // Filename -> hosts
	private Map<String, Set<String>> hosts; // Host -> filenames
	
	private FileFinder() {
		files = new HashMap<String, Queue<String>>();
		hosts = new HashMap<String, Set<String>>();
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
	public synchronized String getAddressFor(String fileName) {
		Queue<String> addresses = files.get(fileName);
		if(addresses == null || addresses.isEmpty()) {
			return null;
		}
		// cycle through the addresses
		String addr = addresses.remove();
		addresses.add(addr);
		return addr;
	}
	
	/**
	 * Removes the provided address from the FileFinder, including mappings from
	 * files to the address.
	 * @param address the address to remove.
	 */
	public synchronized void removeAddress(String address) {
		// find the list of files in the hosts table.
		Set<String> addrFiles = hosts.get(address);
		if(addrFiles == null) {
			// address has already been deleted
			return;
		} else {
			hosts.remove(address);
		}
		for (String file : addrFiles) {
			Queue<String> addrs = files.get(file);
			addrs.remove(address);
			if(addrs.isEmpty()) {
				// the final host for the file is gone, remove the file
				files.remove(file);
			} // else other nodes have the file and can be accessed
		}
	}
	
	/**
	 * Adds the fileNames to the given address.
	 */
	public synchronized void addFilesToAddress(String address, Set<String> fileNames) {
		// add the files => address to the file map.
		for (String fileName : fileNames) {
			Queue<String> addresses = files.get(fileName);
			if(addresses == null) {
				addresses = new LinkedList<String>();
			}
			addresses.add(address);
			files.put(fileName, addresses);
		}
		// add address => fileNames to the hosts map.
		if(hosts.containsKey(address)) {
			Set<String> oldNames = hosts.get(address);
			fileNames.addAll(oldNames);
		} 
		hosts.put(address, fileNames);
	}
	
	/**
	 * @return the files accessible from all nodes connected to the server.
	 */
	public Set<String> getFileNames() {
		return files.keySet();
	}
}
