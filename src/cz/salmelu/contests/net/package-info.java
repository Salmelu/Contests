/**
 * This package contains few classes used for network communication between client and server.
 * It contains PacketOrder and ServerError enums and all the Packets used for data transfer from client to server.
 * The updating packets (except PacketUpdateScore) implement Packet interface, which is used in TaskNewEdit.
 * @author salmelu
 */
package cz.salmelu.contests.net;