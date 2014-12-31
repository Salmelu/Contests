package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.model.ContestInfo;
import cz.salmelu.contests.net.PacketOrder;

@SuppressWarnings("deprecation")
class ActionHandler {
	
	private static ActionHandler instance = null;
	
	private ActionHandler() {
		
	}
	
	protected static ActionHandler get() {
		if(instance == null) {
			instance = new ActionHandler();
		}
		return instance;
	}
	
	protected void showContestList() {
		clearPanel();
		if(Client.get().contests == null) {
			Client.get().contests = new HashMap<>();
			reloadContestList(true);
			Label inform = new Label("Loading contest list from the server, please wait");
			inform.setAlignment(Pos.CENTER);
			Client.get().mainPanel.setCenter(inform);
			return;
		}
		if(Client.get().contests.isEmpty()) {
			Label done = new Label("There are no contests found on the server. Create a new contest using the menu.");
			done.setAlignment(Pos.CENTER);
			Client.get().mainPanel.setCenter(done);
		}
		ContestTable.getInstance().displayAll();
	}
	
	protected void reloadContestList(boolean display) {
		Task<Boolean> load = new Task<Boolean>() {			
			@SuppressWarnings("unchecked")
			@Override
			protected Boolean call() {				
				// Get the stuff from server
				try {
					InetSocketAddress addr = new InetSocketAddress(Config.INET_ADDR, Config.INET_PORT);
			        Socket socket = new Socket();
			        socket.connect(addr);
			        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
			        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
			        send.writeByte(PacketOrder.ALL_GET_NAMES.toByte());
			        send.flush();
			        boolean ret = get.readBoolean();
			        if(!ret) {
						Label error = new Label("Error loading contest list from server!"); 
						error.setAlignment(Pos.CENTER);
						Client.get().mainPanel.setCenter(error);
						socket.close();
						return false;
			        }
			        Client.get().contests = (HashMap<String, ContestInfo>) get.readObject();
			        socket.close();
			        return true;
				}
				catch (UnknownHostException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
		if(display) {
			load.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent arg0) {
					if(load.getValue()) {
						showContestList();
					}
					else {
						showConnectionError();
						Client.get().contests = null;
					}
				}
			});
		}
		else {
			load.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent arg0) {
					if(!load.getValue()) {
						showConnectionError();
						Client.get().contests = null;
					}
				}
			});
		}
		Thread t = new Thread(load);
		t.setDaemon(true);
		t.run();
	}
	
	protected void loadContest(Integer id) {
		loadContest(id, true);
	}
	
	protected void loadContest(Integer id, boolean display) {
		Task<Boolean> load = new Task<Boolean>() {
			@Override
			protected Boolean call() {			
				try {
					InetSocketAddress addr = new InetSocketAddress(Config.INET_ADDR, Config.INET_PORT);
			        Socket socket = new Socket();
			        socket.connect(addr);
			        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
			        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
			        send.writeByte(PacketOrder.CONTEST_GET.toByte());
			        send.writeInt(id);
			        send.flush();
			        boolean ret = get.readBoolean();
			        if(!ret) {
						socket.close();
						return false;
			        }
			        Client.get().current = (Contest) get.readObject();
			        socket.close();
			        return true;
				}
				catch (UnknownHostException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
		load.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				if(display) showSuccessDialog("Contest was loaded", 
						"Contest " + Client.get().current.getName() + " was successfully loaded.");				
			}
		});
		Thread t = new Thread(load);
		t.setDaemon(true);
		t.run();	
	}
	
	private void clearPanel() {
		Client.get().mainPanel.setTop(null);
		Client.get().mainPanel.setCenter(null);
	}
	
	protected void showTable(Displayable d) {
		if(!Client.contestSelected()) {
			showNoContestWarning();
			return;
		}
		clearPanel();
		d.displayAll();
	}
	
	protected void showNoContestWarning() {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Warning!")
			.masthead("No contest selected")
			.message("No contest is currently chosen. Please set your current contest in the main menu.")
	    	.showWarning();
	}
	
	protected boolean showPromptDialog(String sm, String lm) {
		Action response = Dialogs.create()
		        .owner(Client.get().mainStage)
		        .title("Really?")
		        .masthead(sm)
		        .message(lm)
		        .showConfirm();
		return response == Dialog.ACTION_YES;
	}
	
	protected void showConnectionError() {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Connection Error")
			.masthead("Unable to connect to host")
			.message("The program was unable to connect to host " + Config.INET_ADDR 
					+ ". Please check that you are using a valid hostname and try again.")
	    	.showError();
	}
	
	protected void showErrorDialog(String sm, String lm) {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Error!")
			.masthead(sm)
			.message(lm)
	    	.showError();
	}
	
	protected void showSuccessDialog(String sm, String lm) {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Success!")
			.masthead(sm)
			.message(lm)
	    	.showInformation();
	}
}
