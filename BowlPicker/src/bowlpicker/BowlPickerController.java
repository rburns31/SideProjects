package bowlpicker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * 
 * @author Ryan Burns
 */
public class BowlPickerController implements Initializable {
    public static final int NUM_GAMES = 39;

    private static Game[] allGames;

    @FXML
    private Label errorField;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox gamesVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allGames = new Game[NUM_GAMES];
        scrollPane.setContent(gamesVBox);
        try {
            setup();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void setup() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("teams.txt"));
        int counter = 0;
        String thisLine = input.readLine();
        while (thisLine != null) {
            String[] singleGame = thisLine.split(" ");
            allGames[counter] = new Game(new Team(singleGame[0], true, 0),
                                        new Team(singleGame[1], false, 0), 0);
            thisLine = input.readLine();
            counter++;
        }
        input.close();
    }

    private void placeButtons() {
        for (int i = 0; i < allGames.length; i++) {
            Game curGame = allGames[i];
            GameRow gameRow = new GameRow(curGame);
            gameRow.setPrefSize(Driver.SCREEN_WIDTH, 10);
            gameRow.setPadding(new Insets(5, 12, 5, 12));
            gamesVBox.getChildren().add(gameRow);
        }
    }

    @FXML
    private void submitButtonAction(ActionEvent event) {
        if (!allGamesPicked()) {
            errorField.setText("Please pick all games before submitting.");
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
        for (int i = 0; i < teamButtons.length; i += 2) {
            if (!(teamButtons[i].isSelected || teamButtons[i + 1].isSelected)) {
                return false;
            }
        }
        return true;
    }

    private class GameRow extends HBox {
        private final Game game;

        public GameRow(Game game) {
            this.game = game;
        }
    }
}