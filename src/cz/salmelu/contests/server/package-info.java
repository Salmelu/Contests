/**
 * Contains all the server-side only code.<br> 
 * The server listens on a port for various client requests.<br>
 * Contains {@link AutoSaver} class used for automatic saving of data,
 * {@link DataHolder} class holding all the important data and allowing significant data changes,
 * {@link PacketProcesser} for processing the incoming packets and main {@link Server} class 
 * which sets up the socket to listen for the requests.<br>
 * The server is designed to hold all necessary data, which is then distributed to clients.
 * @author salmelu
 */
package cz.salmelu.contests.server;