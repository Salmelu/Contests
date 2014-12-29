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

public class Client extends Application {

	protected HashMap<String, ContestInfo> contests = null;
	protected VBox mainBox;
	protected Stage mainStage;
	protected BorderPane mainPanel;
	protected Contest current;
	protected ActionHandler ah;
	protected MenuAction currentMenu;
	
	@Override
	public void start(Stage arg0) throws Exception {
		ah = new ActionHandler();
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
		
		ah.reloadContestList(this, false);
		arg0.show();
	}
	
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
		MenuItem iedit3 = new MenuItem("Update/New Team category");
		
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
		
		main.getItems().addAll(imain1, imain2, imain3);
		show.getItems().addAll(ishow1, ishow2, ishow3);
		score.getItems().addAll(iscore1);
		edit.getItems().addAll(iedit1, iedit2, iedit3);
		mbar.getMenus().addAll(main, show, score, edit);
		
		return mbar;
	}
	
	private void clearPanel() {
		mainPanel.setTop(null);
		mainPanel.setCenter(null);
	}
	
	protected void handleMenuAction(MenuAction ma) {
		MenuAction prevMa = currentMenu;
		currentMenu = ma;
		boolean nonQuietReload = false;
		switch(ma) {
		case MAIN_CONTESTS:
			clearPanel();
			ah.showContestList(this);
			break;
		case MAIN_RELOAD:
			nonQuietReload = true;
		case MAIN_RELOAD_QUIET:
			ah.reloadContestList(this, false);
			if(current != null) {
				ah.loadContest(this, current.getId(), nonQuietReload);
				handleMenuAction(prevMa);
			}
			else {
				ah.showNoContestWarning(this);
			}
			break;
		case MAIN_EXIT:
			this.mainStage.close();
			break;
		case SHOW_CONTESTANTS:
			clearPanel();
			ah.showContestantTable(this);
			break;
		case SHOW_TEAMS:
			clearPanel();
			ah.showTeamTable(this);
			break;
		case SHOW_ONE_TEAM:
			clearPanel();
			ah.showTeamDetail(this);
			break;
		case SCORE_CATEGORY:
			clearPanel();
			ah.updateCategoryScore(this);
			break;
		case UPDATE_CONTEST:
			clearPanel();
			ah.updateContest(this);
			break;
		case UPDATE_DISCIPLINE:
			clearPanel();
			ah.updateDiscipline(this);
			break;
		case UPDATE_TCATEGORY:
			clearPanel();
			ah.updateTeamCategory(this);
			break;
		default:
				
		}
	}
	
	public static void main(String args[]) {
		launch(args);
	}

}
