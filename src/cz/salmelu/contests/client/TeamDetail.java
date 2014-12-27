package cz.salmelu.contests.client;

import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.model.TeamContestant;

class TeamDetail {
	
	private Client c = null;
	private static TeamDetail instance = null;
	private Team currentTeam = null;
	
	private HBox teamBox = null;
	private Label teamLabel = null;
	private ChoiceBox<Team> teamChoice = null;

	private Label noTeam = null;
	private Label emptyTeam = null;
	private TableView<TeamContestant> table = null;
	
	private TeamDetail(Client c) {
		this.c = c;
		
		// Top panel
		teamBox = new HBox();
		teamBox.setAlignment(Pos.TOP_LEFT);
		teamLabel = new Label("Choose a team: ");
		teamChoice = new ChoiceBox<>();
		teamBox.getChildren().addAll(teamLabel, teamChoice);
		teamBox.setPadding(new Insets(0,15,15,15));
		
		// Center
		noTeam = new Label("Please, select a team");
		noTeam.setAlignment(Pos.CENTER);
		emptyTeam = new Label("The selected team is empty.");
		emptyTeam.setAlignment(Pos.CENTER);
		createTable();
	}
	
	private void createTable() {
		table = new TableView<>();

		TableColumn<TeamContestant, Integer> idCol = new TableColumn<>("Id");
		idCol.setMinWidth(30);
		idCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeamContestant,Integer>, 
				ObservableValue<Integer>>()  {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<TeamContestant, Integer> arg0) {
				return new SimpleIntegerProperty(arg0.getValue().getId()).asObject();
			}
		});
		TableColumn<TeamContestant, String> fNameCol = new TableColumn<>("First Name");
		fNameCol.setMinWidth(100);
		fNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeamContestant,String>, 
				ObservableValue<String>>()  {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<TeamContestant, String> arg0) {
				return new SimpleStringProperty(arg0.getValue().getFirstName());
			}
		});
		TableColumn<TeamContestant, String> lNameCol = new TableColumn<>("Last Name");
		lNameCol.setMinWidth(100);
		lNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeamContestant,String>, 
				ObservableValue<String>>()  {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<TeamContestant, String> arg0) {
				return new SimpleStringProperty(arg0.getValue().getLastName());
			}
		});
		TableColumn<TeamContestant, Double> scoreCol = new TableColumn<>("Score");
		scoreCol.setMinWidth(60);
		scoreCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeamContestant,Double>, 
				ObservableValue<Double>>()  {
			@Override
			public ObservableValue<Double> call(
					CellDataFeatures<TeamContestant, Double> arg0) {
				return new SimpleDoubleProperty(arg0.getValue().getTotalScore()).asObject();
			}
		});
		TableColumn<TeamContestant, Double> bonusCol = new TableColumn<>("Bonus");
		bonusCol.setMinWidth(60);
		bonusCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TeamContestant,Double>, 
				ObservableValue<Double>>()  {
			@Override
			public ObservableValue<Double> call(
					CellDataFeatures<TeamContestant, Double> arg0) {
				return new SimpleDoubleProperty(arg0.getValue().getBonus()).asObject();
			}
		});		
		table.getColumns().add(idCol);
		table.getColumns().add(fNameCol);
		table.getColumns().add(lNameCol);
		table.getColumns().add(scoreCol);
		table.getColumns().add(bonusCol);
	}
	
	protected static TeamDetail getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new TeamDetail(c);
	}
	
	protected void displayHeader() {
		ArrayList<Team> teamList = new ArrayList<>();
		for(TeamCategory tc : c.current.getTeamCategories().values()) {
			teamList.addAll(c.current.getTeams(tc).values());
		}
		teamChoice.setItems(FXCollections.observableArrayList(teamList));
		if(teamChoice.getItems().contains(currentTeam)) {
			teamChoice.getSelectionModel().select(currentTeam);
		}
		teamChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Team>() {
			@Override
			public void changed(ObservableValue<? extends Team> arg0, Team arg1,
					Team arg2) {
				currentTeam = arg2;
				displayTable();
			}
		});
		c.mainPanel.setTop(teamBox);
	}
	
	protected void displayTable() {
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
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
}
