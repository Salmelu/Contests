package cz.salmelu.contests.client;

import java.util.ArrayList;

import cz.salmelu.contests.model.Contestant;
import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.model.TeamContestant;
import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.PacketContestant;
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
 * A class holding elements for form fields and their respective labels to allow changing the contestant infos
 * @author salmelu
 */
final class EditContestant implements Displayable {
	
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

	/**
	 * Constructs a new object, used by {@link getInstance()} if an instance doesn't exist yet
	 */
	private EditContestant() {
		this.c = Client.get();
		dummy = new Team("No Team");
		
		topBox = new HBox(16);
		topBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(160);
		catChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentCat = newVal;
			updateCsList();
		});
		csLabel = new Label("Choose a contestant: ");
		csChoice = new ChoiceBox<>();
		csChoice.setPrefWidth(160);
		csChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentCs = newVal;
			fillFields();
		});
		deleteButton = new Button("Delete contestant");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(160);
		deleteButton.setOnAction(ae -> deleteContestant());
		topBox.setPadding(new Insets(0,15,40,15));
		topBox.getChildren().addAll(catLabel, catChoice, csLabel, csChoice, deleteButton);
		
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

		newButton.setOnAction(ae -> newContestant());
		updateButton.setOnAction(ae -> updateContestant());
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

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of EditContestant
	 */
	protected static EditContestant getInstance() {
		if(instance == null) {
			instance = new EditContestant();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for choosing the category and contestant.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		boolean selected = false;
		int id = currentCat == null ? 0 : currentCat.getId();
		catChoice.setItems(FXCollections.observableArrayList(c.current.getCategories().values()));
		for(Category cat : catChoice.getItems()) {
			if(cat.getId() == id) {
				catChoice.getSelectionModel().select(cat);
				selected = true;
			}
		}
		if(!selected) {
			currentCs = null;
		}
		
		c.mainPanel.setTop(topBox);
	}
	
	/**
	 * Updates a list of contestant to match the selected category. Called when a category is changed.
	 */
	private void updateCsList() {
		if(currentCat == null || c.current.getContestants(currentCat) == null) return;
		int id = currentCs == null ? 0 : currentCs.getId();
		csChoice.setItems(FXCollections.observableArrayList(c.current.getContestants(currentCat).values()));
		for(Contestant cs : csChoice.getItems()) {
			if(cs.getId() == id) {
				csChoice.getSelectionModel().select(cs);
			}
		}
	}

	/**
	 * Sets the field values. If there aren't any values to be set, empties the fields
	 */
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
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		fillFields();
		c.mainPanel.setCenter(gp);
	}

	/** 
	 * Handler called by the delete button. 
	 * Provides basic checks and if successful, starts a delete task.
	 */
	private void deleteContestant() {
		if(currentCat == null || currentCs == null) {
			ActionHandler.get().showErrorDialog("No contestant or category selected",
					"You have not chosen a correct contestant to delete.");
			return;
		}
		if(!ActionHandler.get().showPromptDialog("Are you sure?",
				"Do you really want to remove the contestant?")) {
			return;
		}
		TaskDelete td = new TaskDelete(PacketOrder.CONTESTANT_DELETE, c.current.getId(), 
				currentCat.getId(), currentCs.getId());		
		Thread t = new Thread(td);
		t.run();
		ActionHandler.get().showSuccessDialog("Contestant deleted", "Contestant " + currentCs.toString() + " was deleted.");
	}

	/** 
	 * Handler called by the new button. 
	 * Provides basic checks and if successful, starts a new task.
	 */
	private void newContestant() {
		PacketContestant pc = new PacketContestant();
		pc.fName = fName.getText();
		pc.lName = lName.getText();
		try {
			pc.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			ActionHandler.get().showErrorDialog("Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		if(cat.getSelectionModel().getSelectedItem() == null) {
			ActionHandler.get().showErrorDialog("Invalid value", "No category is selected.");
			return;
		}
		pc.catId = cat.getSelectionModel().getSelectedItem().getId();
		if(pc.fName == null || pc.fName.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid first name selected. Please enter a valid name.");
			return;
		}
		if(pc.lName == null || pc.lName.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid last name selected. Please enter a valid name.");
			return;
		}
		if(team.getSelectionModel().getSelectedItem().equals(dummy)) {
			pc.teamId = 0;
		}
		else {
			pc.teamId = team.getSelectionModel().getSelectedItem().getId();
			pc.tcId = team.getSelectionModel().getSelectedItem().getCategory().getId();
		}
		pc.id = 0;
		pc.conId = c.current.getId();
		TaskNewEdit<PacketContestant> tne = new TaskNewEdit<>(PacketOrder.CONTESTANT_EDIT, pc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("New contestant added", "You have successfully sent a request for a new contestant.");
	}

	/** 
	 * Handler called by the update button. 
	 * Provides basic checks and if successful, starts an update task.
	 */
	private void updateContestant() {
		if(currentCat == null || currentCs == null) {
			ActionHandler.get().showErrorDialog("No category or contestant selected", "You have not chosen a contestant and a category.");
			return;
		}
		PacketContestant pc = new PacketContestant();
		pc.fName = fName.getText();
		pc.lName = lName.getText();
		try {
			pc.bonus = Double.parseDouble(bonus.getText());
		}
		catch(NumberFormatException e) {
			ActionHandler.get().showErrorDialog("Invalid value", "Invalid numeric value in bonus field");
			return;
		}
		if(cat.getSelectionModel().getSelectedItem() == null) {
			ActionHandler.get().showErrorDialog("Invalid value", "No category is selected.");
			return;
		}
		pc.catId = cat.getSelectionModel().getSelectedItem().getId();
		pc.oldCatId = currentCs.getCategory().getId();
		if(pc.fName == null || pc.fName.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid first name selected. Please enter a valid name.");
			return;
		}
		if(pc.lName == null || pc.lName.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid last name selected. Please enter a valid name.");
			return;
		}
		if(team.getSelectionModel().getSelectedItem().equals(dummy)) {
			pc.teamId = 0;
		}
		else {
			pc.teamId = team.getSelectionModel().getSelectedItem().getId();
			pc.tcId = team.getSelectionModel().getSelectedItem().getCategory().getId();
		}
		pc.id = currentCs.getId();
		pc.conId = c.current.getId();
		TaskNewEdit<PacketContestant> tne = new TaskNewEdit<>(PacketOrder.CONTESTANT_EDIT, pc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("Contestant update requested", "You have successfully sent a request for a contestant update.");
	}
}
