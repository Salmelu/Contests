package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ServerError;

/**
 * This class represents the editing task sent to the server.<br>
 * It is used either on adding new data to the server, or on editing the data.<br> 
 * If the intention of this task is to add new data, the packet's id representing the data is set to 0. 
 * @author salmelu
 * @param <T> Packet type send to the server
 */
class TaskNewEdit<T extends Packet> extends TaskContest {

	/** An action code for the server */
	private PacketOrder packetOrder;
	/** Packet sent to the server */ 
	T packet;
	
	/**
	 * Constructs a new task for sending a data update or data insertion to the server.<br>
	 * Also sets a handler to call a reload action when the task is finished
	 * or show a connection error if the task fails.
	 * @param packetOrder the order sent to the server
	 * @param packet the transmitted packet
	 */
	protected TaskNewEdit(PacketOrder packetOrder, T packet) {
		this.packetOrder = packetOrder;
		this.packet = packet;
		this.setOnSucceeded(event -> {
			if(getValue())
				Client.get().handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			else if(getServerError() != null)
				ActionHandler.get().handleServerError(getServerError());
			else
				ActionHandler.get().showConnectionError();
		});
	}
	
	@Override
	protected Boolean call() throws Exception {
		try {
			InetSocketAddress addr = new InetSocketAddress(Config.INET_ADDR, Config.INET_PORT);
	        Socket socket = new Socket();
	        socket.connect(addr);
	        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
	        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
	        send.writeByte(packetOrder.toByte());
	        send.writeObject(packet);
	        send.flush();
	        boolean ret = get.readBoolean();
	        if(!ret) {
	        	setServerError((ServerError) get.readObject());
		        socket.close();
				return false;
	        }
	        socket.close();
	        return true;
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
