package addplayer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The results screen of ADDPlayer
 * @author Ryan Burns
 */
public class ResultsScreenController implements Initializable {
    @FXML
    private Label lastPlayerField;
    @FXML
    private Label player1Field;
    @FXML
    private Label player2Field;
    @FXML
    private Label playerOneField;
    @FXML
    private Label playerTwoField;
    @FXML
    private Label pointsField;
    @FXML
    private AnchorPane previewPane;
    @FXML
    private ScrollPane previewScroller;
    @FXML
    private AnchorPane scoresPane;
    @FXML
    private ScrollPane scoresScroller;
    @FXML
    private Label playerOneGameScore;
    @FXML
    private Label playerTwoGameScore;

    /**
     * 
     */
    private ScoreBoardEntry lastScoreBoardEntry;

    /**
     * The index into the preview boxes array of the currently playing song,
     *     set to -1 if there is not a song currently playing
     */
    private int indexPlaying;

    /**
     * Holds all of the play/pause buttons on this screen
     */
    private Button[] playPauseButtons;

    /**
     * Actually plays/pauses the songs
     */
    private MediaPlayer mediaPlayer;

    /**
     * Sets up the results scene by:
     *  - setting the player name and points text fields
     *  - applying new handlers to all of the colored boxes so they will now
     *        affect the points field on this screen rather than the last one
     * @param url Not used
     * @param rb Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        indexPlaying = -1;
        playPauseButtons = new Button[ADDPlayer.NUM_SONGS];

        setupTextFields();

        // If this wasn't the first round of the game
        if (!ADDPlayer.CUR_PLAYER.isPlayerOne
                || ADDPlayer.CUR_PLAYER.scores.size() > 0) {
            populateScoreboard();
        }

        setupPreviewPane();

        addPointsToScoreboard(ADDPlayer.POINTS, ADDPlayer.CUR_PLAYER,
                ADDPlayer.CUR_PLAYER.scores.size());
    }

    /**
     * Sets the last round's player, and updates the scoreboard every time
     *     the last round's score is changed
     */
    private void setupTextFields() {
        lastPlayerField.setText(ADDPlayer.CUR_PLAYER.name);

        // Just puts the players' names in the appropriate locations
        if (ADDPlayer.CUR_PLAYER.isPlayerOne) {
            player1Field.setText(ADDPlayer.CUR_PLAYER.name);
            player2Field.setText(ADDPlayer.OTHER_PLAYER.name);
            playerOneField.setText(ADDPlayer.CUR_PLAYER.name);
            playerTwoField.setText(ADDPlayer.OTHER_PLAYER.name);
        } else {
            player1Field.setText(ADDPlayer.OTHER_PLAYER.name);
            player2Field.setText(ADDPlayer.CUR_PLAYER.name);
            playerOneField.setText(ADDPlayer.OTHER_PLAYER.name);
            playerTwoField.setText(ADDPlayer.CUR_PLAYER.name);
        }

        pointsField.setText(Integer.toString(ADDPlayer.POINTS));
        pointsField.textProperty().addListener(
                (ObservableValue<? extends String> a, String b,
                        String newValue) -> {

            if (ADDPlayer.CUR_PLAYER.isPlayerOne) {
                lastScoreBoardEntry.playerOneLabel.setText(newValue);
            } else {
                lastScoreBoardEntry.playerTwoLabel.setText(newValue);
            }
        }); 
    }

    /**
     * Just fills in the scoreboard with all of the finished scores
     */
    private void populateScoreboard() {
        // Decide which player is player 1
        Player playerOne;
        Player playerTwo;
        if (ADDPlayer.CUR_PLAYER.isPlayerOne) {
            playerOne = ADDPlayer.CUR_PLAYER;
            playerTwo = ADDPlayer.OTHER_PLAYER;
        } else {
            playerOne = ADDPlayer.OTHER_PLAYER;
            playerTwo = ADDPlayer.CUR_PLAYER;
        }

        int p1Size = playerOne.scores.size();
        int p2Size = playerTwo.scores.size();
        for (int i = 0; i < p1Size - 1; i++) {
            addPointsToScoreboard(playerOne.scores.get(i), playerOne, i);
            addPointsToScoreboard(playerTwo.scores.get(i), playerTwo, i);
        }
        addPointsToScoreboard(
                playerOne.scores.get(p1Size - 1), playerOne, p1Size - 1);
        if (p1Size == p2Size) {
            addPointsToScoreboard(
                    playerTwo.scores.get(p2Size - 1), playerTwo, p2Size - 1);
        }
    }

    /**
     * Adds the round's points to the scoreboard
     * @param points The points to be added to the scoreboard
     * @param player The player whose points need to be added
     * @param index Which round these points belong in
     */
    private void addPointsToScoreboard(int points, Player player, int index) {
        // If player 1, create a new entry on the scoreboard for this round
        if (player.isPlayerOne) {
            lastScoreBoardEntry = new ScoreBoardEntry(points, index);
            scoresPane.getChildren().add(lastScoreBoardEntry);
        } else {
            lastScoreBoardEntry.playerTwoLabel.setText(
                    Integer.toString(points));

            // Keep the scoreboard scrolled to the bottom
            if (player.scores.size() > 7) {
                scoresScroller.setPrefWidth(415);
                scoresScroller.setVvalue(1);
            }
        }
    }

    /**
     * 
     */
    private void setupPreviewPane() {
        ColoredLabelClickHandler handler =
                new ColoredLabelClickHandler(pointsField);

        for (int i = 0; i < ADDPlayer.NUM_SONGS; i++) {
            PreviewHBox previewBox = ADDPlayer.PREVIEW_BOXES[i];

            for (Label colorBox : previewBox.colorBoxes) {
                colorBox.setOnMouseClicked(handler);
            }

            previewBox.setPrefWidth(350);

            ImageView playImgView = new ImageView(new Image("/icons/play.png"));
            playImgView.setFitWidth(14);
            playImgView.setFitHeight(14);

            int boxHeight = (int) previewBox.getPrefHeight();
            HBox playPauseHolder = new HBox();
            playPauseHolder.setPadding(new Insets
                    ((boxHeight - 50) / 2, 10, (boxHeight - 50) / 2, 10));

            Button playPause = new Button();
            playPause.setGraphic(playImgView);
            playPause.setPrefSize(30, 30);

            int temp = i;
            playPause.setOnAction((ActionEvent e) -> {
                playPauseButtonAction(temp);
            });

            playPauseButtons[i] = playPause;

            playPauseHolder.getChildren().add(playPause);

            previewBox.getChildren().add(1, playPauseHolder);

            previewPane.getChildren().add(previewBox);
        }

        if (ADDPlayer.NUM_SONGS > 5) {
            previewScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            previewScroller.setPrefWidth(365);
        }
    }

    /**
     * Tests whether the passed in button has a play icon, if it doesn't then
     *     it has a pause icon
     * @param playImgView A copy of the play icon to be compared against
     * @param playPause The button whose graphic is to be tested
     * @return Whether or not the button's graphic is the play icon
     */
    private boolean isPlayImage(ImageView playImgView, Button playPause) {
        Image currentImg = ((ImageView) playPause.getGraphic()).getImage();
        boolean isPlayImage = true;
        for (int i = 0; i < currentImg.getWidth(); i++) {
            for (int j = 0; j < currentImg.getHeight(); j++) {
                if (isPlayImage && currentImg.getPixelReader().getArgb(i, j) !=
                        playImgView.getImage().getPixelReader().getArgb(i, j)) {

                    isPlayImage = false;
                }
            }
        }
        return isPlayImage;
    }

    /**
     * Button handlers and private classes below here
     */

    /**
     * Handles any play/pause button in the preview pane being pressed by:
     *  - checking if the button's image is currently set to play or pause
     *  - setting this and the other button's graphics appropriately
     *  - playing or pausing the selected song
     *  - stopping the last song that was playing if there was one
     * @param index 
     */
    private void playPauseButtonAction(int index) {
        ImageView playImgView = new ImageView(new Image("/icons/play.png"));
        playImgView.setFitWidth(14);
        playImgView.setFitHeight(14);

        ImageView pauseImgView = new ImageView(new Image("/icons/pause.png"));
        pauseImgView.setFitWidth(14);
        pauseImgView.setFitHeight(14);

        boolean isPlayImage = isPlayImage(playImgView, playPauseButtons[index]);

        // Play the selected song
        if (isPlayImage) {
            // Stop the last song if there was one
            if (indexPlaying != -1) {
                playPauseButtons[indexPlaying].setGraphic(playImgView);
                mediaPlayer.pause();

                emboldenSong(false);
            }

            playPauseButtons[index].setGraphic(pauseImgView);
            SongDetails song = ADDPlayer.packageIntoSongDetails(
                    ADDPlayer.SONGS_IN_ROUND[index], ADDPlayer.CUR_PLAYER);
            mediaPlayer = ADDPlayer.playNextSong(song);
            indexPlaying = index;

            emboldenSong(true);
        // Stop playing the selected song
        } else {
            emboldenSong(false);

            playPauseButtons[indexPlaying].setGraphic(playImgView);
            mediaPlayer.pause();
            indexPlaying = -1;
        }
    }

    /**
     * 
     * @param bold If true make bold, if false make normal
     */
    private void emboldenSong(boolean bold) {
        if (bold) {
            ADDPlayer.PREVIEW_BOXES[indexPlaying].previewSong.setFont(
                    Font.font("System", FontWeight.BOLD, 14));
            ADDPlayer.PREVIEW_BOXES[indexPlaying].previewArtist.setFont(
                    Font.font("System", FontWeight.BOLD, 14));
            ADDPlayer.PREVIEW_BOXES[indexPlaying].previewAlbum.setFont(
                    Font.font("System", FontWeight.BOLD, 14));
        } else {
            ADDPlayer.PREVIEW_BOXES[indexPlaying].previewSong.setFont(
                    Font.font("System", FontWeight.NORMAL, 14));
            ADDPlayer.PREVIEW_BOXES[indexPlaying].previewArtist.setFont(
                    Font.font("System", FontWeight.NORMAL, 14));
            ADDPlayer.PREVIEW_BOXES[indexPlaying].previewAlbum.setFont(
                    Font.font("System", FontWeight.NORMAL, 14));
        }
    }

    /**
     * Button handlers and private classes below here
     */

    /**
     * Stops any songs currently playing and re-launches the game-play screen
     *     with whatever rule choices were used in the last round
     * @param event Not used
     */
    @FXML
    private void nextRoundButtonAction(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        ADDPlayer.CUR_PLAYER.scores.add(
                Integer.parseInt(pointsField.getText()));

        Player temp = ADDPlayer.CUR_PLAYER;
        ADDPlayer.CUR_PLAYER = ADDPlayer.OTHER_PLAYER;
        ADDPlayer.OTHER_PLAYER = temp;

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("GameplayScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Stops any songs currently playing and launches the opening screen
     * @param event Not used
     */
    @FXML
    private void mainMenuButtonAction(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("OpeningScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * 
     */
    private class ScoreBoardEntry extends HBox {
        private final Label roundLabel;
        private final Label playerOneLabel;
        private final Label playerTwoLabel;

        private ScoreBoardEntry(int playerOneScore, int index) {
            super();

            this.setPrefSize(400, 40);
            this.setLayoutY(40 * index);

            roundLabel = new Label(Integer.toString(index + 1));
            roundLabel.setPrefSize(100, 40);
            roundLabel.setFont(Font.font(18)); 
            roundLabel.setAlignment(Pos.CENTER);

            playerOneLabel = new Label(Integer.toString(playerOneScore));
            playerOneLabel.setPrefSize(150, 40);
            playerOneLabel.setFont(Font.font(18));
            playerOneLabel.setAlignment(Pos.CENTER);

            playerTwoLabel = new Label("--");
            playerTwoLabel.setPrefSize(150, 40);
            playerTwoLabel.setFont(Font.font(18));
            playerTwoLabel.setAlignment(Pos.CENTER);

            scoreBoardUpdate();

            this.getChildren().addAll(new Label[]
                    {roundLabel, playerOneLabel, playerTwoLabel});
        }

        /**
         * Bold the winner of each round and count the wins (in real-time)
         */
        private void scoreBoardUpdate() {
            playerTwoLabel.textProperty().addListener(
                (ObservableValue<? extends String> a, String oldValue,
                        String newValue) -> {

                // Convenience variables for the scores in question
                int P1 = Integer.parseInt(playerOneLabel.getText());
                int newP2 = Integer.parseInt(newValue);

                // Handles the initial scoreboard load
                if (oldValue.equals("--")) {
                    if (newP2 > P1) {
                        playerTwoLabel.setFont(
                                Font.font("System", FontWeight.BOLD, 18));
                        playerTwoGameScore.setText(Integer.toString(
                                Integer.parseInt(
                                        playerTwoGameScore.getText()) + 1));
                    } else if (newP2 < P1) {
                        playerOneLabel.setFont(
                                Font.font("System", FontWeight.BOLD, 18));
                        playerOneGameScore.setText(Integer.toString(
                                Integer.parseInt(
                                        playerOneGameScore.getText()) + 1));
                    }

                // Handles keeping the bolding and game score updated
                } else {
                    int oldP2 = Integer.parseInt(oldValue);

                    // p1 = p2 -> p2 > p1
                    if (newP2 > oldP2 && newP2 == P1 + 1) {

                        playerTwoLabel.setFont(
                                Font.font("System", FontWeight.BOLD, 18));
                        playerTwoGameScore.setText(Integer.toString(
                                Integer.parseInt(
                                        playerTwoGameScore.getText()) + 1));

                    // p1 = p2 -> p1 > p2
                    } else if (newP2 < oldP2 && newP2 == P1 - 1) {

                        playerOneLabel.setFont(
                                Font.font("System", FontWeight.BOLD, 18));
                        playerOneGameScore.setText(Integer.toString(
                                Integer.parseInt(
                                        playerOneGameScore.getText()) + 1));

                    // p1 > p2 -> p1 = p2
                    } else if (newP2 > oldP2 && newP2 == P1) {

                        playerOneLabel.setFont(
                                Font.font("System", FontWeight.NORMAL, 18));
                        playerOneGameScore.setText(Integer.toString(
                                Integer.parseInt(
                                        playerOneGameScore.getText()) - 1));

                    // p2 > p1 -> p1 = p2
                    } else if (newP2 < oldP2 && newP2 == P1) {

                        playerTwoLabel.setFont(
                                Font.font("System", FontWeight.NORMAL, 18));
                        playerTwoGameScore.setText(Integer.toString(
                                Integer.parseInt(
                                        playerTwoGameScore.getText()) - 1));
                    }
                }
            });
        }
    }
}