package utils;

/**
 * The MessageType tells the receiver what type of 
 * payload is being sent.
 */
public class MessageType {
	public static final byte LIST = 0;      // Message is a list of Strings
	public static final byte REQUEST = 1;   // Message is a file request
	public static final byte TERMINATE = 2; // Message is to terminate
}
