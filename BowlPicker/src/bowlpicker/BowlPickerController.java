package bowlpicker;

import bowlpicker.Team.Conference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
            System.out.println("Setup didn't work, probably due to an "
                    + "error with teams.txt");
        }
    }

    private void setup() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("teams.txt"));
        int counter = 0;
        String firstLine = input.readLine();
        while (firstLine != null) {
            String secondLine = input.readLine();
            String[] team1Info = secondLine.split(" ");
            Team team1 = new Team(addSpacesBack(team1Info[0]), true,
                    team1Info[1], Conference.valueOf(team1Info[2]), team1Info[3]);
            String thirdLine = input.readLine();
            String[] team2Info = thirdLine.split(" ");
            Team team2 = new Team(addSpacesBack(team2Info[0]), false,
                    team2Info[1], Conference.valueOf(team2Info[2]), team2Info[3]);
            allGames[counter] = new GameRow(
                    new Game(team1, team2, firstLine), counter);
            gamesVBox.getChildren().add(allGames[counter]);
            firstLine = input.readLine();
            counter++;
        }
        input.close();
    }

    private String addSpacesBack(String originalStr) {
        StringBuilder s = new StringBuilder(originalStr);
        for (int i = 1; i < s.length(); ++i) {
            if (Character.isLowerCase(s.charAt(i - 1))
                    && Character.isUpperCase(s.charAt(i))) {
                s.insert(i++, ' ');
            }
        }
        return s.toString();
    }

    private void selectionCheckBoxAction(ActionEvent event) {
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
            // Email and write to file their picks
            // Clear the screen, display the appropriate ending message
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
        private final int gameNumber;

        @FXML
        private final CheckBox checkBox1;
        @FXML
        private final CheckBox checkBox2;

        public GameRow(Game game, int gameNumber) {
            super();
            this.game = game;
            this.gameNumber = gameNumber;
            this.checkBox1 = new CheckBox();
            checkBox1.setId(Integer.toString(2 * gameNumber));
            checkBox1.setOnAction(handler);
            this.checkBox2 = new CheckBox();
            checkBox2.setId(Integer.toString((2 * gameNumber) + 1));
            display();
        }

        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                CheckBox pressedBox = (CheckBox)event.getSource();
                if (pressedBox.getId().equals(checkBox1.getId())) {
                    if (!checkBox1.isSelected()) {
                        checkBox1.setSelected(true);
                        if (checkBox2.isSelected()) {
                            checkBox2.setSelected(false);
                        }
                    }
                } else {
                    if (!checkBox2.isSelected()) {
                        checkBox2.setSelected(true);
                        if (checkBox1.isSelected()) {
                            checkBox1.setSelected(false);
                        }
                    }
                }
            }
        };

        private void display() {
            ImageView imageView1 = new ImageView(game.getAwayTeam().getImage());
            this.getChildren().add(imageView1);
            Label rankLabel1 = new Label();
            this.getChildren().add(rankLabel1);
            if (Integer.parseInt(game.getAwayTeam().getRank()) > 0) {
                rankLabel1.setText("#" + game.getAwayTeam().getRank());
            }
            this.getChildren().add(new Label(game.getAwayTeam().getName()));
            this.getChildren().add(checkBox1);

            this.getChildren().add(checkBox2);
            Label rankLabel2 = new Label();
            this.getChildren().add(rankLabel2);
            if (Integer.parseInt(game.getHomeTeam().getRank()) > 0) {
                rankLabel2.setText("#" + game.getHomeTeam().getRank());
            }
            this.getChildren().add(new Label(game.getHomeTeam().getName()));
            ImageView imageView2 = new ImageView(game.getHomeTeam().getImage());
            this.getChildren().add(imageView2);
        }

        public Game getGame() {
            return game;
        }

        public boolean isPicked() {
            return (checkBox1.isSelected() || checkBox2.isSelected());
        }
    }
}