package cz.salmelu.contests.net;

/**
 * An enum used for sending error codes to client over the socket
 * @author salmelu
 */
public enum ServerError {
	InvalidPacket,
	InvalidInput,
	ContestNotFound,
	InvalidDataState,	// Data structure is different on client and server
	TeamCategoryNotFound,
	TeamNotFound,
	UnableToLock;
}
