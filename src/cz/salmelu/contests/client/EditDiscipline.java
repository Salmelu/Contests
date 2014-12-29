package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cz.salmelu.contests.model.Discipline;
import cz.salmelu.contests.net.DisciplinePacket;
import cz.salmelu.contests.net.Packet;
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

final class EditDiscipline {
	
	private Client c;
	private static EditDiscipline instance = null;
	
	private ChoiceBox<Discipline> discChoice = null;
	private Label discLabel = null;
	private HBox discBox = null;
	private Discipline currentDisc = null;
	private Button deleteButton = null;
	
	private GridPane gp = null;
	private TextField name = null;
	
	private EditDiscipline(Client c) {
		this.c = c;
		
		discBox = new HBox(16);
		discBox.setAlignment(Pos.CENTER);
		discLabel = new Label("Choose a discipline: ");
		discChoice = new ChoiceBox<>();
		discChoice.setPrefWidth(180);
		deleteButton = new Button("Delete discipline");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteDiscipline();
			}
		});
		discBox.setPadding(new Insets(0,15,40,15));
		discBox.getChildren().addAll(discLabel, discChoice, deleteButton);
		
		createTable();
	}
	
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
		
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				newDiscipline();
			}
		});
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateDiscipline();
			}
		});
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Contest name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(buttonBox, 0, 1, 2, 1);
	}

	protected static EditDiscipline getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new EditDiscipline(c);
	}
	
	protected void displayHeader() {
		int id = currentDisc == null ? 0 : currentDisc.getId();
		discChoice.setItems(FXCollections.observableArrayList(new ArrayList<Discipline>(c.current.getDisciplines().values())));
		for(Discipline d : discChoice.getItems()) {
			if(d.getId() == id) {
				discChoice.getSelectionModel().select(d);
			}
		}
		discChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Discipline>() {
			@Override
			public void changed(ObservableValue<? extends Discipline> arg0, Discipline arg1,
					Discipline arg2) {
				currentDisc = arg2;
				fillFields();
			}
		});
		c.mainPanel.setTop(discBox);
	}

	private void fillFields() {
		if(currentDisc == null || !c.current.hasDiscipline(currentDisc.getId())) {
			name.setText("");
		}
		else {
			name.setText(currentDisc.getName());
		}
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	private void deleteDiscipline() {
		if(currentDisc == null) {
			c.ah.showErrorDialog(c, "No discipline selected", "You have not chosen a discipline to delete.");
			return;
		}
		DeleteTask dt = new DeleteTask(c.current.getId(), currentDisc.getId());
		dt.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(dt);
		t.run();
		c.ah.showSuccessDialog(c, "Discipline deleted", "Discipline " + currentDisc.getName() + " was deleted.");
	}
	
	private void newDiscipline() {
		DisciplinePacket dp = new DisciplinePacket();
		dp.name = name.getText();
		if(dp.name == null || dp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid discipline name selected. Please enter a name for the discipline.");
			return;
		}
		dp.id = 0;
		dp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(dp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		System.out.println("test1");
		Thread t = new Thread(net);
		t.run();
		System.out.println("test2");
		c.ah.showSuccessDialog(c, "New discipline added", "You have successfully sent a request for a new discipline.");
		System.out.println("test3");
	}
	
	private void updateDiscipline() {
		if(currentDisc == null) {
			c.ah.showErrorDialog(c, "No discipline selected", "You have not chosen a discipline.");
			return;
		}
		DisciplinePacket dp = new DisciplinePacket();
		dp.name = name.getText();
		if(dp.name == null || dp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid discipline name selected. Please enter a name for the discipline.");
			return;
		}
		dp.conId = c.current.getId();
		dp.id = currentDisc.getId();
		NewEditTask net = new NewEditTask(dp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				if(net.getValue()) {
					c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
				}
				else {
					c.ah.showConnectionError(c);
				}
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "Discipline update requested", "You have successfully sent a request for a discipline update.");
	}
	
	private class NewEditTask extends Task<Boolean> {
		
		private DisciplinePacket dp;
		
		protected NewEditTask(DisciplinePacket dp) {
			this.dp = dp;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.DISCIPLINE_EDIT.toByte());
		        send.writeObject(dp);
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
	
	private class DeleteTask extends Task<Boolean> {

		private int discId;
		private int conId;
		
		protected DeleteTask(int conId, int discId) {
			this.conId = conId;
			this.discId = discId;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.DISCIPLINE_DELETE.toByte());
		        send.writeInt(conId);
		        send.writeInt(discId);
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
