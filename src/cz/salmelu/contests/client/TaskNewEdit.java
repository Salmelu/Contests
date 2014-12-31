package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.Packet;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

class TaskNewEdit<T extends Packet> extends Task<Boolean> {
	
	PacketOrder packetOrder;
	T packet;
	
	protected TaskNewEdit(PacketOrder packetOrder, T packet) {
		this.packetOrder = packetOrder;
		this.packet = packet;
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
	        send.writeByte(packetOrder.toByte());
	        send.writeObject(packet);
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
