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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.model.ContestInfo;
import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.ServerError;

/**
 * A class responsible for all GUI displaying and loading tasks.<br>
 * It is implemented as a singleton class to allow accessing it from anywhere.<br> 
 * Contains display methods, which are responsible for displaying GUI components,
 * and loading methods, which are responsible for loading the content from the server.
 * @author salmelu
 */
@SuppressWarnings("deprecation")
class ActionHandler {
	
	/** instance holder */
	private static ActionHandler instance = null;
	
	/**
	 * Construct a new empty action holder. 
	 * The constructor should be only called once, for creating a first instance.
	 */
	private ActionHandler() {
		
	}
	
	/**
	 * Gets an instance of ActionHandler. If there is no instance yet, creates a new one.
	 * @return an instance of ActionHandler
	 */
	protected static ActionHandler get() {
		if(instance == null) {
			instance = new ActionHandler();
		}
		return instance;
	}
	
	/**
	 * Shows a list of all contests found on the server.
	 * Uses a {@link ContestTable} class to display the table with contests' relevant information.
	 */
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
	
	/**
	 * Reloads the contest list from the server.<br>
	 * The reload task is called as a different thread using JavaFX tasks.<br>
	 * If the reloading fails, displays a GUI dialog to inform the user of the failure. 
	 * @param display when set to true, the task will call {@link #showContestList} to show the list
	 */
	protected void reloadContestList(boolean display) {
		TaskContest load = new TaskContest() {			
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
		load.setOnSucceeded(event -> {
			if(load.getValue() && display) {
				showContestList();
			}
			else if(!load.getValue() && load.getServerError() != null) {
				handleServerError(load.getServerError());
				Client.get().contests = null;
			}
			else if(!load.getValue()) {
				showConnectionError();
				Client.get().contests = null;
			}
		});
		Thread t = new Thread(load);
		t.setDaemon(true);
		t.run();
	}
	
	/**
	 * Loads full contest data from the server. <br>
	 * Gets all the relevant information such as a list of all contestants, teams, categories, etc.<br>
	 * The method will display a success dialog, if the loading is successful, and an error dialog, 
	 * if the loading is unsuccessful.
	 * @param id id of the loaded contest
	 */
	protected void loadContest(Integer id) {
		loadContest(id, true);
	}
	
	/**
	 * Loads full contest data from the server.<br>
	 * Gets all the relevant information such as a list of all contestants, teams, categories, etc.<br>
	 * The method will display an error dialog, if the loading is unsuccessful.
	 * @param id id of the loaded contest
	 * @param display if set to true, displays a success dialog when the contest is loaded.
	 */
	protected void loadContest(Integer id, boolean display) {
		TaskContest load = new TaskContest() {
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
			        	setServerError((ServerError) get.readObject());
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
		load.setOnSucceeded(event -> {
			if(display && load.getValue()) showSuccessDialog("Contest was loaded", 
					"Contest " + Client.get().current.getName() + " was successfully loaded.");	
			if(!load.getValue() && load.getServerError() != null) {
				handleServerError(load.getServerError());
			}
			else if(!load.getValue()) {
				showConnectionError();
			}
		});
		Thread t = new Thread(load);
		t.setDaemon(true);
		t.run();	
	}
	
	/**
	 * Clears all the elements from the BorderPane.
	 * This is used before any GUI updates to clear the header and body.
	 * It is mean to prevent other components to stay displayed in case something fails. 
	 */
	private void clearPanel() {
		Client.get().mainPanel.setTop(null);
		Client.get().mainPanel.setCenter(null);
	}
	
	/**
	 * Show a Displayable in the GUI.
	 * Clears the BorderPane and fills it with new content.
	 * @param d Displayable to be shown.
	 */
	protected void showTable(Displayable d) {
		if(!Client.contestSelected() && !(d instanceof ContestTable)) {
			showNoContestWarning();
			return;
		}
		clearPanel();
		d.displayAll();
	}
	
	/**
	 * Shows a warning dialog stating that there is no contest selected,
	 *  so it doesn't make sense to display a GUI table.
	 */
	protected void showNoContestWarning() {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Warning!")
			.masthead("No contest selected")
			.message("No contest is currently chosen. Please set your current contest in the main menu.")
	    	.showWarning();
	}
	
	/**
	 * Shows a prompt dialog for the user.
	 * @param sm the header of the dialog
	 * @param lm the text of the dialog
	 * @return true, if the user clicked Yes, false on any other action
	 */
	protected boolean showPromptDialog(String sm, String lm) {
		Action response = Dialogs.create()
		        .owner(Client.get().mainStage)
		        .title("Really?")
		        .masthead(sm)
		        .message(lm)
		        .showConfirm();
		return response == Dialog.ACTION_YES;
	}
	
	/**
	 * Show an error stating that the client couldn't connect to the server.
	 */
	protected void showConnectionError() {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Connection Error")
			.masthead("Unable to connect to host")
			.message("The program was unable to connect to host " + Config.INET_ADDR 
					+ ". Please check that you are using a valid hostname and try again.")
	    	.showError();
	}
	
	/**
	 * Shows an error dialog with a custom message.
	 * @param sm the header of the dialog
	 * @param lm the text of the dialog
	 */
	protected void showErrorDialog(String sm, String lm) {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Error!")
			.masthead(sm)
			.message(lm)
	    	.showError();
	}
	
	/**
	 * Shows a success dialog with a custom message.
	 * @param sm the header of the dialog
	 * @param lm the text of the dialog
	 */
	protected void showSuccessDialog(String sm, String lm) {
		Dialogs.create()
			.owner(Client.get().mainStage)
			.title("Success!")
			.masthead(sm)
			.message(lm)
	    	.showInformation();
	}
	
	protected void handleServerError(ServerError se) {
		switch(se) {
		case ContestNotFound:
			showErrorDialog("Invalid contest", 
					"You were trying to access a contest, which probably doesn't exist anymore. "
					+ "Please, choose a valid contest.");
			break;
		case InvalidDataState:
			showErrorDialog("Invalid data state", 
					"The data on the server have changed since you last downloaded them and "
					+ "therefore the operation was invalid. Please reload the data and try again.");
			break;
		case InvalidInput:
			showErrorDialog("Invalid input", 
					"Server has obtained invalid packet data and therefore it cannot process the operation."
					+ "Please try again.");
			break;
		case InvalidPacket:
			showErrorDialog("Invalid packet code", 
					"Server has received an unknown packet code.");
			break;
		case UnableToLock:
			showErrorDialog("Unable to lock data", 
					"Server couldn't lock the data. "
					+ "Please verify that the server is running correctly and try again.");
			break;
		}
	}
}
