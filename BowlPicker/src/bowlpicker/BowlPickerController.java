package bowlpicker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BowlPickerController implements Initializable, ControlledScreen {
    private static Game[] allGames;
    private static SpecButton[] teamButtons;
    private ScreensController parentController;
    
    private static final int NUM_GAMES = 39;
    
    @FXML
    private VBox schedule;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allGames = new Game[NUM_GAMES];
        teamButtons = new SpecButton[NUM_GAMES * 2];
        try {
            scheduler();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void scheduler() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("teams.txt"));
        int counter = 0;
        String thisLine = input.readLine();
        while (thisLine != null) {
            String[] singleGame = thisLine.split(" ");
            allGames[counter] = new Game(new Team(singleGame[0], true), new Team(singleGame[1], false), 0);
            thisLine = input.readLine();
            counter++;
        }
        input.close();
    }

    private void placeButtons() {
        for (int i = 0; i < allGames.length; i++) {
            Game curGame = allGames[i];
            teamButtons[i] = new SpecButton(curGame.getAwayTeam().getName());
            teamButtons[i + 1] = new SpecButton(curGame.getHomeTeam().getName());
            HBox gameRow = new HBox();
            gameRow.setPrefSize(Driver.SCREEN_WIDTH, 10);
            gameRow.setPadding(new Insets(5, 12, 5, 12));
            gameRow.setSpacing(Driver.SCREEN_WIDTH - (2 * teamButtons[i].getPrefWidth()) - 24);
            gameRow.getChildren().addAll(teamButtons[i], teamButtons[i + 1]);
            //schedule.add(gameRow);
        }
    }

    @FXML
    private void submitButtonAction(ActionEvent event) {
        String msg = "Please pick all games before submitting!";
        if (allGamesPicked()) {
            msg = "Thank you! Email 'picks.txt' to Ryan.";
        }
    }

    private boolean allGamesPicked() {
        for (int i = 0; i < teamButtons.length; i += 2) {
            if (!(teamButtons[i].isSelected || teamButtons[i + 1].isSelected)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setScreenParent(ScreensController parentController) {
        this.parentController = parentController;
    }

    private class SpecButton extends Button {
        protected boolean isSelected = false;

        public SpecButton(String label) {
            super(label);
            this.setPrefSize(100, 10);
            this.setStyle("-fx-background-color: #FFFFFF;");
            this.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(final ActionEvent event) {
                    //int index = teamButtons.indexOf(b);
                    int index = 0;
                    if (!isSelected) {
                        setStyle("-fx-background-color: #00BFFF;");
                        isSelected = true;
                        if (index % 2 != 0) {
                            SpecButton b2 = teamButtons[index - 1];
                            b2.setStyle("-fx-background-color: #FFFFFF;");
                            b2.isSelected = false;
                        } else {
                            SpecButton b2 = teamButtons[index + 1];
                            b2.setStyle("-fx-background-color: #FFFFFF;");
                            b2.isSelected = false;
                        }
                    } else {
                        setStyle("-fx-background-color: #FFFFFF;");
                        isSelected = false;
                    }
                }
            });
        }
    }
}