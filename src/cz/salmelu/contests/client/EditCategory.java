package cz.salmelu.contests.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.controlsfx.control.CheckComboBox;

import cz.salmelu.contests.model.Discipline;
import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.CategoryPacket;
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

final class EditCategory {
	
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
	
	private EditCategory(Client c) {
		this.c = c;
		
		catBox = new HBox(16);
		catBox.setAlignment(Pos.CENTER);
		catLabel = new Label("Choose a category: ");
		catChoice = new ChoiceBox<>();
		catChoice.setPrefWidth(180);
		deleteButton = new Button("Delete category");
		deleteButton.setPadding(new Insets(5,5,5,5));
		deleteButton.setPrefWidth(200);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteCategory();
			}
		});
		catBox.setPadding(new Insets(0,15,40,15));
		catBox.getChildren().addAll(catLabel, catChoice, deleteButton);
		
		createTable();
	}
	
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
		
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				newCategory();
			}
		});
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateCategory();
			}
		});
		buttonBox.getChildren().add(newButton);
		buttonBox.getChildren().add(updateButton);
		buttonBox.setAlignment(Pos.CENTER);
		
		gp.add(new Label("Category name:"), 0, 0);
		gp.add(name, 1, 0);
		gp.add(new Label("Category disciplines:"), 0, 1);
		gp.add(buttonBox, 0, 2, 2, 1);
	}

	protected static EditCategory getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new EditCategory(c);
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
				fillFields();
			}
		});
		c.mainPanel.setTop(catBox);
	}

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
	
	private void updateItems() {
		// Need to recreate each time to avoid FX bug - warnings when loading css styles
		gp.getChildren().remove(discChoice);
		discChoice = new CheckComboBox<>();
		discChoice.setPrefWidth(200);
		discChoice.getItems().setAll(c.current.getDisciplines().values());
		gp.add(discChoice, 1, 1);
	}
	
	protected void displayAll() {
		if(c.current == null) return;
		updateItems();
		displayHeader();
		c.mainPanel.setCenter(gp);
	}

	private void deleteCategory() {
		if(currentCat == null) {
			c.ah.showErrorDialog(c, "No category selected", "You have not chosen a category to delete.");
			return;
		}
		DeleteTask dt = new DeleteTask(c.current.getId(), currentCat.getId());
		dt.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(dt);
		t.run();
		c.ah.showSuccessDialog(c, "Category deleted", "Category " + currentCat.getName() + " was deleted.");
	}
	
	private void newCategory() {
		CategoryPacket cp = new CategoryPacket();
		cp.name = name.getText();
		for(Discipline d : discChoice.getCheckModel().getCheckedItems()) {
			cp.disciplines.add(d.getId());
		}
		if(cp.name == null || cp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid category name selected. Please enter a name for the category.");
			return;
		}
		cp.id = 0;
		cp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(cp);
		net.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				c.handleMenuAction(MenuAction.MAIN_RELOAD_QUIET);
			}
		});
		
		Thread t = new Thread(net);
		t.run();
		c.ah.showSuccessDialog(c, "New category added", "You have successfully sent a request for a new category.");
	}
	
	private void updateCategory() {
		if(currentCat == null) {
			c.ah.showErrorDialog(c, "No category selected", "You have not chosen a category.");
			return;
		}
		CategoryPacket cp = new CategoryPacket();
		cp.name = name.getText();
		for(Discipline d : discChoice.getCheckModel().getCheckedItems()) {
			cp.disciplines.add(d.getId());
		}
		if(cp.name == null || cp.name.equals("")) {
			c.ah.showErrorDialog(c, "Field error", "An invalid category name selected. Please enter a name for the category.");
			return;
		}
		cp.id = currentCat.getId();
		cp.conId = c.current.getId();
		NewEditTask net = new NewEditTask(cp);
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
		c.ah.showSuccessDialog(c, "Category update requested", "You have successfully sent a request for a category update.");
	}
	
	private class NewEditTask extends Task<Boolean> {
		
		private CategoryPacket cp;
		
		protected NewEditTask(CategoryPacket cp) {
			this.cp = cp;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.CATEGORY_EDIT.toByte());
		        send.writeObject(cp);
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

		private int catId;
		private int conId;
		
		protected DeleteTask(int conId, int catId) {
			this.conId = conId;
			this.catId = catId;
		}
		
		@Override
		protected Boolean call() throws Exception {
			try {
				InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
		        Socket socket = new Socket();
		        socket.connect(addr);
		        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
		        send.writeByte(Packet.CATEGORY_DELETE.toByte());
		        send.writeInt(conId);
		        send.writeInt(catId);
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
