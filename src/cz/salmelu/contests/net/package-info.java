/**
 * Contains classes used for network communication between client and server.<br>
 * It contains {@link PacketOrder} class used for sending request orders from client to server,
 * {@link ServerError} to send error codes from server to client and classes implementing {@link Packet} 
 * interface to be used by the client to send a data change request.<br>
 * All the member fields in {@link Packet} implementations are public as those are used as simple data holders.
 * @author salmelu
 */
package cz.salmelu.contests.net;