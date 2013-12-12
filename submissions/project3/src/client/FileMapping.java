package client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The FileMapping contains a mapping between file names
 * and where on the local disk they are currently stored.
 */
public class FileMapping {
	
	private Map<String, String> files; // Filename -> path
	
	public FileMapping() {
		files = new HashMap<String, String>();
	}
	
	/**
	 * Adds the indicated file to this mapping.
	 * @param filename the name of the file associated with
	 * 		  the 'filePath'
	 * @param filePath the path on the local machine to the file.
	 */
	public synchronized void addFile(String filename, String filePath) {
		files.put(filename, filePath);
	}
	
	/**
	 * Retrieves the path associated with the given filename.
	 * @param filename the name of the file to lookup.
	 * @return the path on the local machine of file named 
	 * 		   'filename'. Returns null if the file isn't in the
	 * 		   FileMapping.
	 */
	public synchronized String getPath(String filename) {
		return files.get(filename);
	}
	
	public synchronized Set<String> getAvailableFilenames() {
		return files.keySet();
	}
}
