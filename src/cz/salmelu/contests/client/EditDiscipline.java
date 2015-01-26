package cz.salmelu.contests.client;

import cz.salmelu.contests.model.Discipline;
import cz.salmelu.contests.net.PacketDiscipline;
import cz.salmelu.contests.net.PacketOrder;
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
 * A class holding elements for form fields and their respective labels to allow changing the discipline infos
 * @author salmelu
 */
final class EditDiscipline implements Displayable {
	
	private Client c;
	private static EditDiscipline instance = null;
	
	private ChoiceBox<Discipline> discChoice = null;
	private Label discLabel = null;
	private HBox discBox = null;
	private Discipline currentDisc = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;

	/**
	 * Constructs a new object, used by {@link getInstance()} if an instance doesn't exist yet
	 */
	private EditDiscipline() {
		this.c = Client.get();
		
		discBox = new HBox(16);
		discBox.setAlignment(Pos.CENTER);
		discLabel = new Label("Choose a discipline: ");
		discChoice = new ChoiceBox<>();
		discChoice.setPrefWidth(180);
		discChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentDisc = newVal;
			fillFields();
		});
		deleteButton = new Button("Delete discipline");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(ae -> deleteDiscipline());
		discBox.setPadding(new Insets(0,15,40,15));
		discBox.getChildren().addAll(discLabel, discChoice, deleteButton);
		
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
		name.setPromptText("Enter discipline name");
		name.setPrefWidth(200);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New discipline");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(200);
		Button updateButton = new Button("Update discipline");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(200);
		
		newButton.setOnAction(ae -> newDiscipline());
		updateButton.setOnAction(ae -> updateDiscipline());
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Contest name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(buttonBox, 0, 1, 2, 1);
	}

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of EditDiscipline
	 */
	protected static EditDiscipline getInstance() {
		if(instance == null) {
			instance = new EditDiscipline();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for choosing the discipline.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		int id = currentDisc == null ? 0 : currentDisc.getId();
		discChoice.setItems(FXCollections.observableArrayList(c.current.getDisciplines().values()));
		for(Discipline d : discChoice.getItems()) {
			if(d.getId() == id) {
				discChoice.getSelectionModel().select(d);
			}
		}
		c.mainPanel.setTop(discBox);
	}

	/**
	 * Sets the field values. If there aren't any values to be set, empties the fields
	 */
	private void fillFields() {
		if(currentDisc == null || !c.current.hasDiscipline(currentDisc.getId())) {
			name.setText("");
		}
		else {
			name.setText(currentDisc.getName());
		}
	}
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	/** 
	 * Handler called by the delete button. 
	 * Provides basic checks and if successful, starts a delete task.
	 */
	private void deleteDiscipline() {
		if(currentDisc == null) {
			ActionHandler.get().showErrorDialog("No discipline selected", "You have not chosen a discipline to delete.");
			return;
		}
		if(!ActionHandler.get().showPromptDialog("Are you sure?",
				"Do you really want to remove the discipline and all the scores associated with it?")) {
			return;
		}
		TaskDelete td = new TaskDelete(PacketOrder.DISCIPLINE_DELETE, c.current.getId(), currentDisc.getId());
		Thread t = new Thread(td);
		t.run();
		ActionHandler.get().showSuccessDialog("Discipline deleted", "Discipline " + currentDisc.getName() + " was deleted.");
	}

	/** 
	 * Handler called by the new button. 
	 * Provides basic checks and if successful, starts a new task.
	 */
	private void newDiscipline() {
		PacketDiscipline pd = new PacketDiscipline();
		pd.name = name.getText();
		if(pd.name == null || pd.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid discipline name selected. Please enter a name for the discipline.");
			return;
		}
		pd.id = 0;
		pd.conId = c.current.getId();
		TaskNewEdit<PacketDiscipline> net = new TaskNewEdit<>(PacketOrder.DISCIPLINE_EDIT, pd);		
		Thread t = new Thread(net);
		t.run();
		ActionHandler.get().showSuccessDialog("New discipline added", "You have successfully sent a request for a new discipline.");
	}

	/** 
	 * Handler called by the update button. 
	 * Provides basic checks and if successful, starts an update task.
	 */
	private void updateDiscipline() {
		if(currentDisc == null) {
			ActionHandler.get().showErrorDialog("No discipline selected", "You have not chosen a discipline.");
			return;
		}
		PacketDiscipline pd = new PacketDiscipline();
		pd.name = name.getText();
		if(pd.name == null || pd.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid discipline name selected. Please enter a name for the discipline.");
			return;
		}
		pd.conId = c.current.getId();
		pd.id = currentDisc.getId();
		TaskNewEdit<PacketDiscipline> net = new TaskNewEdit<>(PacketOrder.DISCIPLINE_EDIT, pd);		
		Thread t = new Thread(net);
		t.run();
		ActionHandler.get().showSuccessDialog("Discipline update requested", "You have successfully sent a request for a discipline update.");
	}
}
