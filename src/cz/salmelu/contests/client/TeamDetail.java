package cz.salmelu.contests.client;

import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.model.TeamContestant;

/**
 * A class responsible for displaying a list of all members in a certain team and their respective scores.
 * @author salmelu
 */
final class TeamDetail implements Displayable {
	
	private Client c = null;
	private static TeamDetail instance = null;
	private Team currentTeam = null;
	
	private HBox teamBox = null;
	private Label teamLabel = null;
	private ChoiceBox<Team> teamChoice = null;

	private Label noTeam = null;
	private Label emptyTeam = null;
	private TableView<TeamContestant> table = null;
	
	/**
	 * Constructs a new TeamDetail setting all the important variables and creating GUI elements.
	 */
	private TeamDetail() {
		this.c = Client.get();
		
		// Top panel
		teamBox = new HBox(16);
		teamBox.setAlignment(Pos.CENTER);
		teamLabel = new Label("Choose a team: ");
		teamChoice = new ChoiceBox<>();
		teamChoice.setPrefWidth(200);
		teamChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentTeam = newVal;
			displayTable();
		});
		teamBox.getChildren().addAll(teamLabel, teamChoice);
		teamBox.setPadding(new Insets(0,15,40,15));
		
		// Center
		noTeam = new Label("Please, select a team");
		noTeam.setAlignment(Pos.CENTER);
		emptyTeam = new Label("The selected team is empty.");
		emptyTeam.setAlignment(Pos.CENTER);
		createTable();
	}
	
	/**
	 * Creates the GUI table. Sets all the GUI-related properties.
	 */
	private void createTable() {
		table = new TableView<>();

		TableColumn<TeamContestant, Integer> idCol = new TableColumn<>("Id");
		idCol.setMinWidth(30);
		idCol.setCellValueFactory(cell ->
				new SimpleIntegerProperty(cell.getValue().getId()).asObject());
		TableColumn<TeamContestant, String> fNameCol = new TableColumn<>("First Name");
		fNameCol.setMinWidth(100);
		fNameCol.setCellValueFactory(cell ->
				new SimpleStringProperty(cell.getValue().getFirstName()));
		TableColumn<TeamContestant, String> lNameCol = new TableColumn<>("Last Name");
		lNameCol.setMinWidth(100);
		lNameCol.setCellValueFactory(cell ->
				new SimpleStringProperty(cell.getValue().getLastName()));
		TableColumn<TeamContestant, Double> scoreCol = new TableColumn<>("Score");
		scoreCol.setMinWidth(60);
		scoreCol.setCellValueFactory(cell ->
				new SimpleDoubleProperty(cell.getValue().getTotalScore()).asObject());
		TableColumn<TeamContestant, Double> bonusCol = new TableColumn<>("Bonus");
		bonusCol.setMinWidth(60);
		bonusCol.setCellValueFactory(cell ->
				new SimpleDoubleProperty(cell.getValue().getBonus()).asObject());	
		table.getColumns().add(idCol);
		table.getColumns().add(fNameCol);
		table.getColumns().add(lNameCol);
		table.getColumns().add(scoreCol);
		table.getColumns().add(bonusCol);
	}

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of TeamDetail
	 */
	protected static TeamDetail getInstance() {
		if(instance == null) {
			instance = new TeamDetail();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for chosing the team.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		int id = currentTeam == null ? 0 : currentTeam.getId();
		ArrayList<Team> teamList = new ArrayList<>();
		for(TeamCategory tc : c.current.getTeamCategories().values()) {
			teamList.addAll(c.current.getTeams(tc).values());
		}
		teamChoice.setItems(FXCollections.observableArrayList(teamList));
		for(Team t : teamChoice.getItems()) {
			if(t.getId() == id) {
				teamChoice.getSelectionModel().select(t);
			}
		}
		c.mainPanel.setTop(teamBox);
	}
	
	/**
	 * Displays a table with the details about the team depending on the team set
	 */
	private void displayTable() {
		if(currentTeam == null) {
			c.mainPanel.setCenter(noTeam);
			return;
		}
		if(currentTeam.getContestants().isEmpty()) {
			c.mainPanel.setCenter(emptyTeam);
			return;
		}
		table.setItems(FXCollections.observableArrayList(currentTeam.getContestants()));
		c.mainPanel.setCenter(table);
	}
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
}
