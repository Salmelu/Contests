package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.UpdateScorePacket;

class CategoryScore {
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
	
	private CategoryScore(Client c) {
		this.c = c;
		
		// Top panel
		catBox = new HBox();
		catBox.setAlignment(Pos.TOP_LEFT);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catBox.getChildren().addAll(catLabel, catChoice);
		catBox.setPadding(new Insets(0,15,15,15));
		
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
		scoreFields = new HashMap<>();
	}
	
	protected void displayHeader() {
		catChoice.setItems(FXCollections.observableArrayList(new ArrayList<Category>(c.current.getCategories().values())));
		if(catChoice.getItems().contains(currentCat)) {
			catChoice.getSelectionModel().select(currentCat);
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
				tf.setText(String.valueOf(con.getScore(disc)));
				scoreFields.get(con).put(disc, tf);
				table.add(tf, col, row);
				col++;
			}
			row++;
		}
		Button update = new Button("Update score");
		update.setAlignment(Pos.CENTER);
		table.add(update, 0, row, currentCat.getDisciplines().size()+2, 1);
		update.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateScore();
			}
		});
		c.mainPanel.setCenter(table);
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		displayTable();
	}
	
	private void updateScore() {
		ArrayList<UpdateScorePacket> updatePackets = new ArrayList<>();
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
						updatePackets.add(new UpdateScorePacket(currentCat.getId(), disc.getId(), con.getId(), score));
						updateScores.get(con).put(disc, score);
					}
				}
			}
		}
		catch (NumberFormatException e) {
			c.ah.showErrorDialog(c, "Error parsing fields", 
					"There was an error while parsing the values in the text field. Please verify that those are valid numbers.");
			return;
		} 
		catch (NoSuchFieldException e) {
			c.ah.showErrorDialog(c, "Field error", 
					"The program was unable to find the text fields. Please reload the list.");
			return;
		}
		for(Entry<Contestant, HashMap<Discipline,Double>> e1 : updateScores.entrySet()) {
			for(Entry<Discipline, Double> e2 : e1.getValue().entrySet()) {
				e1.getKey().setScore(e2.getKey(), e2.getValue());
			}
		}
		UpdateRequest ur = new UpdateRequest(updatePackets, c.current.getId());
		ur.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			public void handle(WorkerStateEvent arg0) {
				if(!ur.getValue()) {
					c.ah.showErrorDialog(c, "Error updating score", 
							"Server didn't accept the update request. "
							+ "The possible reason is that some of the contestants or disciplines were removed. "
							+ "Please, reload the contest data and try the update again.");
				}
			}
		});
		Thread t = new Thread(ur);
		t.run();
		c.ah.showSuccessDialog(c, "Data updated.", "Score data was updated and a request to server update was sent");
	}
	
	protected static CategoryScore getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new CategoryScore(c);
	}
	
	private class UpdateRequest extends Task<Boolean> {
		
		private ArrayList<UpdateScorePacket> updates = null;
		private int conId;
		
		public UpdateRequest(ArrayList<UpdateScorePacket> updates, int conId) {
			this.updates = updates;
			this.conId = conId;
		}

		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.SCORE_UPDATE.toByte());
		        send.writeInt(conId);
		        send.writeInt(updates.size());
		        for(UpdateScorePacket usp : updates) {
		        	send.writeObject(usp);
		        }
		        send.flush();
		        boolean ret = get.readBoolean();
		        socket.close();
		        if(!ret) {
					return false;
		        }
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
