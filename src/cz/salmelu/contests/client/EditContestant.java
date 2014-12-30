package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cz.salmelu.contests.model.Contestant;
import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.model.TeamContestant;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ContestantPacket;
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

final class EditContestant {
	
	private Client c;
	private static EditContestant instance = null;
	private Team dummy = null;
	
	private ChoiceBox<Category> catChoice = null;
	private ChoiceBox<Contestant> csChoice = null;
	private Label catLabel = null;
	private Label csLabel = null;
	private HBox topBox = null;
	private Category currentCat = null;
	private Contestant currentCs = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField fName = null;
	private TextField lName = null;
	private TextField bonus = null;
	private ChoiceBox<Category> cat = null; 
	private ChoiceBox<Team> team = null; 
	
	private EditContestant(Client c) {
		this.c = c;
		dummy = new Team("No Team");
		
		topBox = new HBox(16);
		topBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(160);
		csLabel = new Label("Choose a contestant: ");
		csChoice = new ChoiceBox<>();
		csChoice.setPrefWidth(160);
		deleteButton = new Button("Delete contestant");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(160);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteContestant();
			}
		});
		topBox.setPadding(new Insets(0,15,40,15));
		topBox.getChildren().addAll(catLabel, catChoice, csLabel, csChoice, deleteButton);
		
		createTable();
	}
	
	private void createTable() {
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
		
		fName = new TextField();
		fName.setPromptText("Enter first name");
		fName.setPrefWidth(200);
		
		lName = new TextField();
		lName.setPromptText("Enter last name");
		lName.setPrefWidth(200);
		
		cat = new ChoiceBox<>();
		cat.setItems(FXCollections.observableArrayList(c.current.getCategories().values()));
		cat.setPrefWidth(180);
		
		bonus = new TextField();
		bonus.setPromptText("Enter team bonus");
		bonus.setPrefWidth(160);
		
		team = new ChoiceBox<>();
		ArrayList<Team> teamList = new ArrayList<>();
		teamList.add(dummy);
		for(TeamCategory tc : c.current.getTeamCategories().values()) {
			teamList.addAll(c.current.getTeams(tc).values());
		}
		team.setItems(FXCollections.observableArrayList(teamList));
		team.setPrefWidth(180);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New contestant");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(180);
		Button updateButton = new Button("Update contestant");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(180);
		
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				newContestant();
			}
		});
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateContestant();
			}
		});
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("First name:"), 0, 0);
		gp.add(fName, 1, 0);
		gp.add(new Label("Last name:"), 0, 1);
		gp.add(lName, 1, 1);
		gp.add(new Label("Category:"), 0, 2);
		gp.add(cat, 1, 2);
		gp.add(new Label("Team bonus:"), 0, 3);
		gp.add(bonus, 1, 3);
		gp.add(new Label("Team:"), 0, 4);
		gp.add(team, 1, 4);
		gp.add(buttonBox, 0, 5, 2, 1);
	}

	protected static EditContestant getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new EditContestant(c);
	}
	
	protected void displayHeader() {
		boolean selected = false;
		int id = currentCat == null ? 0 : currentCat.getId();
		catChoice.setItems(FXCollections.observableArrayList(c.current.getCategories().values()));
		for(Category cat : catChoice.getItems()) {
			if(cat.getId() == id) {
				catChoice.getSelectionModel().select(cat);
				selected = true;
			}
		}
		catChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Category>() {
			@Override
			public void changed(ObservableValue<? extends Category> arg0, Category arg1,
					Category arg2) {
				currentCat = arg2;
				updateCsList();
			}
		});
		if(!selected) {
			currentCs = null;
		}
		
		c.mainPanel.setTop(topBox);
	}
	
	private void updateCsList() {
		if(currentCat == null || c.current.getContestants(currentCat) == null) return;
		int id = currentCs == null ? 0 : currentCs.getId();
		csChoice.setItems(FXCollections.observableArrayList(c.current.getContestants(currentCat).values()));
		for(Contestant cs : csChoice.getItems()) {
			if(cs.getId() == id) {
				csChoice.getSelectionModel().select(cs);
			}
		}
		csChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Contestant>() {
			@Override
			public void changed(ObservableValue<? extends Contestant> arg0,
					Contestant arg1, Contestant arg2) {
				currentCs = arg2;
				fillFields();
			}
		});
	}

	private void fillFields() {
		// Update fields
		cat.setItems(FXCollections.observableArrayList(c.current.getCategories().values()));
		ArrayList<Team> teamList = new ArrayList<>();
		teamList.add(dummy);
		for(TeamCategory tc : c.current.getTeamCategories().values()) {
			teamList.addAll(c.current.getTeams(tc).values());
		}
		team.setItems(FXCollections.observableArrayList(teamList));
		
		// Fill the related fields
		if(currentCat == null || currentCs == null || !c.current.hasContestant(currentCs)) {
			fName.setText("");
			lName.setText("");
			bonus.setText("");
			team.getSelectionModel().select(dummy);
		}
		else {
			fName.setText(currentCs.getFirstName());
			lName.setText(currentCs.getLastName());
			cat.getSelectionModel().select(currentCs.getCategory());
			if(currentCs instanceof TeamContestant) {
				TeamContestant tcs = (TeamContestant) currentCs;
				bonus.setText(String.valueOf(tcs.getBonus()));
				if(tcs.getTeam() != null) {
					team.getSelectionModel().select(tcs.getTeam());
				}
				else {
					team.getSelectionModel().select(dummy);
				}
			}
		}
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		fillFields();
		c.mainPanel.setCenter(gp);
	}

	private void deleteContestant() {
		if(currentCat == null || currentCs == null) {
			c.ah.showErrorDialog(c, "No contestant or category selected",
					"You have not chosen a correct contestant to delete.");
			return;
		}
		if(!c.ah.showPromptDialog(c, "Are you sure?",
				"Do you really want to remove the contestant?")) {
			return;
		}
		DeleteTask dt = new DeleteTask(c.current.getId(), currentCat.getId(), currentCs.getId());
		dt.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(dt);
		t.run();
		c.ah.showSuccessDialog(c, "Contestant deleted", "Contestant " + currentCs.toString() + " was deleted.");
	}
	
	private void newContestant() {
		ContestantPacket cp = new ContestantPacket();
		cp.fName = fName.getText();
		cp.lName = lName.getText();
		try {
			cp.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			c.ah.showErrorDialog(c, "Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		if(cat.getSelectionModel().getSelectedItem() == null) {
			c.ah.showErrorDialog(c, "Invalid value", "No category is selected.");
			return;
		}
		cp.catId = cat.getSelectionModel().getSelectedItem().getId();
		if(cp.fName == null || cp.fName.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid first name selected. Please enter a valid name.");
			return;
		}
		if(cp.lName == null || cp.lName.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid last name selected. Please enter a valid name.");
			return;
		}
		if(team.getSelectionModel().getSelectedItem().equals(dummy)) {
			cp.teamId = 0;
		}
		else {
			cp.teamId = team.getSelectionModel().getSelectedItem().getId();
			cp.tcId = team.getSelectionModel().getSelectedItem().getCategory().getId();
		}
		cp.id = 0;
		cp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(cp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "New contestant added", "You have successfully sent a request for a new contestant.");
	}
	
	private void updateContestant() {
		if(currentCat == null || currentCs == null) {
			c.ah.showErrorDialog(c, "No category or contestant selected", "You have not chosen a contestant and a category.");
			return;
		}
		ContestantPacket cp = new ContestantPacket();
		cp.fName = fName.getText();
		cp.lName = lName.getText();
		try {
			cp.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			c.ah.showErrorDialog(c, "Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		if(cat.getSelectionModel().getSelectedItem() == null) {
			c.ah.showErrorDialog(c, "Invalid value", "No category is selected.");
			return;
		}
		cp.catId = cat.getSelectionModel().getSelectedItem().getId();
		cp.oldCatId = currentCs.getCategory().getId();
		if(cp.fName == null || cp.fName.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid first name selected. Please enter a valid name.");
			return;
		}
		if(cp.lName == null || cp.lName.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid last name selected. Please enter a valid name.");
			return;
		}
		if(team.getSelectionModel().getSelectedItem().equals(dummy)) {
			cp.teamId = 0;
		}
		else {
			cp.teamId = team.getSelectionModel().getSelectedItem().getId();
			cp.tcId = team.getSelectionModel().getSelectedItem().getCategory().getId();
		}
		cp.id = currentCs.getId();
		cp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(cp);
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
		c.ah.showSuccessDialog(c, "Contestant update requested", "You have successfully sent a request for a contestant update.");
	}
	
	private class NewEditTask extends Task<Boolean> {
		
		private ContestantPacket cp;
		
		protected NewEditTask(ContestantPacket cp) {
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
		        send.writeByte(Packet.CONTESTANT_EDIT.toByte());
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

		private int id;
		private int catId;
		private int conId;
		
		protected DeleteTask(int conId, int catId, int id) {
			this.conId = conId;
			this.catId = catId;
			this.id = id;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.CONTESTANT_DELETE.toByte());
		        send.writeInt(conId);
		        send.writeInt(catId);
		        send.writeInt(id);
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
