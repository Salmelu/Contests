package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cz.salmelu.contests.model.ContestInfo;
import cz.salmelu.contests.model.ScoreMode;
import cz.salmelu.contests.net.ContestPacket;
import cz.salmelu.contests.net.Packet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

final class ContestEdit {
	
	private Client c;
	private static ContestEdit instance = null;
	
	private ChoiceBox<ContestInfo> contestChoice = null;
	private Label contestLabel = null;
	private HBox contestBox = null;
	private ContestInfo currentContest = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;
	private ChoiceBox<ScoreMode> sm = null;
	
	private ContestEdit(Client c) {
		this.c = c;
		
		contestBox = new HBox();
		contestBox.setAlignment(Pos.TOP_LEFT);
		contestLabel = new Label("Choose a contest: ");
		contestChoice = new ChoiceBox<>();
		deleteButton = new Button("Delete contest");
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteContest();
			}
		});
		contestBox.getChildren().addAll(contestLabel, contestChoice);
		contestBox.setPadding(new Insets(0,15,15,15));
		
		createTable();
	}
	
	private void createTable() {
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
		
		name = new TextField();
		name.setPromptText("Enter contest name");
		
		sm = new ChoiceBox<>();
		sm.setItems(FXCollections.observableArrayList(ScoreMode.values()));
		
		HBox buttonBox = new HBox(5);
		Button newButton = new Button("New contest");
		newButton.setPadding(new Insets(5,5,5,5));
		Button updateButton = new Button("Update contest");
		updateButton.setPadding(new Insets(5,5,5,5));
		
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				newContest();
			}
		});
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateContest();
			}
		});
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Contest name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(new Label("Contest score mode:"), 0, 1);
		gp.add(sm, 1, 1);
		gp.add(buttonBox, 0, 2, 2, 1);
	}

	protected static ContestEdit getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new ContestEdit(c);
	}
	
	protected void displayHeader() {
		int id = currentContest == null ? 0 : currentContest.getId();
		contestChoice.setItems(FXCollections.observableArrayList(new ArrayList<ContestInfo>(c.contests.values())));
		for(ContestInfo ci : contestChoice.getItems()) {
			if(ci.getId() == id) {
				contestChoice.getSelectionModel().select(ci);
			}
		}
		contestChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ContestInfo>() {
			@Override
			public void changed(ObservableValue<? extends ContestInfo> arg0, ContestInfo arg1,
					ContestInfo arg2) {
				currentContest = arg2;
				fillFields();
			}
		});
		c.mainPanel.setTop(contestBox);
	}

	private void fillFields() {
		if(currentContest == null || !c.contests.containsValue(currentContest)) {
			name.setText("");
		}
		else {
			name.setText(currentContest.getName());
			sm.getSelectionModel().select(currentContest.getScoreMode());
		}
	}
	
	protected void displayAll() {
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	private void deleteContest() {
		if(currentContest == null) {
			c.ah.showErrorDialog(c, "No contest selected", "You have not chosen a contest.");
			return;
		}
		DeleteTask dt = new DeleteTask(currentContest.getId());
		dt.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.ah.reloadContestList(c, false);
			}
		});
		
		Thread t = new Thread(dt);
		t.run();
		c.ah.showSuccessDialog(c, "Contest deleted", "Contest " + currentContest.getName() + " was deleted.");
	}
	
	private void newContest() {
		ContestPacket cp = new ContestPacket();
		cp.name = name.getText();
		if(cp.name == null || cp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid contest name selected. Please enter a name for the contest.");
			return;
		}
		cp.sm = sm.getSelectionModel().getSelectedItem();
		if(cp.sm == null) {
			c.ah.showErrorDialog(c, "Field error", "Please select a score mode for the contest");
			return;
		}
		cp.id = 0;
		NewEditTask net = new NewEditTask(cp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.ah.reloadContestList(c, false);
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "New contest added", "You have successfully sent a request for a new contest.");
	}
	
	private void updateContest() {
		if(currentContest == null) {
			c.ah.showErrorDialog(c, "No contest selected", "You have not chosen a contest.");
			return;
		}
		ContestPacket cp = new ContestPacket();
		cp.name = name.getText();
		if(cp.name == null || cp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid contest name selected. Please enter a name for the contest.");
			return;
		}
		cp.sm = sm.getSelectionModel().getSelectedItem();
		if(cp.sm == null) {
			c.ah.showErrorDialog(c, "Field error", "Please select a score mode for the contest");
			return;
		}
		cp.id = currentContest.getId();
		NewEditTask net = new NewEditTask(cp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				if(net.getValue())
				{
					c.ah.reloadContestList(c, false);
					if(c.current != null && c.current.getId() == cp.id) {
						c.ah.loadContest(c, cp.id);
					}
				}
				else {
					c.ah.showConnectionError(c);
				}
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "Contest update requested", "You have successfully sent a request for a contest update.");
	}
	
	private class NewEditTask extends Task<Boolean> {
		
		private ContestPacket cp;
		
		protected NewEditTask(ContestPacket cp) {
			this.cp = cp;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.CONTEST_EDIT.toByte());
		        send.writeObject(cp);
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
	
	private class DeleteTask extends Task<Boolean> {
		
		private int conId;
		
		protected DeleteTask(int conId) {
			this.conId = conId;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.CONTEST_DELETE.toByte());
		        send.writeInt(conId);
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
}
