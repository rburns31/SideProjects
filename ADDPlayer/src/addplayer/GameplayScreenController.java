package addplayer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * The main game-play screen of ADDPlayer
 * @author Ryan Burns
 */
public class GameplayScreenController implements Initializable {
    @FXML
    private ToggleButton songCorrect;
    @FXML
    private ToggleButton artistCorrect;
    @FXML
    private ToggleButton albumCorrect;
    @FXML
    private ToggleButton songIncorrect;
    @FXML
    private ToggleButton artistIncorrect;
    @FXML
    private ToggleButton albumIncorrect;
    @FXML
    private Label playerField;
    @FXML
    private Label librarySizeField;
    @FXML
    private Label progressField;
    @FXML
    private Label pointsField;
    @FXML
    private Label scoreToBeatField;
    @FXML
    private TextField songField;
    @FXML
    private TextField artistField;
    @FXML
    private TextField albumField;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider songProgressSlider;
    @FXML
    private Label songProgressField;
    @FXML
    private AnchorPane previewPane;
    @FXML
    private ScrollPane scrollPane;

    /**
     * The number of songs that have been played in the round so far
     */
    private int songsPlayed;

    /**
     * Whether or not a song is currently playing (could be paused)
     */
    //private boolean isPlaying;

    /**
     * Actually plays/pauses the songs
     */
    private MediaPlayer mediaPlayer;

    /**
     * 
     */
    private static int counter;

    /**
     * Sets up the game-play scene by:
     *  - setting up the corresponding buttons map
     *  - initializing instance variables
     *  - setting the library size and player name text fields
     *  - randomly selecting the songs that will be played this round
     *  - setting up the preview pane
     *  - playing the first song
     *  - setting the timer to switch songs
     * @param url Not used
     * @param rb Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        setupButtonMapping();

        songsPlayed = 0;
        //isPlaying = false;
        ADDPlayer.SONGS_IN_ROUND = new int[ADDPlayer.NUM_SONGS];
        ADDPlayer.PREVIEW_BOXES = new PreviewHBox[ADDPlayer.NUM_SONGS];

        librarySizeField.setText(Integer.toString(
                ADDPlayer.CUR_PLAYER.library.size()));
        playerField.setText(ADDPlayer.CUR_PLAYER.name);
        if (!ADDPlayer.CUR_PLAYER.isPlayerOne) {
            scoreToBeatField.setText(Integer.toString(
                    ADDPlayer.OTHER_PLAYER.scores.get(
                            ADDPlayer.OTHER_PLAYER.scores.size() - 1)));
        }

        songProgressSlider.setMax(ADDPlayer.SONG_LENGTH);

        Random random = new Random();
        for (int i = 0; i < ADDPlayer.NUM_SONGS; i++) {
            ADDPlayer.SONGS_IN_ROUND[i] =
                    random.nextInt(ADDPlayer.CUR_PLAYER.library.size() + 1);
        }

        setupPreviewPane();

        cycleSongs();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(
                1000), ae -> tick(ae)));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS * ADDPlayer.SONG_LENGTH);
        timeline.play();

        //Timeline timeline = new Timeline(new KeyFrame(Duration.millis(
        //        ADDPlayer.SONG_LENGTH * 1000), ae -> cycleSongs()));
        //timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        //timeline.play();
    }

    private void tick(ActionEvent ae) {
        counter++;
        if (counter % ADDPlayer.SONG_LENGTH == 0) {
            cycleSongs();
            counter = 0;
            songProgressSlider.setValue(counter);
            songProgressField.setText("0:0" + counter);
        } else {
            songProgressSlider.setValue(counter);
            if (counter < 10) {
                songProgressField.setText("0:0" + counter);
            } else {
                songProgressField.setText("0:" + counter);
            }
        }
    }

    /**
     * Iterates through each song in the round, adding a tile to the preview
     *     pane for each one
     */
    private void setupPreviewPane() {
        for (int i = 0; i < ADDPlayer.NUM_SONGS; i++) {
            SongDetails song = ADDPlayer.packageIntoSongDetails(
                    ADDPlayer.SONGS_IN_ROUND[i], ADDPlayer.CUR_PLAYER);

            PreviewHBox songPreview = new PreviewHBox(song);

            songPreview.colorBoxes[0].setOnMouseClicked(
                    new ColoredLabelClickHandler(pointsField, songCorrect));
            songPreview.colorBoxes[1].setOnMouseClicked(
                    new ColoredLabelClickHandler(pointsField, artistCorrect));
            songPreview.colorBoxes[2].setOnMouseClicked(
                    new ColoredLabelClickHandler(pointsField, albumCorrect));

            songPreview.setLayoutY(160 * i);

            ADDPlayer.PREVIEW_BOXES[i] = songPreview;
        }
        previewPane.getChildren().addAll(ADDPlayer.PREVIEW_BOXES);
        if (ADDPlayer.NUM_SONGS > 5) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setPrefWidth(315);
        }
    }

    /**
     * Handles all actions to be taken when the song should be switched:
     *  - Stop playing the last song if this isn't the first song
     *  - Apply new handlers to the colored boxes of the last song so that they
     *        do not affect the button states any longer
     *  - Reset the buttons selected values
     *  - Change this song's color boxes from gray to red
     *  - Play the next song
     *  - Display the new song's values to the user
     *  - Increment the number of songs played and display that to the user
     */
    private void cycleSongs() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();

            ColoredLabelClickHandler handler =
                    new ColoredLabelClickHandler(pointsField);
            for (Label colorBox :
                    ADDPlayer.PREVIEW_BOXES[songsPlayed - 1].colorBoxes) {
                colorBox.setOnMouseClicked(handler);
            }
        }

        // Keep going until we hit the passed in number of songs
        if (songsPlayed < ADDPlayer.NUM_SONGS) {
            resetButtons();
            for (int i = 0; i < 3; i++) {
                changeColorBox(songsPlayed, "graylabel", "redlabel", i);
            }
            SongDetails song = ADDPlayer.packageIntoSongDetails(
                    ADDPlayer.SONGS_IN_ROUND[songsPlayed], ADDPlayer.CUR_PLAYER);
            mediaPlayer = ADDPlayer.playNextSong(song);

            // Set the text fields on this screen with the new song info
            songField.setText(song.name);
            artistField.setText(song.artist);
            albumField.setText(song.album);
        } else {
            gameOver();
        }
        songsPlayed++;

        progressField.setText(songsPlayed + "/" + ADDPlayer.NUM_SONGS);
    }

    /**
     * Reset all of the buttons as they may have changed during the last song
     */
    private void resetButtons() {
        if (songCorrect.isSelected()) {
            songCorrect.setSelected(false);
            songIncorrect.setSelected(true);
        }
        if (artistCorrect.isSelected()) {
            artistCorrect.setSelected(false);
            artistIncorrect.setSelected(true);
        }
        if (albumCorrect.isSelected()) {
            albumCorrect.setSelected(false);
            albumIncorrect.setSelected(true);
        }
    }

    /**
     * Changes the color of a passed in colored box
     * @param index Which preview box to alter
     * @param oldColor The old color of the colored box
     * @param newColor The new color of the colored box
     * @param whichBox Which colored box to alter (song, artist or album)
     */
    private void changeColorBox(int index, String oldColor,
            String newColor, int whichBox) {

        ADDPlayer.PREVIEW_BOXES[index].colorBoxes[whichBox].getStyleClass()
                .remove(oldColor);
        ADDPlayer.PREVIEW_BOXES[index].colorBoxes[whichBox].getStyleClass()
                .add(newColor);
    }

    /**
     * When the game is over set the global points value and show the end screen
     */
    private void gameOver() {
        ADDPlayer.POINTS = Integer.parseInt(pointsField.getText());
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("ResultsScreen.fxml"));
            Scene scene = new Scene(root);
            //if (ADDPlayer.RESULTS_SCENE == null) {
            //    ADDPlayer.RESULTS_SCENE = scene;
            //}
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    /**
     * Sets up a mapping in the form of a HashMap from each correct and
     *     incorrect button to its partner button (allows for easy toggling)
     */
    private void setupButtonMapping() {
        ADDPlayer.CORR_BUTTONS = new HashMap<>();
        ADDPlayer.CORR_BUTTONS.put(songCorrect, songIncorrect);
        ADDPlayer.CORR_BUTTONS.put(songIncorrect, songCorrect);
        ADDPlayer.CORR_BUTTONS.put(artistCorrect, artistIncorrect);
        ADDPlayer.CORR_BUTTONS.put(artistIncorrect, artistCorrect);
        ADDPlayer.CORR_BUTTONS.put(albumCorrect, albumIncorrect);
        ADDPlayer.CORR_BUTTONS.put(albumIncorrect, albumCorrect);
    }

    /**
     * Button handlers below here
     */

    /**
     * 
     * @param event The pushing of the "play/pause" button
     */
    @FXML
    private void playPauseButtonAction(ActionEvent event) {
        /**System.out.println(currentSong.currentTimeProperty());
        if (!isPlaying) {
            currentSong.play();
            isPlaying = true;
        } else {
            currentSong.pause();
            isPlaying = false;
        }*/
    }

    /**
     * Handles whenever a correct button is pushed by:
     *  - verifying that this button was not already selected
     *  - incrementing the points text field
     *  - updating the correct color box to green in the preview pane
     *  - toggling the buttons' selected state
     * @param event The pushing of any "correct" button
     */
    @FXML
    private void correctButtonAction(ActionEvent event) {
        ToggleButton thisButton = (ToggleButton)event.getSource();
        if (thisButton.isSelected()) {
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) + 1));

            // Update the correct color in the preview pane
            if (thisButton.getId().equals("songCorrect")) {
                changeColorBox(songsPlayed - 1, "redlabel", "greenlabel", 0);
            } else if (thisButton.getId().equals("artistCorrect")) {
                changeColorBox(songsPlayed - 1, "redlabel", "greenlabel", 1);
            } else if (thisButton.getId().equals("albumCorrect")) {
                changeColorBox(songsPlayed - 1, "redlabel", "greenlabel", 2);
            }
        }
        thisButton.setSelected(true);
        ADDPlayer.CORR_BUTTONS.get(thisButton).setSelected(false);
    }

    /**
     * Handles whenever an incorrect button is pushed by:
     *  - verifying that this button was not already selected
     *  - decrementing the points text field
     *  - updating the correct color box to red in the preview pane
     *  - toggling the buttons' selected state
     * @param event The pushing of any "incorrect" button
     */
    @FXML
    private void incorrectButtonAction(ActionEvent event) {
        ToggleButton thisButton = (ToggleButton)event.getSource();
        if (thisButton.isSelected()) {
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) - 1));

            // Update the correct color in the preview pane
            if (thisButton.getId().equals("songIncorrect")) {
                changeColorBox(songsPlayed - 1, "greenlabel", "redlabel", 0);
            } else if (thisButton.getId().equals("artistIncorrect")) {
                changeColorBox(songsPlayed - 1, "greenlabel", "redlabel", 1);
            } else if (thisButton.getId().equals("albumIncorrect")) {
                changeColorBox(songsPlayed - 1, "greenlabel", "redlabel", 2);
            }
        }
        thisButton.setSelected(true);
        ADDPlayer.CORR_BUTTONS.get(thisButton).setSelected(false);
    }

    /**
     * 
     * @param event Not used
     */
    @FXML
    private void quitRoundButtonAction(ActionEvent event) {
        // TODO: Define preview pane state when skipping to the results screen
        gameOver();
    }
}