package utils;

/**
 * The MessageType tells the receiver what type of 
 * payload is being sent.
 */
public class MessageType {
	public static final byte LIST = 0;      // Message is a list of Strings
	public static final byte REQUEST = 1;   // Message is a file request
	public static final byte TERMINATE = 2; // Message is to terminate
	public static final byte FILE_META = 3; // Message is the meta data of the file
	public static final byte FILE_DATA = 4; // Message contains data for a file
	public static final byte REQUEST_AVAILABLE_FILES = 5;
	public static final byte UPDATE_AVAILABLE_FILES = 6;
}
