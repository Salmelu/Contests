package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.salmelu.contests.net.PacketOrder;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * This class represents a task used by the Edit tables 
 * to remove the data from the server.
 * @author salmelu
 */
class TaskDelete extends Task<Boolean> {
	
	/** The ids sent to the server */
	private int[] ids;
	/** An action code for the server */
	private PacketOrder packet;
	
	/**
	 * Constructs a delete task
	 * @param packet the order sent to the server
	 * @param ids the ids sent to the server, the order is maintained
	 */
	protected TaskDelete(PacketOrder packet, int... ids) {
		this.packet = packet;
		this.ids = ids;
		this.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				if(getValue()) {
					Client.get().handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
				}
				else {
					ActionHandler.get().showConnectionError();
				}
			}
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
	        send.writeByte(packet.toByte());
	        for(int id : ids) {
	        	send.writeInt(id);
	        }
	        send.flush();
	        boolean ret = get.readBoolean();
	        socket.close();
	        if(!ret) {	
	        	return false;
	        }
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
