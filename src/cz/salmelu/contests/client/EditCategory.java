package cz.salmelu.contests.client;

import org.controlsfx.control.CheckComboBox;

import cz.salmelu.contests.model.Discipline;
import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.PacketCategory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * A class holding elements for form fields and their respective labels to allow changing the category infos
 * @author salmelu
 */
final class EditCategory implements Displayable {
	
	private Client c;
	private static EditCategory instance = null;
	
	private ChoiceBox<Category> catChoice = null;
	private Label catLabel = null;
	private HBox catBox = null;
	private Category currentCat = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;
	private CheckComboBox<Discipline> discChoice; 

	/**
	 * Constructs a new object, used by {@link getInstance()} if an instance doesn't exist yet
	 */
	private EditCategory() {
		this.c = Client.get();
		
		catBox = new HBox(16);
		catBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(180);
		catChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentCat = newVal;
			fillFields();
		});
		deleteButton = new Button("Delete category");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(ae -> deleteCategory());
		catBox.setPadding(new Insets(0,15,40,15));
		catBox.getChildren().addAll(catLabel, catChoice, deleteButton);
		
		createTable();
	}
	
	/**
	 * Creates a gridpane containing all the fields and their labels
	 */
	private void createTable() {
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
		
		name = new TextField();
		name.setPromptText("Enter category name");
		name.setPrefWidth(200);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New category");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(200);
		Button updateButton = new Button("Update category");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(200);
		
		newButton.setOnAction(ae -> newCategory());
		updateButton.setOnAction(ae -> updateCategory());
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Category name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(new Label("Category disciplines:"), 0, 1);
		gp.add(buttonBox, 0, 2, 2, 1);
	}

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of EditCategory
	 */
	protected static EditCategory getInstance() {
		if(instance == null) {
			instance = new EditCategory();
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
	 * Sets the field values. If there aren't any values to be set, empties the fields
	 */
	private void fillFields() {
		if(currentCat == null || !c.current.hasCategory(currentCat.getId())) {
			name.setText("");
			discChoice.getCheckModel().clearChecks();
		}
		else {
			name.setText(currentCat.getName());
			discChoice.getCheckModel().clearChecks();
			for(Discipline d : currentCat.getDisciplines()) {
				discChoice.getCheckModel().check(d);
			}
		}
	}
	
	/**
	 * Called, when the updated category is changed to empty the combobox
	 */
	private void updateItems() {
		// Need to recreate each time to avoid FX bug - warnings when loading css styles
		gp.getChildren().remove(discChoice);
		discChoice = new CheckComboBox<>();
		discChoice.setPrefWidth(200);
		discChoice.getItems().setAll(c.current.getDisciplines().values());
		gp.add(discChoice, 1, 1);
	}
	
	public void displayAll() {
		if(c.current == null) return;
		updateItems();
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	/** 
	 * Handler called by the delete button. 
	 * Provides basic checks and if successful, starts a delete task.
	 */
	private void deleteCategory() {
		if(currentCat == null) {
			ActionHandler.get().showErrorDialog("No category selected", "You have not chosen a category to delete.");
			return;
		}
		if(!ActionHandler.get().showPromptDialog("Are you sure?",
				"Do you really want to remove the category and all the contestants in it?")) {
			return;
		}
		TaskDelete td = new TaskDelete(PacketOrder.CATEGORY_DELETE, c.current.getId(), currentCat.getId());
		Thread t = new Thread(td);
		t.run();
		ActionHandler.get().showSuccessDialog("Category deleted", "Category " + currentCat.getName() + " was deleted.");
	}

	/** 
	 * Handler called by the new button. 
	 * Provides basic checks and if successful, starts a new task.
	 */
	private void newCategory() {
		PacketCategory pc = new PacketCategory();
		pc.name = name.getText();
		for(Discipline d : discChoice.getCheckModel().getCheckedItems()) {
			pc.disciplines.add(d.getId());
		}
		if(pc.name == null || pc.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid category name selected. Please enter a name for the category.");
			return;
		}
		pc.id = 0;
		pc.conId = c.current.getId();
		TaskNewEdit<PacketCategory> tne = new TaskNewEdit<>(PacketOrder.CATEGORY_EDIT, pc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("New category added", "You have successfully sent a request for a new category.");
	}

	/** 
	 * Handler called by the update button. 
	 * Provides basic checks and if successful, starts an update task.
	 */
	private void updateCategory() {
		if(currentCat == null) {
			ActionHandler.get().showErrorDialog("No category selected", "You have not chosen a category.");
			return;
		}
		PacketCategory pc = new PacketCategory();
		pc.name = name.getText();
		for(Discipline d : discChoice.getCheckModel().getCheckedItems()) {
			pc.disciplines.add(d.getId());
		}
		if(pc.name == null || pc.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid category name selected. Please enter a name for the category.");
			return;
		}
		pc.id = currentCat.getId();
		pc.conId = c.current.getId();
		TaskNewEdit<PacketCategory> tne = new TaskNewEdit<>(PacketOrder.CATEGORY_EDIT, pc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("Category update requested", "You have successfully sent a request for a category update.");
	}
}