package cz.salmelu.contests.net;

/**
 * An enum used for sending error codes to client over the socket.
 * @author salmelu
 */
public enum ServerError {
	/** Server received a byte which doesn't represent any of the packets */
	InvalidPacket,
	/** Server expected a different data structure for the obtained packet */
	InvalidInput,
	/** Server got a task to be done on a contest, which doesn't exist anymore */
	ContestNotFound,
	/** The data on the server are different, then on the client */ 
	InvalidDataState,
	/** There was an error while the server was locking the data to access them */
	UnableToLock;
}
