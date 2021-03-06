package bowlpicker;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.FadeTransition;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * The FXML controller for the main picking screen of BowlPicker
 * Inputs the schedule of games from file, then allows the user to
 *     pick each of those games, and then handles outputting their picks to file
 * @author Ryan Burns
 */
public class BowlPickerController implements Initializable {
    /**
     * As of 2017-2018, there are 41 bowl games, including the championship
     */
    public static final int NUM_GAMES = 41;

    private final Set<String> bigBowlNames = new HashSet<>();

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

        bigBowlNames.add("Championship Game");
        bigBowlNames.add("Rose Bowl");
        bigBowlNames.add("Sugar Bowl");
        bigBowlNames.add("Orange Bowl");
        bigBowlNames.add("Cotton Bowl");
        bigBowlNames.add("Fiesta Bowl");
        bigBowlNames.add("Peach Bowl");

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
        BufferedReader input = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("teams.txt")));
        int counter = 0;
        String firstLine = input.readLine();
        while (firstLine != null) {
            String secondLine = input.readLine();
            String[] team1Info = secondLine.split(" ");
            Team team1 = new Team(addSpacesBack(team1Info[0]), team1Info[1],
                    Team.Conference.valueOf(team1Info[2]),
                    team1Info[3], team1Info[4]);

            String thirdLine = input.readLine();
            String[] team2Info = thirdLine.split(" ");
            Team team2 = new Team(addSpacesBack(team2Info[0]), team2Info[1],
                    Team.Conference.valueOf(team2Info[2]),
                    team2Info[3], team2Info[4]);

            String[] bowlInfo = firstLine.split(" ");
            allGames[counter] = new GameRow(new Game(team1, team2,
                    addSpacesBack(bowlInfo[0]), bowlInfo[1], bowlInfo[2]),
                    counter);

            gamesVBox.getChildren().add(allGames[counter]);
            firstLine = input.readLine();
            counter++;
        }
        input.close();

        // Initializes the championship game as a special case (TBD teams)
        Team champTeam1 = new Team("TBD", "-1", Team.Conference.TBD, "0-0", "-1");
        Team champTeam2 = new Team("TBD", "-1", Team.Conference.TBD, "0-0", "-1");
        // TODO: Needs to be changed each year
        allGames[counter] = new GameRow(
                new Game(champTeam1, champTeam2, "Championship Game",
                        "1/8", "8:00pm"), counter);
        gamesVBox.getChildren().add(allGames[counter]);
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
     * If the user has picked every game, their picks are outputted as a
     *     port of the the final message, which is displayed
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
            Pane mainPane = (Pane)scrollPane.getParent();
            mainPane.getChildren().clear();

            endingProtocol();

            try {
                Pane myPane = (Pane)FXMLLoader.load(getClass().getResource(
                        "ClosingScreen.fxml"));
                Scene myScene = new Scene(myPane);
                Driver.mainStage.setScene(myScene);
                Driver.mainStage.show();
            } catch (IOException ioe) {
                System.out.println("Couldn't load the closing screen.");
            }
        }
    }

    /**
     * Handles all procedures to be taken upon successful picking completion
     *      and outputs string representing the user's name and picks
     */
    private void endingProtocol() {
        StringBuilder outputStr = new StringBuilder(
                Driver.playerName.replace(" ", ""));
        int num = 0;
        for (int i = 0; i < allGames.length / 2; i++) {
            if (allGames[i].checkBox2.isSelected()) {
                num += Math.pow(2, i);
            }
        }
        outputStr.append(num).append("/");
        num = 0;
        for (int i = allGames.length / 2; i < allGames.length - 1; i++) {
            if (allGames[i].checkBox2.isSelected()) {
                num += Math.pow(2, i - allGames.length / 2);
            }
        }
        outputStr.append(num).append("/");

        // Encode the last character for the championship game pick
        String champ = allGames[allGames.length - 1].getPick();
        if (champ.equals("Clemson")) {
            outputStr.append(0);
        } else if (champ.equals("Oklahoma")) {
            outputStr.append(1);
        } else if (champ.equals("Alabama")) {
            outputStr.append(2);
        } else {
            outputStr.append(3);
        }
        
        System.out.println(outputStr);
        Driver.output = outputStr.toString();
    }

    /**
     * @return Whether the user has picked every game available to them
     */
    private boolean allGamesPicked() {
        for (GameRow gameRow : allGames) {
            if (!gameRow.isPicked()) {
                gameRow.setStyle("-fx-background-color: #FF0000");
                return false;
            }
        }
        return true;
    }

    /**
     * A special type of HBox that houses the GUI for one bowl game
     */
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
            this.checkBox2 = new CheckBox();
            checkBox1.setId(Integer.toString(2 * gameNumber));
            checkBox1.setOnAction(checkBoxHandler);
            checkBox2.setId(Integer.toString((2 * gameNumber) + 1));
            checkBox2.setOnAction(checkBoxHandler);
            if (bigBowlNames.contains(game.getBowlName())) {
                this.setStyle("-fx-background-color: #FFFF00");
            }
            //checkBox1.setSelected(true); // DELETE THIS!!!
            display();
        }

        /**
         * Returns the team picked to win this game
         * @return The team name that the user has picked to win this game
         */
        public String getPick() {
            if (checkBox1.isSelected()) {
                return game.getAwayTeam().getName();
            } else {
                return game.getHomeTeam().getName();
            }
        }

        /**
         * Handles any CheckBox being pressed by toggling the choice if
         *     appropriate, and taking the proper action when semi-finals
         *     are selected, etc.
         */
        EventHandler<ActionEvent> checkBoxHandler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // Toggles the choice of the game
                CheckBox pressedBox = (CheckBox)event.getSource();
                if (pressedBox.getId().equals(checkBox1.getId())) {
                    checkBox1.setSelected(true);
                    checkBox2.setSelected(false);
                } else if (pressedBox.getId().equals(checkBox2.getId())) {
                    checkBox2.setSelected(true);
                    checkBox1.setSelected(false);
                }
                // Unhighlights the box if it was red prior to being selected
                GameRow thisRow = (GameRow)pressedBox.getParent().getParent().getParent();
                if (thisRow.getStyle().equals("-fx-background-color: #FF0000")) {
                    thisRow.setStyle("-fx-background-color: #FFFFFF");
                    if (bigBowlNames.contains(game.getBowlName())) {
                        thisRow.setStyle("-fx-background-color: #FFFF00");
                    }
                }
                // Just some prompts for my own enjoyment
                if (game.getAwayTeam().getName().equals("Georgia Tech")
                        || game.getHomeTeam().getName().equals("Georgia Tech")) {
                    errorField.setText("Give 'em hell, Tech!");
                    if (ft != null) {
                        ft.play();
                    }
                } else if (game.getAwayTeam().getName().equals("Georgia")
                        || game.getHomeTeam().getName().equals("Georgia")) {
                    errorField.setText("THWg");
                    if (ft != null) {
                        ft.play();
                    }
                }
                // Handles the semifinals' picks affecting the championship game
                // TODO: Needs to be changed each year
                if (game.getBowlName().equals("Rose Bowl")) {
                    allGames[NUM_GAMES - 1] = new GameRow(
                            allGames[NUM_GAMES - 1].game.setAwayTeam(
                                    getCurrentlyPicked()), NUM_GAMES - 1);
                    gamesVBox.getChildren().remove(NUM_GAMES - 1);
                    gamesVBox.getChildren().add(allGames[NUM_GAMES - 1]);
                } else if (game.getBowlName().equals("Sugar Bowl")) {
                    allGames[NUM_GAMES - 1] = new GameRow(
                            allGames[NUM_GAMES - 1].game.setHomeTeam(
                                    getCurrentlyPicked()), NUM_GAMES - 1);
                    gamesVBox.getChildren().remove(NUM_GAMES - 1);
                    gamesVBox.getChildren().add(allGames[NUM_GAMES - 1]);
                }
            }
        };

        /**
         * Handles any team icon being clicked by opening that team's info in
         *     their browser on ESPN's web site
         */
        EventHandler<MouseEvent> imageClickHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                try {
                    StringBuilder urlName = new StringBuilder();
                    urlName.append("http://espn.go.com/college-football/team/_/id/");

                    Team thisTeam = game.getAwayTeam();
                    ImageView thisImage = (ImageView)event.getSource();
                    if (Integer.parseInt(thisImage.getId()) % 2 == 1) {
                        thisTeam = game.getHomeTeam();
                    }

                    urlName.append(thisTeam.getEspnID());

                    if (!thisTeam.getName().equals("TBD")) {
                        Desktop.getDesktop().browse(new URI(urlName.toString()));
                    }
                } catch (URISyntaxException | IOException e) {
                    errorField.setText("Could not open that team's webpage. "
                            + "Check your internet.");
                    if (ft != null) {
                        ft.play();
                    }
                }
                event.consume();
            }
        };

        /**
         * Displays everything that is housed in one GameRow
         */
        private void display() {
            // The first team's icon
            ImageView imageView1 = new ImageView(game.getAwayTeam().getImage());
            imageView1.setId(Integer.toString(2 * gameNumber));
            imageView1.setOnMouseClicked(imageClickHandler);
            this.getChildren().add(imageView1);

            // The first team's name and cooresponding CheckBox
            StringBuilder team1NameStr = new StringBuilder();
            if (Integer.parseInt(game.getAwayTeam().getRank()) > 0) {
                team1NameStr.append("#").append(
                        game.getAwayTeam().getRank()).append(" ");
            }
            team1NameStr.append(game.getAwayTeam().getName());

            Label team1NameLabel = new Label(team1NameStr.toString());
            team1NameLabel.setGraphic(checkBox1);
            team1NameLabel.setContentDisplay(ContentDisplay.RIGHT);
            team1NameLabel.setGraphicTextGap(10);
            team1NameLabel.setAlignment(Pos.CENTER_RIGHT);

            Label team1InfoLabel = new Label(
                    game.getAwayTeam().getConferenceString() + ", "
                            + game.getAwayTeam().getRecord());
            team1InfoLabel.setAlignment(Pos.CENTER_RIGHT);
            team1InfoLabel.setFont(Font.font("System", FontPosture.ITALIC, 12));

            VBox team1LabelBox = new VBox();
            team1LabelBox.setPrefWidth(200);
            team1LabelBox.setAlignment(Pos.CENTER_RIGHT);
            team1LabelBox.getChildren().addAll(team1NameLabel, team1InfoLabel);
            this.getChildren().add(team1LabelBox);

            // The first separator, between team 1 and the bowl info
            Separator sep1 = new Separator();
            sep1.setOrientation(Orientation.VERTICAL);
            this.getChildren().add(sep1);

            // The bowl info
            Label bowlNameLabel = new Label(game.getBowlName());
            bowlNameLabel.setAlignment(Pos.CENTER);
            bowlNameLabel.setFont(Font.font("System", FontWeight.BOLD, 11));

            Label bowlTimeLabel = new Label(
                    game.getDate() + ", "+ game.getTime());
            bowlTimeLabel.setAlignment(Pos.CENTER);

            VBox bowlLabelBox = new VBox();
            bowlLabelBox.setPrefWidth(200);
            bowlLabelBox.setAlignment(Pos.CENTER);
            bowlLabelBox.getChildren().addAll(bowlNameLabel, bowlTimeLabel);
            this.getChildren().add(bowlLabelBox);

            // The second separator, between the bowl info and team 2
            Separator sep2 = new Separator();
            sep2.setOrientation(Orientation.VERTICAL);
            this.getChildren().add(sep2);

            // The second team's name and cooresponding CheckBox
            StringBuilder team2NameStr = new StringBuilder();
            if (Integer.parseInt(game.getHomeTeam().getRank()) > 0) {
                team2NameStr.append("#").append(
                        game.getHomeTeam().getRank()).append(" ");
            }
            team2NameStr.append(game.getHomeTeam().getName());

            Label team2NameLabel = new Label(team2NameStr.toString());
            team2NameLabel.setGraphic(checkBox2);
            team2NameLabel.setContentDisplay(ContentDisplay.LEFT);
            team2NameLabel.setAlignment(Pos.CENTER_LEFT);

            Label team2InfoLabel = new Label(
                    game.getHomeTeam().getConferenceString() + ", "
                            + game.getHomeTeam().getRecord());
            team2InfoLabel.setAlignment(Pos.CENTER_LEFT);
            team2InfoLabel.setFont(Font.font("System", FontPosture.ITALIC, 12));

            VBox team2LabelBox = new VBox();
            team2LabelBox.setPrefWidth(200);
            team2LabelBox.setAlignment(Pos.CENTER_LEFT);
            team2LabelBox.getChildren().addAll(team2NameLabel, team2InfoLabel);
            this.getChildren().add(team2LabelBox);

            // The second team's icon
            ImageView imageView2 = new ImageView(game.getHomeTeam().getImage());
            imageView2.setId(Integer.toString(2 * gameNumber) + 1);
            imageView2.setOnMouseClicked(imageClickHandler);
            this.getChildren().add(imageView2);
        }

        public Game getGame() {
            return game;
        }

        public Team getCurrentlyPicked() {
            if (checkBox1.isSelected()) {
                return game.getAwayTeam();
            } else if (checkBox2.isSelected()) {
                return game.getHomeTeam();
            } else {
                return null;
            }
        }

        public boolean isPicked() {
            return (checkBox1.isSelected() || checkBox2.isSelected());
        }
    }
}
