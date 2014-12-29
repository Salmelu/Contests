package cz.salmelu.contests.client;

import java.util.ArrayList;

import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.model.Contestant;
import cz.salmelu.contests.model.Discipline;
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

final class ContestantTable {

	private Client c = null;
	private static ContestantTable instance = null;
	private Category currentCat = null;
	
	private HBox catBox = null;
	private Label catLabel = null;
	private ChoiceBox<Category> catChoice = null;

	private Label noCategory = null;
	private Label noContestant = null;
	private TableView<Contestant> table = null;
	
	private ContestantTable(Client c) {
		this.c = c;
		
		// Top panel
		catBox = new HBox();
		catBox.setAlignment(Pos.TOP_LEFT);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catBox.getChildren().addAll(catLabel, catChoice);
		catBox.setPadding(new Insets(0,15,15,15));
		
		// Center
		noCategory = new Label("Please, select a category");
		noCategory.setAlignment(Pos.CENTER);
		noContestant = new Label("There are no contestants in the selected category");
		noContestant.setAlignment(Pos.CENTER);
	}
	
	private void createTable() {
		if(currentCat == null) return;
		table = new TableView<>();

		TableColumn<Contestant, Integer> idCol = new TableColumn<>("Id");
		idCol.setMinWidth(30);
		idCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contestant,Integer>, 
				ObservableValue<Integer>>()  {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<Contestant, Integer> arg0) {
				return new SimpleIntegerProperty(arg0.getValue().getId()).asObject();
			}
		});
		TableColumn<Contestant, String> fNameCol = new TableColumn<>("First Name");
		fNameCol.setMinWidth(100);
		fNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contestant,String>, 
				ObservableValue<String>>()  {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Contestant, String> arg0) {
				return new SimpleStringProperty(arg0.getValue().getFirstName());
			}
		});
		TableColumn<Contestant, String> lNameCol = new TableColumn<>("Last Name");
		lNameCol.setMinWidth(100);
		lNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contestant,String>, 
				ObservableValue<String>>()  {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Contestant, String> arg0) {
				return new SimpleStringProperty(arg0.getValue().getLastName());
			}
		});
		table.getColumns().add(idCol);
		table.getColumns().add(fNameCol);
		table.getColumns().add(lNameCol);
		for(Discipline d : currentCat.getDisciplines()) {
			TableColumn<Contestant, Double> discCol = new TableColumn<>(d.getName());
			discCol.setMinWidth(50);
			discCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contestant,Double>, 
					ObservableValue<Double>>()  {
				@Override
				public ObservableValue<Double> call(
						CellDataFeatures<Contestant, Double> arg0) {
					return new SimpleDoubleProperty(arg0.getValue().getScore(d)).asObject();
				}
			});
			table.getColumns().add(discCol);
		}
		TableColumn<Contestant, Double> totalCol = new TableColumn<>("Total score");
		totalCol.setMinWidth(60);
		totalCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contestant,Double>, 
				ObservableValue<Double>>()  {
			@Override
			public ObservableValue<Double> call(
					CellDataFeatures<Contestant, Double> arg0) {
				return new SimpleDoubleProperty(arg0.getValue().getTotalScore()).asObject();
			}
		});
		table.getColumns().add(totalCol);
	}
	
	protected static ContestantTable getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new ContestantTable(c);
	}
	
	protected void displayHeader() {
		int id = currentCat == null ? 0 : currentCat.getId();
		catChoice.setItems(FXCollections.observableArrayList(new ArrayList<Category>(c.current.getCategories().values())));
		for(Category cat : catChoice.getItems()) {
			if(cat.getId() == id) {
				catChoice.getSelectionModel().select(cat);
			}
		}
		catChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Category>() {
			@Override
			public void changed(ObservableValue<? extends Category> arg0, Category arg1,
					Category arg2) {
				currentCat = arg2;
				displayTable();
			}
		});
		c.mainPanel.setTop(catBox);
	}
	
	protected void displayTable() {
		if(currentCat == null) {
			c.mainPanel.setCenter(noCategory);
			return;
		}
		if(c.current.getContestants(currentCat) == null || c.current.getContestants(currentCat).isEmpty()) {
			c.mainPanel.setCenter(noContestant);
			return;
		}
		createTable();
		table.setItems(FXCollections.observableArrayList(c.current.getContestants(currentCat).values()));
		c.mainPanel.setCenter(table);
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
}
