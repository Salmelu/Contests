package cz.salmelu.contests.client;

import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.model.Contestant;
import cz.salmelu.contests.model.Discipline;
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

/**
 * A class responsible for displaying a list of all contestants in a chosen category and their respective scores.
 * @author salmelu
 */
final class ContestantTable implements Displayable  {

	private Client c = null;
	private static ContestantTable instance = null;
	private Category currentCat = null;
	
	private HBox catBox = null;
	private Label catLabel = null;
	private ChoiceBox<Category> catChoice = null;

	private Label noCategory = null;
	private Label noContestant = null;
	private TableView<Contestant> table = null;
	
	/**
	 * Constructs a new ContestantTable setting all the important variables and creating GUI elements.
	 */
	private ContestantTable() {
		this.c = Client.get();
		
		// Top panel
		catBox = new HBox(16);
		catBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(200);
		catChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentCat = newVal;
			displayTable();
		});
		catBox.getChildren().addAll(catLabel, catChoice);
		catBox.setPadding(new Insets(0,15,40,15));
		
		// Center
		noCategory = new Label("Please, select a category");
		noCategory.setAlignment(Pos.CENTER);
		noContestant = new Label("There are no contestants in the selected category");
		noContestant.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Creates the GUI table. Sets all the GUI-related properties. 
	 * The table is dynamically recreated each time the category is chosen, 
	 * because each category contains different disciplines.
	 */
	private void createTable() {
		if(currentCat == null) return;
		table = new TableView<>();

		TableColumn<Contestant, Integer> idCol = new TableColumn<>("Id");
		idCol.setMinWidth(30);
		idCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
		TableColumn<Contestant, String> fNameCol = new TableColumn<>("First Name");
		fNameCol.setMinWidth(100);
		fNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirstName()));
		TableColumn<Contestant, String> lNameCol = new TableColumn<>("Last Name");
		lNameCol.setMinWidth(100);
		lNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLastName()));
		table.getColumns().add(idCol);
		table.getColumns().add(fNameCol);
		table.getColumns().add(lNameCol);
		for(Discipline d : currentCat.getDisciplines()) {
			TableColumn<Contestant, Double> discCol = new TableColumn<>(d.getName());
			discCol.setMinWidth(50);
			discCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getScore(d)).asObject());
			table.getColumns().add(discCol);
		}
		TableColumn<Contestant, Double> totalCol = new TableColumn<>("Total score");
		totalCol.setMinWidth(60);
		totalCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotalScore()).asObject());
		table.getColumns().add(totalCol);
	}

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of ContestantTable
	 */
	protected static ContestantTable getInstance() {
		if(instance == null) {
			instance = new ContestantTable();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for choosing the category.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		int id = currentCat == null ? 0 : currentCat.getId();
		catChoice.setItems(FXCollections.observableArrayList(c.current.getCategories().values()));
		for(Category cat : catChoice.getItems()) {
			if(cat.getId() == id) {
				catChoice.getSelectionModel().select(cat);
			}
		}
		c.mainPanel.setTop(catBox);
	}
	
	/**
	 * Displays a table of all contestants depending on the category set
	 */
	private void displayTable() {
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
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
}
