package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.controlsfx.dialog.Dialogs;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.model.ContestInfo;
import cz.salmelu.contests.net.Packet;

@SuppressWarnings("deprecation")
class ActionHandler {
	
	protected ActionHandler() {
		
	}
	
	protected void showContestList(Client c) {
		if(c.contests == null) {
			c.contests = new HashMap<>();
			reloadContestList(c, true);
			Label inform = new Label("Loading contest list from the server, please wait");
			inform.setAlignment(Pos.CENTER);
			c.mainPanel.setCenter(inform);
			return;
		}
		if(c.contests.isEmpty()) {
			Label done = new Label("There are no contests found on the server. Create a new contest using the menu.");
			done.setAlignment(Pos.CENTER);
			c.mainPanel.setCenter(done);
		}
		if(ContestTable.getInstance() == null) {
			ContestTable.setClient(c);
		}
		ContestTable.getInstance().display();
	}
	
	protected void reloadContestList(Client c, boolean display) {
		Task<Boolean> load = new Task<Boolean>() {			
			@SuppressWarnings("unchecked")
			@Override
			protected Boolean call() {				
				// Get the stuff from server
				try {
					InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
			        Socket socket = new Socket();
			        socket.connect(addr);
			        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
			        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
			        send.writeByte(Packet.ALL_GET_NAMES.toByte());
			        send.flush();
			        boolean ret = get.readBoolean();
			        if(!ret) {
						Label error = new Label("Error loading contest list from server!"); 
						error.setAlignment(Pos.CENTER);
						c.mainPanel.setCenter(error);
						socket.close();
						return false;
			        }
			        c.contests = (HashMap<String, ContestInfo>) get.readObject();
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
						showContestList(c);
					}
					else {
						showConnectionError(c);
						c.contests = null;
					}
				}
			});
		}
		else {
			load.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent arg0) {
					if(!load.getValue()) {
						showConnectionError(c);
						c.contests = null;
					}
				}
			});
		}
		Thread t = new Thread(load);
		t.setDaemon(true);
		t.run();
	}
	
	protected void loadContest(Client c, Integer id) {
		Task<Boolean> load = new Task<Boolean>() {
			@Override
			protected Boolean call() {			
				try {
					InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
			        Socket socket = new Socket();
			        socket.connect(addr);
			        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
			        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
			        send.writeByte(Packet.CONTEST_GET.toByte());
			        send.writeInt(id);
			        send.flush();
			        boolean ret = get.readBoolean();
			        if(!ret) {
						socket.close();
						return false;
			        }
			        c.current = (Contest) get.readObject();
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
				showSuccessDialog(c, "Contest was loaded", "Contest " + c.current.getName() + " was successfully loaded.");				
				if(c.currentMenu == MenuAction.SHOW_CONTESTANTS || c.currentMenu == MenuAction.SHOW_TEAMS) {
					c.handleMenuAction(c.currentMenu);
				}
			}
		});
		Thread t = new Thread(load);
		t.setDaemon(true);
		t.run();	
	}
	
	protected void showContestantTable(Client c) {
		if(c.current == null) {
			showNoContestWarning(c);
		}
		if(ContestantTable.getInstance() == null) {
			ContestantTable.setClient(c);
		}
		ContestantTable.getInstance().displayAll();
	}
	
	protected void showTeamTable(Client c) {
		if(c.current == null) {
			showNoContestWarning(c);
		}
		if(TeamTable.getInstance() == null) {
			TeamTable.setClient(c);
		}
		TeamTable.getInstance().displayAll();
	}
	
	protected void showTeamDetail(Client c) {
		if(c.current == null) {
			showNoContestWarning(c);
		}
		if(TeamDetail.getInstance() == null) {
			TeamDetail.setClient(c);
		}
		TeamDetail.getInstance().displayAll();
	}
	
	protected void showNoContestWarning(Client c) {
		Dialogs.create()
			.owner(c.mainStage)
			.title("Warning!")
			.masthead("No contest selected")
			.message("No contest is currently chosen. Please set your current contest in the main menu.")
	    	.showWarning();
	}
	
	protected void showConnectionError(Client c) {
		Dialogs.create()
			.owner(c.mainStage)
			.title("Connection Error")
			.masthead("Unable to connect to host")
			.message("The program was unable to connect to host " + Config.INET_ADDR 
					+ ". Please check that you are using a valid hostname and try again.")
	    	.showError();
	}
	
	protected void showSuccessDialog(Client c, String sm, String lm) {
		Dialogs.create()
			.owner(c.mainStage)
			.title("Success!")
			.masthead(sm)
			.message(lm)
	    	.showInformation();
	}
}
