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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * The FXML controller for the main picking screen of BowlPicker
 * Inputs the schedule of games from file, then allows the user to
 *     pick each of those games, and then handles outputting their picks to file
 * @author Ryan Burns
 */
public class BowlPickerController implements Initializable {
    /**
     * As of 2014-2015,there are 39 bowl games, including the championship
     */
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

    /**
     * Reads in the bowl games from file and populates all of the games and
     *     displays all of their associated rows
     * @throws IOException Throws a fatal exception when teams.txt isn't present
     */
    private void setup() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("teams.txt"));
        int counter = 0;
        String firstLine = input.readLine();
        while (firstLine != null) {
            String secondLine = input.readLine();
            String[] team1Info = secondLine.split(" ");
            Team team1 = new Team(addSpacesBack(team1Info[0]), true,
                    team1Info[1], Conference.valueOf(team1Info[2]),
                    team1Info[3]);
            String thirdLine = input.readLine();
            String[] team2Info = thirdLine.split(" ");
            Team team2 = new Team(addSpacesBack(team2Info[0]), false,
                    team2Info[1], Conference.valueOf(team2Info[2]),
                    team2Info[3]);
            allGames[counter] = new GameRow(
                    new Game(team1, team2, firstLine), counter);
            gamesVBox.getChildren().add(allGames[counter]);
            firstLine = input.readLine();
            counter++;
        }
        input.close();
    }

    /**
     * Fixes an artifact of how the information is read into the program,
     *     and places spaces between capitalized words which had none
     * @param originalStr The String that currently has no spaces
     * @return The parameter String, now with appropriate spaces
     */
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

    /**
     * If the user has picked every game, their picks are outputted to a text
     *     file and hopefully to an automated email, and the final message
     *     is displayed
     * @param event The submit button being pressed
     */
    @FXML
    private void submitButtonAction(ActionEvent event) {
        if (!allGamesPicked()) {
            errorField.setText("Please pick all games before submitting.");
            if (ft != null) {
                ft.play();
            }
        } else {

            boolean emailSuccess = endingProtocol();
            Pane mainPane = (Pane)scrollPane.getParent();
            mainPane.getChildren().clear();
            Label finalMsg = new Label();
            finalMsg.setPrefWidth(560);
            finalMsg.setPrefHeight(720);
            finalMsg.setLayoutX(50);
            finalMsg.setWrapText(true);
            finalMsg.setFont(new Font("System", 24));
            if (emailSuccess) {
                finalMsg.setText("Your picks have been emailed to Ryan, and "
                        + "there is also a copy of them in the same folder "
                        + "as this application for your own viewing. Ryan will "
                        + "update you when all picks have been submitted. "
                        + "Good luck, and Go Jackets!");
            } else {
                finalMsg.setText("Your picks have failed to email to Ryan, but "
                        + "there is a copy of them in the same folder "
                        + "as this application. You will need to email this "
                        + ".txt file to bearskid5@yahoo.com for your picks "
                        + "to count. Ryan will update you when all picks have "
                        + "been submitted. Good luck, and Go Jackets!");
            }
            finalMsg.setAlignment(Pos.CENTER);
            mainPane.getChildren().add(finalMsg);
        }
    }

    /**
     * Handles all procedures to be taken upon successful picking completion
     *     Attempts to email picks to me, and saves picks to file
     * @return Whether or not the email sent successfully
     */
    private boolean endingProtocol() {
        
        return false;
    }

    /**
     * @return Whether the user has picked every game available to them
     */
    private boolean allGamesPicked() {
        for (GameRow gameRow : allGames) {
            if (!gameRow.isPicked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * A special type of HBox that houses the GUI for one bowl game
     */
    private class GameRow extends HBox {
        public static final int LABEL_HEIGHT = 70;

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
            this.checkBox2 = new CheckBox();
            checkBox1.setId(Integer.toString(2 * gameNumber));
            checkBox1.setOnAction(handler);
            checkBox2.setId(Integer.toString((2 * gameNumber) + 1));
            checkBox2.setOnAction(handler);
            display();
        }

        /**
         * Handles any CheckBox being pressed
         */
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                CheckBox pressedBox = (CheckBox)event.getSource();
                if (pressedBox.getId().equals(checkBox1.getId())) {
                    checkBox1.setSelected(true);
                    checkBox2.setSelected(false);
                } else if (pressedBox.getId().equals(checkBox2.getId())) {
                    checkBox2.setSelected(true);
                    checkBox1.setSelected(false);
                }
            }
        };

        /**
         * Displays everything that is housed in one GameRow
         */
        private void display() {
            // The first team's icon
            ImageView imageView1 = new ImageView(game.getAwayTeam().getImage());
            this.getChildren().add(imageView1);

            // The first team's name and cooresponding CheckBox
            StringBuilder labelStr1 = new StringBuilder();
            if (Integer.parseInt(game.getAwayTeam().getRank()) > 0) {
                labelStr1.append("#").append(
                        game.getAwayTeam().getRank()).append(" ");
            }
            labelStr1.append(game.getAwayTeam().getName());
            Label label1 = new Label(labelStr1.toString());
            label1.setGraphic(checkBox1);
            label1.setContentDisplay(ContentDisplay.RIGHT);
            label1.setGraphicTextGap(10);
            label1.setPrefHeight(LABEL_HEIGHT);
            label1.setPrefWidth(145);
            label1.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().add(label1);

            // The first separator, between team 1 and the bowl info
            Separator sep1 = new Separator();
            sep1.setOrientation(Orientation.VERTICAL);
            this.getChildren().add(sep1);

            // The bowl info
            Label bowlLabel = new Label(game.getBowlName());
            bowlLabel.setPrefWidth(115);
            bowlLabel.setPrefHeight(LABEL_HEIGHT);
            bowlLabel.setAlignment(Pos.CENTER);
            this.getChildren().add(bowlLabel);

            // The second separator, between the bowl info and team 2
            Separator sep2 = new Separator();
            sep2.setOrientation(Orientation.VERTICAL);
            this.getChildren().add(sep2);

            // The second team's name and cooresponding CheckBox
            StringBuilder labelStr2 = new StringBuilder();
            if (Integer.parseInt(game.getHomeTeam().getRank()) > 0) {
                labelStr2.append("#").append(
                        game.getHomeTeam().getRank()).append(" ");
            }
            labelStr2.append(game.getHomeTeam().getName());
            Label label2 = new Label(labelStr2.toString());
            label2.setGraphic(checkBox2);
            label2.setContentDisplay(ContentDisplay.LEFT);
            label2.setPrefHeight(LABEL_HEIGHT);
            label2.setPrefWidth(145);
            label2.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().add(label2);

            // The second team's icon
            ImageView imageView2 = new ImageView(game.getHomeTeam().getImage());
            this.getChildren().add(imageView2);
        }

        public Game getGame() {
            return game;
        }

        public int getGameNumber() {
            return gameNumber;
        }

        public boolean isPicked() {
            return (checkBox1.isSelected() || checkBox2.isSelected());
        }
    }
}