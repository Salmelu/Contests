package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.TeamPacket;
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

final class EditTeam {
	
	private Client c;
	private static EditTeam instance = null;
	
	private ChoiceBox<TeamCategory> catChoice = null;
	private ChoiceBox<Team> teamChoice = null;
	private Label catLabel = null;
	private Label teamLabel = null;
	private HBox topBox = null;
	private TeamCategory currentCat = null;
	private Team currentTeam = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;
	private TextField bonus = null;
	private ChoiceBox<TeamCategory> teamCat = null; 
	
	private EditTeam(Client c) {
		this.c = c;
		
		topBox = new HBox(16);
		topBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a team category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(160);
		catChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TeamCategory>() {
			@Override
			public void changed(ObservableValue<? extends TeamCategory> arg0, TeamCategory arg1,
					TeamCategory arg2) {
				currentCat = arg2;
				updateTeamList();
			}
		});
		teamLabel = new Label("Choose a team: ");
		teamChoice = new ChoiceBox<>();
		teamChoice.setPrefWidth(160);
		teamChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Team>() {
			@Override
			public void changed(ObservableValue<? extends Team> arg0,
					Team arg1, Team arg2) {
				currentTeam = arg2;
				fillFields();
			}
		});
		deleteButton = new Button("Delete team");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(160);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteTeam();
			}
		});
		topBox.setPadding(new Insets(0,15,40,15));
		topBox.getChildren().addAll(catLabel, catChoice, teamLabel, teamChoice, deleteButton);
		
		createTable();
	}
	
	private void createTable() {
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
		
		name = new TextField();
		name.setPromptText("Enter team name");
		name.setPrefWidth(200);
		
		bonus = new TextField();
		bonus.setPromptText("Enter team bonus");
		bonus.setPrefWidth(160);
		
		teamCat = new ChoiceBox<>();
		teamCat.setItems(FXCollections.observableArrayList(c.current.getTeamCategories().values()));
		teamCat.setPrefWidth(180);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New team");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(180);
		Button updateButton = new Button("Update team");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(180);
		
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				newTeam();
			}
		});
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateTeam();
			}
		});
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Team name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(new Label("Team bonus:"), 0, 1);
		gp.add(bonus, 1, 1);
		gp.add(new Label("Team category:"), 0, 2);
		gp.add(teamCat, 1, 2);
		gp.add(buttonBox, 0, 3, 2, 1);
	}

	protected static EditTeam getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new EditTeam(c);
	}
	
	protected void displayHeader() {
		boolean selected = false;
		int id = currentCat == null ? 0 : currentCat.getId();
		catChoice.setItems(FXCollections.observableArrayList(c.current.getTeamCategories().values()));
		for(TeamCategory cat : catChoice.getItems()) {
			if(cat.getId() == id) {
				catChoice.getSelectionModel().select(cat);
				selected = true;
			}
		}
		if(!selected) {
			currentTeam = null;
		}
		
		c.mainPanel.setTop(topBox);
	}
	
	private void updateTeamList() {
		if(currentCat == null || c.current.getTeams(currentCat) == null) return;
		int id = currentTeam == null ? 0 : currentTeam.getId();
		teamChoice.setItems(FXCollections.observableArrayList(c.current.getTeams(currentCat).values()));
		for(Team t : teamChoice.getItems()) {
			if(t.getId() == id) {
				teamChoice.getSelectionModel().select(t);
			}
		}
	}

	private void fillFields() {
		teamCat.setItems(FXCollections.observableArrayList(c.current.getTeamCategories().values()));
		if(currentCat == null || currentTeam == null || !c.current.hasTeam(currentTeam)) {
			name.setText("");
			bonus.setText("");
		}
		else {
			name.setText(currentTeam.getName());
			bonus.setText(String.valueOf(currentTeam.getBonus()));
			teamCat.getSelectionModel().select(currentTeam.getCategory());
		}
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		fillFields();
		c.mainPanel.setCenter(gp);
	}

	private void deleteTeam() {
		if(currentCat == null || currentTeam == null) {
			c.ah.showErrorDialog(c, "No team or team category selected",
					"You have not chosen a correct team to delete.");
			return;
		}
		if(!c.ah.showPromptDialog(c, "Are you sure?",
				"Do you really want to remove the team?")) {
			return;
		}
		DeleteTask dt = new DeleteTask(c.current.getId(), currentCat.getId(), currentTeam.getId());
		dt.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(dt);
		t.run();
		c.ah.showSuccessDialog(c, "Team deleted", "Team " + currentTeam.getName() + " was deleted.");
	}
	
	private void newTeam() {
		TeamPacket tp = new TeamPacket();
		tp.name = name.getText();
		try {
			tp.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			c.ah.showErrorDialog(c, "Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		if(teamCat.getSelectionModel().getSelectedItem() == null) {
			c.ah.showErrorDialog(c, "Invalid value", "No team category is selected.");
			return;
		}
		tp.tcId = teamCat.getSelectionModel().getSelectedItem().getId();
		if(tp.name == null || tp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid team name selected. Please enter a name for the team.");
			return;
		}
		tp.id = 0;
		tp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(tp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "New team added", "You have successfully sent a request for a new team.");
	}
	
	private void updateTeam() {
		if(currentCat == null || currentTeam == null) {
			c.ah.showErrorDialog(c, "No team or team category selected", "You have not chosen a team and team category.");
			return;
		}
		TeamPacket tp = new TeamPacket();
		tp.name = name.getText();
		try {
			tp.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			c.ah.showErrorDialog(c, "Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		tp.tcId = teamCat.getSelectionModel().getSelectedItem().getId();
		tp.oldTcId = currentTeam.getCategory().getId();
		if(tp.name == null || tp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid team name selected. Please enter a name for the team.");
			return;
		}
		tp.id = currentTeam.getId();
		tp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(tp);
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
		c.ah.showSuccessDialog(c, "Team update requested", "You have successfully sent a request for a team update.");
	}
	
	private class NewEditTask extends Task<Boolean> {
		
		private TeamPacket tp;
		
		protected NewEditTask(TeamPacket tp) {
			this.tp = tp;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.TEAM_EDIT.toByte());
		        send.writeObject(tp);
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

		private int teamId;
		private int tcId;
		private int conId;
		
		protected DeleteTask(int conId, int tcId, int teamId) {
			this.conId = conId;
			this.tcId = tcId;
			this.teamId = teamId;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.TEAM_DELETE.toByte());
		        send.writeInt(conId);
		        send.writeInt(tcId);
		        send.writeInt(teamId);
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
