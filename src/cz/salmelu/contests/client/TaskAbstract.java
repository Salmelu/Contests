package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import cz.salmelu.contests.net.ServerError;
import javafx.concurrent.Task;

/**
 * An abstract parent for various client to server tasks to allow handling server errors.<br>
 * Defines two abstract methods, which are to be overridden to allow giving the methods desired behaviour. 
 * @author salmelu
 */
public abstract class TaskAbstract extends Task<Boolean> {
	private ServerError se = null;
	
	/**
	 * Gets a {@link ServerError} object representing the occured error.
	 * @return a ServerError object, or null, if no error happened on the server
	 */
	public ServerError getServerError() {
		return se;
	}
	
	/**
	 * Updates the object with the new error.
	 * @param se happening server error
	 */
	public void setServerError(ServerError se) {
		this.se = se;
	}
	

	@Override
	protected Boolean call() throws Exception {
		InetSocketAddress addr = new InetSocketAddress(Config.INET_ADDR, Config.INET_PORT);
		try(Socket socket = new Socket();) {
			socket.connect(addr);
			ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
	        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
	        // Do the sending operations
	        send(send);
	        send.flush();
	        boolean ret = get.readBoolean();
	        // Do the receiving operations
	        receive(get, ret);
			socket.close();
			return ret;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Called after opening the socket to the server. Must override to give a packet code and any other 
	 * required information
	 * @param sender supplied sending stream, to which the data is written
	 * @throws IOException if writing to the stream fails
	 */
	abstract protected void send(ObjectOutputStream sender) throws IOException;
	
	/**
	 * Called after receiving response from the server. Must be overriden to deal with the received data
	 *  and use them respectively.
	 * @param receiver receiving object stream
	 * @param success true, if the operation succeeded
	 * @throws ClassNotFoundException if the readObject method fails
	 * @throws IOException if writing to the stream fails
	 */
	abstract protected void receive(ObjectInputStream receiver, boolean success) throws ClassNotFoundException, IOException;
}
