package cz.salmelu.contests.client;

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

/**
 * A class responsible for displaying a list of all teams in a chosen category and their respective scores.
 * @author salmelu
 */
final class TeamTable implements Displayable {
	
	private Client c = null;
	private static TeamTable instance = null;
	private TeamCategory currentCat = null;
	
	private HBox catBox = null;
	private Label catLabel = null;
	private ChoiceBox<TeamCategory> catChoice = null;

	private Label noCategory = null;
	private Label noTeams = null;
	private TableView<Team> table = null;
	
	/**
	 * Constructs a new TeamTable setting all the important variables and creating GUI elements.
	 */
	private TeamTable() {
		this.c = Client.get();
		
		// Top panel
		catBox = new HBox(16);
		catBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a team category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(200);
		catChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TeamCategory>() {
			@Override
			public void changed(ObservableValue<? extends TeamCategory> arg0, TeamCategory arg1,
					TeamCategory arg2) {
				currentCat = arg2;
				displayTable();
			}
		});
		catBox.getChildren().addAll(catLabel, catChoice);
		catBox.setPadding(new Insets(0,15,40,15));
		
		// Center
		noCategory = new Label("Please, select a category");
		noCategory.setAlignment(Pos.CENTER);
		noTeams = new Label("There are no teams in the selected category");
		noTeams.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Creates the GUI table. Sets all the GUI-related properties.
	 */
	private void createTable() {
		if(currentCat == null) return;
		table = new TableView<>();

		TableColumn<Team, Integer> idCol = new TableColumn<>("Id");
		idCol.setMinWidth(30);
		idCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Team,Integer>, 
				ObservableValue<Integer>>()  {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<Team, Integer> arg0) {
				return new SimpleIntegerProperty(arg0.getValue().getId()).asObject();
			}
		});
		TableColumn<Team, String> nameCol = new TableColumn<>("Team Name");
		nameCol.setMinWidth(100);
		nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Team,String>, 
				ObservableValue<String>>()  {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Team, String> arg0) {
				return new SimpleStringProperty(arg0.getValue().getName());
			}
		});TableColumn<Team, Integer> sizeCol = new TableColumn<>("Team Size");
		sizeCol.setMinWidth(40);
		sizeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Team,Integer>, 
				ObservableValue<Integer>>()  {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<Team, Integer> arg0) {
				return new SimpleIntegerProperty(arg0.getValue().getContestants().size()).asObject();
			}
		});
		TableColumn<Team, Double> bonusCol = new TableColumn<>("Bonus score");
		bonusCol.setMinWidth(60);
		bonusCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Team,Double>, 
				ObservableValue<Double>>()  {
			@Override
			public ObservableValue<Double> call(
					CellDataFeatures<Team, Double> arg0) {
				return new SimpleDoubleProperty(arg0.getValue().getBonus()).asObject();
			}
		});
		TableColumn<Team, Double> totalCol = new TableColumn<>("Total score");
		totalCol.setMinWidth(60);
		totalCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Team,Double>, 
				ObservableValue<Double>>()  {
			@Override
			public ObservableValue<Double> call(
					CellDataFeatures<Team, Double> arg0) {
				return new SimpleDoubleProperty(arg0.getValue().getTotalScore()).asObject();
			}
		});
		table.getColumns().add(idCol);
		table.getColumns().add(nameCol);
		table.getColumns().add(sizeCol);
		table.getColumns().add(bonusCol);
		table.getColumns().add(totalCol);
	}

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of TeamTable
	 */
	protected static TeamTable getInstance() {
		if(instance == null) {
			instance = new TeamTable();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for chosing the team category.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		int id = currentCat == null ? 0 : currentCat.getId();
		catChoice.setItems(FXCollections.observableArrayList(c.current.getTeamCategories().values()));
		for(TeamCategory tc : catChoice.getItems()) {
			if(tc.getId() == id) {
				catChoice.getSelectionModel().select(tc);
			}
		}
		c.mainPanel.setTop(catBox);
	}
	
	/**
	 * Displays a table of all teams depending on the team category set
	 */
	private void displayTable() {
		if(currentCat == null) {
			c.mainPanel.setCenter(noCategory);
			return;
		}
		if(c.current.getTeams(currentCat) == null || c.current.getTeams(currentCat).isEmpty()) {
			c.mainPanel.setCenter(noTeams);
			return;
		}
		createTable();
		table.setItems(FXCollections.observableArrayList(c.current.getTeams(currentCat).values()));
		c.mainPanel.setCenter(table);
	}
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
}
