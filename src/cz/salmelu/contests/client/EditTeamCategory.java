package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cz.salmelu.contests.model.ScoreMode;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.TeamCategoryPacket;
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

final class EditTeamCategory {
	
	private Client c;
	private static EditTeamCategory instance = null;
	
	private ChoiceBox<TeamCategory> tcChoice = null;
	private Label tcLabel = null;
	private HBox tcBox = null;
	private TeamCategory currentTc = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;
	private ChoiceBox<ScoreMode> sm = null;
	
	private EditTeamCategory(Client c) {
		this.c = c;
		
		tcBox = new HBox(16);
		tcBox.setAlignment(Pos.CENTER);
		tcLabel = new Label("Choose a team category: ");
		tcChoice = new ChoiceBox<>();
		tcChoice.setPrefWidth(180);
		tcChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TeamCategory>() {
			@Override
			public void changed(ObservableValue<? extends TeamCategory> arg0, TeamCategory arg1,
					TeamCategory arg2) {
				currentTc = arg2;
				fillFields();
			}
		});
		deleteButton = new Button("Delete team category");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteTeamCategory();
			}
		});
		tcBox.setPadding(new Insets(0,15,40,15));
		tcBox.getChildren().addAll(tcLabel, tcChoice, deleteButton);
		
		createTable();
	}
	
	private void createTable() {
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
		
		name = new TextField();
		name.setPromptText("Enter team category name");
		name.setPrefWidth(200);

		sm = new ChoiceBox<>();
		sm.setItems(FXCollections.observableArrayList(ScoreMode.values()));
		sm.setPrefWidth(200);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New team category");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(200);
		Button updateButton = new Button("Update team category");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(200);
		
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				newTeamCategory();
			}
		});
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateTeamCategory();
			}
		});
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Team category name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(new Label("Team category score mode:"), 0, 1);
		gp.add(sm, 1, 1);
		gp.add(buttonBox, 0, 2, 2, 1);
	}

	protected static EditTeamCategory getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new EditTeamCategory(c);
	}
	
	protected void displayHeader() {
		int id = currentTc == null ? 0 : currentTc.getId();
		tcChoice.setItems(FXCollections.observableArrayList(new ArrayList<TeamCategory>(c.current.getTeamCategories().values())));
		for(TeamCategory tc : tcChoice.getItems()) {
			if(tc.getId() == id) {
				tcChoice.getSelectionModel().select(tc);
			}
		}
		c.mainPanel.setTop(tcBox);
	}

	private void fillFields() {
		if(currentTc == null || !c.current.hasTeamCategory(currentTc.getId())) {
			name.setText("");
		}
		else {
			name.setText(currentTc.getName());
			sm.getSelectionModel().select(currentTc.getScoreMode());
		}
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	private void deleteTeamCategory() {
		if(currentTc == null) {
			c.ah.showErrorDialog(c, "No team category selected", "You have not chosen a team category to delete.");
			return;
		}
		if(!c.ah.showPromptDialog(c, "Are you sure?",
				"Do you really want to remove the team category and all the teams in it?")) {
			return;
		}
		DeleteTask dt = new DeleteTask(c.current.getId(), currentTc.getId());
		dt.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(dt);
		t.run();
		c.ah.showSuccessDialog(c, "Team category deleted", "Team category " + currentTc.getName() + " was deleted.");
	}
	
	private void newTeamCategory() {
		TeamCategoryPacket tcp = new TeamCategoryPacket();
		tcp.name = name.getText();
		tcp.sm = sm.getSelectionModel().getSelectedItem();
		if(tcp.name == null || tcp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid team category name selected. Please enter a name for the team category.");
			return;
		}
		if(tcp.sm == null) {
			c.ah.showErrorDialog(c, "Field error", "An invalid score mode selected. Please enter a score mode for the team category.");
			return;
		}
		tcp.id = 0;
		tcp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(tcp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "New team category added", "You have successfully sent a request for a new team category.");
	}
	
	private void updateTeamCategory() {
		if(currentTc == null) {
			c.ah.showErrorDialog(c, "No team category selected", "You have not chosen a team category.");
			return;
		}
		TeamCategoryPacket tcp = new TeamCategoryPacket();
		tcp.name = name.getText();
		tcp.sm = sm.getSelectionModel().getSelectedItem();
		if(tcp.name == null || tcp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid team category name selected. Please enter a name for the team category.");
			return;
		}
		if(tcp.sm == null) {
			c.ah.showErrorDialog(c, "Field error", "An invalid score mode selected. Please enter a score mode for the team category.");
			return;
		}
		tcp.conId = c.current.getId();
		tcp.id = currentTc.getId();
		NewEditTask net = new NewEditTask(tcp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				if(net.getValue()) {
					c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
				}
				else {
					c.ah.showConnectionError(c);
				}
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "Team category update requested", "You have successfully sent a request for a team category update.");
	}
	
	private class NewEditTask extends Task<Boolean> {
		
		private TeamCategoryPacket tcp;
		
		protected NewEditTask(TeamCategoryPacket tcp) {
			this.tcp = tcp;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.TCATEGORY_EDIT.toByte());
		        send.writeObject(tcp);
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

		private int tcId;
		private int conId;
		
		protected DeleteTask(int conId, int tcId) {
			this.conId = conId;
			this.tcId = tcId;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.TCATEGORY_DELETE.toByte());
		        send.writeInt(conId);
		        send.writeInt(tcId);
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
