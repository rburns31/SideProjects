package bowlpicker;

import bowlpicker.Team.Conference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * 
 * @author Ryan Burns
 */
public class BowlPickerController implements Initializable {
    public static final int NUM_GAMES = 39;

    private GameRow[] allGames;
    private FadeTransition ft;

    @FXML
    private Label errorField;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox gamesVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ft = new FadeTransition(Duration.millis(1000), errorField);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        allGames = new GameRow[NUM_GAMES];
        scrollPane.setContent(gamesVBox);
        try {
            setup();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setup() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("teams.txt"));
        int counter = 0;
        String firstLine = input.readLine();
        while (firstLine != null) {
            String[] team1Info = firstLine.split(" ");
            Team team1 = new Team(team1Info[0], true, Integer.parseInt(team1Info[1]),
                    Conference.valueOf(team1Info[2]), team1Info[3]);
            String secondLine = input.readLine();
            String[] team2Info = secondLine.split(" ");
            Team team2 = new Team(team2Info[0], false, Integer.parseInt(team2Info[1]),
                    Conference.valueOf(team2Info[2]), team2Info[3]);
            allGames[counter] = new GameRow(new Game(team1, team2));
            gamesVBox.getChildren().add(allGames[counter]);
            firstLine = input.readLine();
            counter++;
        }
        input.close();
    }

    private void selectionMade() {
        // Handles whenever a user picks any game
    }

    @FXML
    private void submitButtonAction(ActionEvent event) {
        if (!allGamesPicked()) {
            errorField.setText("Please pick all games before submitting.");
            if (ft != null) {
                ft.play();
            }
        } else {
            try {
                Pane myPane = (Pane)FXMLLoader.load(getClass().getResource("EndingScreen.fxml"));
                Scene myScene = new Scene(myPane);
                Driver.mainStage.setScene(myScene);
                Driver.mainStage.show();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }

    private boolean allGamesPicked() {
        for (GameRow gameRow : allGames) {
            if (!gameRow.isPicked()) {
                return false;
            }
        }
        return true;
    }

    private class GameRow extends HBox {
        private final Game game;

        public GameRow(Game game) {
            super();
            this.game = game;
        }

        public Game getGame() {
            return game;
        }

        public boolean isPicked() {
            return false; // TODO: Implement
        }
    }
}