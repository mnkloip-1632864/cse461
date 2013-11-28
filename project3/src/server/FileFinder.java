package server;

/**
 * The FileFinder provides a way for clients to find nodes that have a 
 * particular file to download.
 */
public class FileFinder {

	// There is only one instance of the FileFinder
	private static volatile FileFinder INSTANCE = null;
	
	private FileFinder() {
		
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
	
}
