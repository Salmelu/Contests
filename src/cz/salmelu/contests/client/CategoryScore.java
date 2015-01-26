package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.model.Contestant;
import cz.salmelu.contests.model.Discipline;
import cz.salmelu.contests.net.PacketOrder;
import cz.salmelu.contests.net.PacketUpdateScore;
import cz.salmelu.contests.net.ServerError;

/**
 * Holds a GUI panel used for updating contestants' score.
 * @author salmelu
 */
final class CategoryScore implements Displayable {
	
	private Client c = null;
	private static CategoryScore instance = null;
	private Category currentCat = null;
	
	private HBox catBox = null;
	private Label catLabel = null;
	private ChoiceBox<Category> catChoice = null;

	private Label noCategory = null;
	private Label noContestants = null;
	private Label fName = null;
	private Label lName = null;
	private GridPane table = null;
	private Map<Contestant, Map<Discipline,TextField>> scoreFields;
	
	private Button updateButton = null;
	
	/**
	 * Constructs all the GUI components for score editing table.
	 */
	private CategoryScore() {
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
		
		// Center labels
		noCategory = new Label("Please, select a category");
		noCategory.setAlignment(Pos.CENTER);
		noContestants = new Label("There are no contestants in the selected category");
		noContestants.setAlignment(Pos.CENTER);
		fName = new Label("First name");
		fName.setAlignment(Pos.CENTER);
		lName = new Label("Last name");
		lName.setAlignment(Pos.CENTER);
		
		// Center tables
		table = new GridPane();
		table.setHgap(8);
		table.setVgap(12);
		table.setAlignment(Pos.CENTER);
		scoreFields = new HashMap<>();
		
		updateButton = new Button("Update score");
		updateButton.setAlignment(Pos.CENTER);
		updateButton.setPadding(new Insets(5,5,5,5));
		updateButton.setPrefWidth(200);
		updateButton.setOnAction(event -> updateScore());
	}
	
	/**
	 * Displays the table header, containing a choicebox with for chosing a category.
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
	 * Displays a table depending on the category set. It shows all the textfields for editing the score.
	 */
	private void displayTable() {
		if(currentCat == null) {
			c.mainPanel.setCenter(noCategory);
			return;
		}
		if(c.current.getContestants(currentCat) == null || c.current.getContestants(currentCat).isEmpty()) {
			c.mainPanel.setCenter(noContestants);
			return;
		}
		
		table.getChildren().clear();
		table.add(fName, 0, 0);
		table.add(lName, 1, 0);
		int index = 2;
		for(Discipline disc : currentCat.getDisciplines()) {
			table.add(new Label(disc.getName()), index++, 0);
		}
		int row = 1;
		for(Contestant con : c.current.getContestants(currentCat).values()) {
			table.add(new Label(con.getFirstName()), 0, row);
			table.add(new Label(con.getLastName()), 1, row);
			scoreFields.put(con, new HashMap<>());
			int col = 2;
			for(Discipline disc : currentCat.getDisciplines()) {
				TextField tf = new TextField();
				tf.setPrefWidth(80);
				tf.setText(String.valueOf(con.getScore(disc)));
				scoreFields.get(con).put(disc, tf);
				table.add(tf, col, row);
				col++;
			}
			row++;
		}
		table.add(updateButton, 0, row, currentCat.getDisciplines().size()+2, 1);
		c.mainPanel.setCenter(table);
	}
	
	public void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
	
	/**
	 * This method is called on clicking update button. It collects all the data from the textfields and 
	 * sends a request to the server.
	 */
	private void updateScore() {
		ArrayList<PacketUpdateScore> updatePackets = new ArrayList<>();
		HashMap<Contestant, HashMap<Discipline, Double>> updateScores = new HashMap<>();
		try {
			for(Contestant con : c.current.getContestants(currentCat).values()) {
				updateScores.put(con, new HashMap<>());
				for(Discipline disc : currentCat.getDisciplines()) {
					if(!scoreFields.containsKey(con) || !scoreFields.get(con).containsKey(disc)) {
						throw new NoSuchFieldException();
					}
					double score = Double.parseDouble(scoreFields.get(con).get(disc).getText());
					if(score != con.getScore(disc)) {
						updatePackets.add(new PacketUpdateScore(currentCat.getId(), disc.getId(), con.getId(), score));
						updateScores.get(con).put(disc, score);
					}
				}
			}
		}
		catch (NumberFormatException e) {
			ActionHandler.get().showErrorDialog("Error parsing fields", 
					"There was an error while parsing the values in the text field. Please verify that those are valid numbers.");
			return;
		} 
		catch (NoSuchFieldException e) {
			ActionHandler.get().showErrorDialog("Field error", 
					"The program was unable to find the text fields. Please reload the list.");
			return;
		}
		for(Entry<Contestant, HashMap<Discipline,Double>> e1 : updateScores.entrySet()) {
			for(Entry<Discipline, Double> e2 : e1.getValue().entrySet()) {
				e1.getKey().setScore(e2.getKey(), e2.getValue());
			}
		}
		UpdateRequest ur = new UpdateRequest(updatePackets, c.current.getId());
		ur.setOnSucceeded(event -> {
			if(!ur.getValue()) {
				ActionHandler.get().handleServerError(ur.getServerError());
			}
		});
		Thread t = new Thread(ur);
		t.run();
		ActionHandler.get().showSuccessDialog("Data updated.",
				"Score data was updated and a request to server update was sent");
	}
	
	/**
	 * Implementation of the singleton design pattern. Returns an instance of this class.
	 * It also creates a new instance, if no instance was previously created.
	 * @return an instance of CategoryScore
	 */
	protected static CategoryScore getInstance() {
		if(instance == null) {
			instance = new CategoryScore();
		}
		return instance;
	}
	
	/**
	 * A task run when the client wants to send update score packets to the server.
	 * @author salmelu
	 */
	private class UpdateRequest extends TaskContest {
		
		private ArrayList<PacketUpdateScore> updates = null;
		private int conId;
		
		/**
		 * Constructs a new Update request.
		 * @param updates List of PacketUpdateScore instances
		 * @param conId id of the modified contest
		 */
		public UpdateRequest(ArrayList<PacketUpdateScore> updates, int conId) {
			this.updates = updates;
			this.conId = conId;
		}

		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Config.INET_ADDR, Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(PacketOrder.SCORE_UPDATE.toByte());
		        send.writeInt(conId);
		        send.writeInt(updates.size());
		        for(PacketUpdateScore usp : updates) {
		        	send.writeObject(usp);
		        }
		        send.flush();
		        boolean ret = get.readBoolean();
		        if(!ret) {
		        	setServerError((ServerError) get.readObject());
			        socket.close();
					return false;
		        }
		        socket.close();
		        return true;
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}
