package cz.salmelu.contests.client;

import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.PacketTeam;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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

final class EditTeam implements Displayable {
	
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
	
	private EditTeam() {
		this.c = Client.get();
		
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
		if(instance == null) {
			instance = new EditTeam();
		}
		return instance;
	}
	
	private void displayHeader() {
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
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		fillFields();
		c.mainPanel.setCenter(gp);
	}

	private void deleteTeam() {
		if(currentCat == null || currentTeam == null) {
			ActionHandler.get().showErrorDialog("No team or team category selected",
					"You have not chosen a correct team to delete.");
			return;
		}
		if(!ActionHandler.get().showPromptDialog("Are you sure?",
				"Do you really want to remove the team?")) {
			return;
		}
		TaskDelete td = new TaskDelete(PacketOrder.TEAM_DELETE, c.current.getId(),
				currentCat.getId(), currentTeam.getId());
		Thread t = new Thread(td);
		t.run();
		ActionHandler.get().showSuccessDialog("Team deleted", "Team " + currentTeam.getName() + " was deleted.");
	}
	
	private void newTeam() {
		PacketTeam pt = new PacketTeam();
		pt.name = name.getText();
		try {
			pt.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			ActionHandler.get().showErrorDialog("Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		if(teamCat.getSelectionModel().getSelectedItem() == null) {
			ActionHandler.get().showErrorDialog("Invalid value", "No team category is selected.");
			return;
		}
		pt.tcId = teamCat.getSelectionModel().getSelectedItem().getId();
		if(pt.name == null || pt.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid team name selected. Please enter a name for the team.");
			return;
		}
		pt.id = 0;
		pt.conId = c.current.getId();
		TaskNewEdit<PacketTeam> tne = new TaskNewEdit<>(PacketOrder.TEAM_EDIT, pt);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("New team added", "You have successfully sent a request for a new team.");
	}
	
	private void updateTeam() {
		if(currentCat == null || currentTeam == null) {
			ActionHandler.get().showErrorDialog("No team or team category selected", "You have not chosen a team and team category.");
			return;
		}
		PacketTeam pt = new PacketTeam();
		pt.name = name.getText();
		try {
			pt.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			ActionHandler.get().showErrorDialog("Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		pt.tcId = teamCat.getSelectionModel().getSelectedItem().getId();
		pt.oldTcId = currentTeam.getCategory().getId();
		if(pt.name == null || pt.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid team name selected. Please enter a name for the team.");
			return;
		}
		pt.id = currentTeam.getId();
		pt.conId = c.current.getId();
		TaskNewEdit<PacketTeam> tne = new TaskNewEdit<>(PacketOrder.TEAM_EDIT, pt);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("Team update requested", "You have successfully sent a request for a team update.");
	}
}
