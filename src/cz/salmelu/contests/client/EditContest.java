package cz.salmelu.contests.client;

import cz.salmelu.contests.model.ContestInfo;
import cz.salmelu.contests.net.PacketContest;
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
 * A class holding elements for form fields and their respective labels to allow changing the contest infos
 * @author salmelu
 */
final class EditContest implements Displayable {
	
	private Client c;
	private static EditContest instance = null;
	
	private ChoiceBox<ContestInfo> contestChoice = null;
	private Label contestLabel = null;
	private HBox contestBox = null;
	private ContestInfo currentContest = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;

	/**
	 * Constructs a new object, used by {@link #getInstance()} if an instance doesn't exist yet
	 */
	private EditContest() {
		this.c = Client.get();
		
		contestBox = new HBox(16);
		contestBox.setAlignment(Pos.CENTER);
		contestLabel = new Label("Choose a contest: ");
		contestChoice = new ChoiceBox<>();
		contestChoice.setPrefWidth(180);
		contestChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentContest = newVal;
			fillFields();
		});
		deleteButton = new Button("Delete contest");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(ae -> deleteContest());
		contestBox.getChildren().addAll(contestLabel, contestChoice, deleteButton);
		contestBox.setPadding(new Insets(0,15,40,15));
		
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
		name.setPromptText("Enter contest name");
		name.setPrefWidth(200);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New contest");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(200);
		Button updateButton = new Button("Update contest");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(200);
		
		newButton.setOnAction(ae -> newContest());
		updateButton.setOnAction(ae -> updateContest());
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
	 * @return an instance of EditContest
	 */
	protected static EditContest getInstance() {
		if(instance == null) {
			instance = new EditContest();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for choosing the contest.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		int id = currentContest == null ? 0 : currentContest.getId();
		contestChoice.setItems(FXCollections.observableArrayList(c.contests.values()));
		for(ContestInfo ci : contestChoice.getItems()) {
			if(ci.getId() == id) {
				contestChoice.getSelectionModel().select(ci);
			}
		}
		c.mainPanel.setTop(contestBox);
	}

	/**
	 * Sets the field values. If there aren't any values to be set, empties the fields
	 */
	private void fillFields() {
		if(currentContest == null || !c.contests.containsValue(currentContest)) {
			name.setText("");
		}
		else {
			name.setText(currentContest.getName());
		}
	}
	
	public void displayAll() {
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	/** 
	 * Handler called by the delete button. 
	 * Provides basic checks and if successful, starts a delete task.
	 */
	private void deleteContest() {
		if(currentContest == null) {
			ActionHandler.get().showErrorDialog("No contest selected", "You have not chosen a contest.");
			return;
		}
		if(!ActionHandler.get().showPromptDialog("Are you sure?",
				"Do you really want to remove the contest and all the related data?")) {
			return;
		}
		TaskDelete td = new TaskDelete(PacketOrder.CONTEST_DELETE, currentContest.getId());
		Thread t = new Thread(td);
		t.run();
		ActionHandler.get().showSuccessDialog("Contest deleted", "Contest " + currentContest.getName() + " was deleted.");
	}

	/** 
	 * Handler called by the new button. 
	 * Provides basic checks and if successful, starts a new task.
	 */
	private void newContest() {
		PacketContest pc = new PacketContest();
		pc.name = name.getText();
		if(pc.name == null || pc.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid contest name selected. Please enter a name for the contest.");
			return;
		}
		pc.id = 0;
		TaskNewEdit<PacketContest> tne = new TaskNewEdit<>(PacketOrder.CONTEST_EDIT, pc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("New contest added", "You have successfully sent a request for a new contest.");
	}

	/** 
	 * Handler called by the update button. 
	 * Provides basic checks and if successful, starts an update task.
	 */
	private void updateContest() {
		if(currentContest == null) {
			ActionHandler.get().showErrorDialog("No contest selected", "You have not chosen a contest.");
			return;
		}
		PacketContest pc = new PacketContest();
		pc.name = name.getText();
		if(pc.name == null || pc.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid contest name selected. Please enter a name for the contest.");
			return;
		}
		pc.id = currentContest.getId();
		TaskNewEdit<PacketContest> tne = new TaskNewEdit<>(PacketOrder.CONTEST_EDIT, pc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("Contest update requested", "You have successfully sent a request for a contest update.");
	}
}
