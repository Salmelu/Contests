package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.ServerError;

/**
 * This class represents a task used by the Edit tables to remove the data from the server.<br>
 * It takes an array of integer ids which are then sent to the server to identify the removed data.
 * @author salmelu
 */
class TaskDelete extends TaskAbstract {
	
	/** The ids sent to the server */
	private int[] ids;
	/** An action code for the server */
	private PacketOrder packet;
	
	/**
	 * Constructs a delete task to be sent to the server.<br>
	 * Also sets a handler to call a reload action when the task is finished
	 * or show a connection error if the task fails.
	 * @param packet the order sent to the server
	 * @param ids the ids sent to the server, the order is maintained
	 */
	protected TaskDelete(PacketOrder packet, int... ids) {
		this.packet = packet;
		this.ids = ids;
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
	protected void send(ObjectOutputStream sender) throws IOException {
        sender.writeByte(packet.toByte());
        for(int id : ids) {
        	sender.writeInt(id);
        }
	}

	@Override
	protected void receive(ObjectInputStream receiver, boolean success)
			throws ClassNotFoundException, IOException {
		if(!success) 
        	setServerError((ServerError) receiver.readObject());
	}
	
}
