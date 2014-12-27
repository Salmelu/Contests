package cz.salmelu.contests.client;

import java.util.Map.Entry;

import cz.salmelu.contests.model.ContestInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

class ContestTable {
	
	private Client c;
	private static ContestTable instance = null;
	
	private GridPane gp;
	
	private ContestTable(Client c) {
		this.c = c;
		gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(6);
	}
	
	protected static ContestTable getInstance() {
		return instance;
	}
	
	protected static void setClient(Client c) {
		instance = new ContestTable(c);
	}
	
	protected void display() {
		gp.getChildren().clear();
		
		Label headName = new Label("Contest name");
		Label headSizeCon = new Label("Contestants");
		Label headSizeTeam = new Label("Teams");
		gp.add(headName, 0, 0);
		gp.add(headSizeCon, 1, 0);
		gp.add(headSizeTeam, 2, 0);
		
		final ToggleGroup choiceGroup = new ToggleGroup();
		int index=1;
		for(Entry<String, ContestInfo> e : c.contests.entrySet()) {
			RadioButton rb = new RadioButton(e.getKey());
			rb.setToggleGroup(choiceGroup);
			rb.setUserData(e.getKey());
			if(c.current != null && e.getKey().equals(c.current.getName())) {
				rb.setSelected(true);
			}
			gp.add(rb, 0, index);

			Label sizeCon = new Label(String.valueOf(e.getValue().getContestants()));
			gp.add(sizeCon, 1, index);
			Label sizeTeam = new Label(String.valueOf(e.getValue().getTeams()));
			gp.add(sizeTeam, 2, index);
			index++;
		}
		choiceGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle arg1, Toggle arg2) {
				String name = choiceGroup.getSelectedToggle().getUserData().toString();
				for(String s : c.contests.keySet()) {
					if(s.equals(name)) {
						c.ah.loadContest(c, c.contests.get(s).getId());
						return;
					}
				}
			}
		});
		c.mainPanel.setCenter(gp);
	}
}
