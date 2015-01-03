package cz.salmelu.contests.client;

import cz.salmelu.contests.model.ScoreMode;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.PacketTeamCategory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

final class EditTeamCategory implements Displayable {
	
	private Client c;
	private static EditTeamCategory instance = null;
	
	private ChoiceBox<TeamCategory> tcChoice = null;
	private Label tcLabel = null;
	private HBox tcBox = null;
	private TeamCategory currentTc = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;
	private ChoiceBox<ScoreMode> sm = null;
	
	private EditTeamCategory() {
		this.c = Client.get();
		
		tcBox = new HBox(16);
		tcBox.setAlignment(Pos.CENTER);
		tcLabel = new Label("Choose a team category: ");
		tcChoice = new ChoiceBox<>();
		tcChoice.setPrefWidth(180);
		tcChoice.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
			currentTc = newVal;
			fillFields();
		});
		deleteButton = new Button("Delete team category");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(ae -> deleteTeamCategory());
		tcBox.setPadding(new Insets(0,15,40,15));
		tcBox.getChildren().addAll(tcLabel, tcChoice, deleteButton);
		
		createTable();
	}
	
	private void createTable() {
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
		
		name = new TextField();
		name.setPromptText("Enter team category name");
		name.setPrefWidth(200);

		sm = new ChoiceBox<>();
		sm.setItems(FXCollections.observableArrayList(ScoreMode.values()));
		sm.setPrefWidth(200);
		
		HBox buttonBox = new HBox(10);
		Button newButton = new Button("New team category");
		newButton.setPadding(new Insets(5,5,5,5));
		newButton.setPrefWidth(200);
		Button updateButton = new Button("Update team category");
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(200);
		
		newButton.setOnAction(ae -> newTeamCategory());
		updateButton.setOnAction(ae -> updateTeamCategory());
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Team category name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(new Label("Team category score mode:"), 0, 1);
		gp.add(sm, 1, 1);
		gp.add(buttonBox, 0, 2, 2, 1);
	}

	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of EditTeamCategory
	 */
	protected static EditTeamCategory getInstance() {
		if(instance == null) {
			instance = new EditTeamCategory();
		}
		return instance;
	}
	
	/**
	 * Displays a header with the ChoiceBox for chosing the team category.
	 * Also resets the list of items, because it may have changed in the meantime
	 */
	private void displayHeader() {
		int id = currentTc == null ? 0 : currentTc.getId();
		tcChoice.setItems(FXCollections.observableArrayList(c.current.getTeamCategories().values()));
		for(TeamCategory tc : tcChoice.getItems()) {
			if(tc.getId() == id) {
				tcChoice.getSelectionModel().select(tc);
			}
		}
		c.mainPanel.setTop(tcBox);
	}

	private void fillFields() {
		if(currentTc == null || !c.current.hasTeamCategory(currentTc.getId())) {
			name.setText("");
		}
		else {
			name.setText(currentTc.getName());
			sm.getSelectionModel().select(currentTc.getScoreMode());
		}
	}
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	private void deleteTeamCategory() {
		if(currentTc == null) {
			ActionHandler.get().showErrorDialog("No team category selected", "You have not chosen a team category to delete.");
			return;
		}
		if(!ActionHandler.get().showPromptDialog("Are you sure?",
				"Do you really want to remove the team category and all the teams in it?")) {
			return;
		}
		TaskDelete td = new TaskDelete(PacketOrder.TCATEGORY_DELETE, c.current.getId(), currentTc.getId());
		Thread t = new Thread(td);
		t.run();
		ActionHandler.get().showSuccessDialog("Team category deleted", "Team category " + currentTc.getName() + " was deleted.");
	}
	
	private void newTeamCategory() {
		PacketTeamCategory ptc = new PacketTeamCategory();
		ptc.name = name.getText();
		ptc.sm = sm.getSelectionModel().getSelectedItem();
		if(ptc.name == null || ptc.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid team category name selected. Please enter a name for the team category.");
			return;
		}
		if(ptc.sm == null) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid score mode selected. Please enter a score mode for the team category.");
			return;
		}
		ptc.id = 0;
		ptc.conId = c.current.getId();
		TaskNewEdit<PacketTeamCategory> tne = new TaskNewEdit<>(PacketOrder.TCATEGORY_EDIT, ptc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("New team category added", "You have successfully sent a request for a new team category.");
	}
	
	private void updateTeamCategory() {
		if(currentTc == null) {
			ActionHandler.get().showErrorDialog("No team category selected", "You have not chosen a team category.");
			return;
		}
		PacketTeamCategory ptc = new PacketTeamCategory();
		ptc.name = name.getText();
		ptc.sm = sm.getSelectionModel().getSelectedItem();
		if(ptc.name == null || ptc.name.equals("")) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid team category name selected. Please enter a name for the team category.");
			return;
		}
		if(ptc.sm == null) {
			ActionHandler.get().showErrorDialog("Field error", "An invalid score mode selected. Please enter a score mode for the team category.");
			return;
		}
		ptc.conId = c.current.getId();
		ptc.id = currentTc.getId();
		TaskNewEdit<PacketTeamCategory> tne = new TaskNewEdit<>(PacketOrder.TCATEGORY_EDIT, ptc);
		Thread t = new Thread(tne);
		t.run();
		ActionHandler.get().showSuccessDialog("Team category update requested", "You have successfully sent a request for a team category update.");
	}
}
