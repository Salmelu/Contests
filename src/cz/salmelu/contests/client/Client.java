package cz.salmelu.contests.client;

import java.util.HashMap;

import cz.salmelu.contests.model.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main class owning all the GUI components responsible for creating the main window,
 *  menu buttons and holding the main BorderPane and Stage.<br>
 * Also holds the references to current loaded contest list and contest.
 * @author salmelu
 */
public class Client extends Application {

	/** Implementation of Singleton */
	private static Client instance;
	
	/** List of contest infos, the key is its name */
	protected HashMap<String, ContestInfo> contests = null;
	/** The main box containing the mainPanel and the menu */
	protected VBox mainBox;
	/** The main stage */
	protected Stage mainStage;
	/** The main BorderPane containing all the GUI components */
	protected BorderPane mainPanel;
	/** The current, loaded contest */
	protected Contest current = null;
	/** The current selected menu action */
	protected MenuAction currentMenu = null;
	
	/**
	 * Gets a current instance of this client.
	 * @return an instance of client
	 */
	public static Client get() {
		return instance;
	}
	
	/**
	 * Checks, if there is a contest already selected.
	 * @return true, if there is a selected and loaded contest
	 */
	public static boolean contestSelected() {
		return !(instance.current == null);
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		instance = this;
		mainStage = arg0;
		
		mainBox = new VBox();
		mainPanel = new BorderPane();
		Label defLabel = new Label("Choose a contest from main menu, or add a new one");
		defLabel.setAlignment(Pos.CENTER);
		mainPanel.setCenter(defLabel);
		mainPanel.setPadding(new Insets(40,40,40,40));
		mainBox.getChildren().addAll(loadMenus(), mainPanel);
				
		Scene mainScene = new Scene(mainBox, 800, 600);
		arg0.setScene(mainScene);
		
		ActionHandler.get().reloadContestList(false);
		arg0.show();
	}
	
	/**
	 * Creates a MenuBar with all the menus and adds the listeners to them.
	 * @return a MenuBar with all menu options
	 */
	private MenuBar loadMenus() {
		MenuBar mbar = new MenuBar();
		
		Menu main = new Menu("Main");
		Menu show = new Menu("Show");
		Menu score = new Menu("Score");
		Menu edit = new Menu("Edit");
		
		MenuItem imain1 = new MenuItem("Contests");
		MenuItem imain2 = new MenuItem("Reload");
		MenuItem imain3 = new MenuItem("Exit");

		MenuItem ishow1 = new MenuItem("Contestants");
		MenuItem ishow2 = new MenuItem("Teams");
		MenuItem ishow3 = new MenuItem("Team Detail");

		MenuItem iscore1 = new MenuItem("Update Category");

		MenuItem iedit1 = new MenuItem("Update/New Contest");
		MenuItem iedit2 = new MenuItem("Update/New Discipline");
		MenuItem iedit3 = new MenuItem("Update/New Team Category");
		MenuItem iedit4 = new MenuItem("Update/New Category");
		MenuItem iedit5 = new MenuItem("Update/New Team");
		MenuItem iedit6 = new MenuItem("Update/New Contestant");
		
		imain1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.MAIN_CONTESTS);
			}
		});
		imain2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.MAIN_RELOAD);
			}
		});
		imain3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.MAIN_EXIT);
			}
		});

		ishow1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.SHOW_CONTESTANTS);
			}
		});
		ishow2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.SHOW_TEAMS);
			}
		});
		ishow3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.SHOW_ONE_TEAM);
			}
		});
		iscore1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.SCORE_CATEGORY);
			}
		});
		iedit1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.UPDATE_CONTEST);
			}
		});
		iedit2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.UPDATE_DISCIPLINE);
			}
		});
		iedit3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.UPDATE_TCATEGORY);
			}
		});
		iedit4.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.UPDATE_CATEGORY);
			}
		});
		iedit5.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.UPDATE_TEAM);
			}
		});
		iedit6.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handleMenuAction(MenuAction.UPDATE_CONTESTANT);
			}
		});
		
		main.getItems().addAll(imain1, imain2, imain3);
		show.getItems().addAll(ishow1, ishow2, ishow3);
		score.getItems().addAll(iscore1);
		edit.getItems().addAll(iedit1, iedit2, iedit3, iedit4, iedit5, iedit6);
		mbar.getMenus().addAll(main, show, score, edit);
		
		return mbar;
	}
	
	/**
	 * Handles a MenuAction. This method is called when a menu button is clicked 
	 * and the respective enum value is passed to this method.
	 * @param ma MenuAction representing the menu option selected.
	 */
	protected void handleMenuAction(MenuAction ma) {
		MenuAction prevMa = currentMenu;
		currentMenu = ma;
		boolean nonQuietReload = false;
		switch(ma) {
		case MAIN_CONTESTS:
			ActionHandler.get().showContestList();
			break;
		case MAIN_RELOAD:
			nonQuietReload = true;
		case MAIN_RELOAD_QUIET:
			ActionHandler.get().reloadContestList(false);
			if(current != null) {
				ActionHandler.get().loadContest(current.getId(), nonQuietReload);
				handleMenuAction(prevMa);
			}
			else {
				ActionHandler.get().showNoContestWarning();
			}
			break;
		case MAIN_EXIT:
			this.mainStage.close();
			break;
		case SHOW_CONTESTANTS:
			ActionHandler.get().showTable(ContestantTable.getInstance());
			break;
		case SHOW_TEAMS:
			ActionHandler.get().showTable(TeamTable.getInstance());
			break;
		case SHOW_ONE_TEAM:
			ActionHandler.get().showTable(TeamDetail.getInstance());
			break;
		case SCORE_CATEGORY:
			ActionHandler.get().showTable(CategoryScore.getInstance());
			break;
		case UPDATE_CONTEST:
			ActionHandler.get().showTable(EditContest.getInstance());
			break;
		case UPDATE_DISCIPLINE:
			ActionHandler.get().showTable(EditDiscipline.getInstance());
			break;
		case UPDATE_TCATEGORY:
			ActionHandler.get().showTable(EditTeamCategory.getInstance());
			break;
		case UPDATE_CATEGORY:
			ActionHandler.get().showTable(EditCategory.getInstance());
			break;
		case UPDATE_TEAM:
			ActionHandler.get().showTable(EditTeam.getInstance());
			break;
		case UPDATE_CONTESTANT:
			ActionHandler.get().showTable(EditContestant.getInstance());
			break;
		}
	}
	
	/**
	 * The entry point of the client part.
	 * @param args Command line arguments
	 */
	public static void main(String args[]) {
		launch(args);
	}

}
