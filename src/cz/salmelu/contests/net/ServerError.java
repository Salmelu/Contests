package cz.salmelu.contests.net;

/**
 * An enum used for sending error codes to client over the socket
 * @author salmelu
 */
public enum ServerError {
	InvalidPacket,
	InvalidInput,
	ContestNotFound,
	TeamCategoryNotFound,
	TeamNotFound
}
