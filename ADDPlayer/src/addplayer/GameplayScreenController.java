package addplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
//import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
    private TextField songField;
    @FXML
    private TextField artistField;
    @FXML
    private TextField albumField;
    //@FXML
    //private Slider volumeSlider;
    @FXML
    private AnchorPane previewPane;

    /**
     * The number of songs that have been played in the round so far
     */
    private int songsPlayed;

    /**
     * Whether or not a song is currently playing (could be paused)
     */
    //private boolean isPlaying;

    /**
     * The indices of the songs in the library that will be played this round
     */
    private int[] songsInRound;

    /**
     * Actually plays/pauses the songs
     */
    private MediaPlayer mediaPlayer;

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
        setUpButtonMapping();

        songsPlayed = 0;
        //isPlaying = false;
        songsInRound = new int[ADDPlayer.NUM_SONGS];
        ADDPlayer.PREVIEW_BOXES = new PreviewHBox[ADDPlayer.NUM_SONGS];

        librarySizeField.setText(Integer.toString(ADDPlayer.LIBRARY.size()));
        playerField.setText(ADDPlayer.PLAYER);

        Random random = new Random();
        for (int i = 0; i < songsInRound.length; i++) {
            songsInRound[i] = random.nextInt(ADDPlayer.LIBRARY.size() + 1);
        }

        setupPreviewPane();

        cycleSongs();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(
                ADDPlayer.SONG_LENGTH * 1000), ae -> cycleSongs()));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        timeline.play();
    }

    /**
     * Iterates through each song in the round, adding a tile to the preview
     *     pane for each one
     */
    private void setupPreviewPane() {
        for (int i = 0; i < songsInRound.length; i++) {
            SongDetails song = packageIntoSongDetails();

            PreviewHBox songPreview = new PreviewHBox(
                    song, 800 / ADDPlayer.NUM_SONGS);

            songPreview.colorBoxes[0].setOnMouseClicked(
                    new ColoredLabelClickHandler(pointsField, songCorrect));
            songPreview.colorBoxes[1].setOnMouseClicked(
                    new ColoredLabelClickHandler(pointsField, artistCorrect));
            songPreview.colorBoxes[2].setOnMouseClicked(
                    new ColoredLabelClickHandler(pointsField, albumCorrect));

            songPreview.setLayoutY(800 / ADDPlayer.NUM_SONGS * i);

            ADDPlayer.PREVIEW_BOXES[i] = songPreview;
        }
        previewPane.getChildren().addAll(ADDPlayer.PREVIEW_BOXES);
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
            SongDetails song = playNextSong();

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
     * Plays the next song
     * @return The song that is now being played
     */
    private SongDetails playNextSong() {
        SongDetails song = packageIntoSongDetails();

        Media songFile = new Media(new File(song.location).toURI().toString());
        mediaPlayer = new MediaPlayer(songFile);
        mediaPlayer.play();
        return song;
    }

    /**
     * This method handles standardizing the format of the songs regardless
     *     of whether the library came from on file or from an iTunes play-list
     * @return The song packaged into a SongDetails object
     */
    private SongDetails packageIntoSongDetails() {
        SongDetails song;
        if (ADDPlayer.MODE == 1) {
            // Create fake SongDetails objects from the metadata
            String location = (String) ADDPlayer.LIBRARY.get(
                    songsInRound[songsPlayed]);
            song = convertWithMetadata(location);
        } else {
            song = (SongDetails) ADDPlayer.LIBRARY.get(
                    songsInRound[songsPlayed]);
        }
        return song;
    }

    /**
     * Uses a MP3 parser to pull the relevant meta-data (song, artist and album)
     *     out of the song file at the passed in location on disk
     * @param location The location of the song file
     * @return The song packaged into a SongDetails object
     */
    private SongDetails convertWithMetadata(String location) {
        try (InputStream input = new FileInputStream(new File(location))) {
            Metadata metadata = new Metadata();
            new Mp3Parser().parse(
                    input, new DefaultHandler(), metadata, new ParseContext());
            input.close();

            return new SongDetails(metadata.get("title"),
                    metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), 
                    null, null, location);
        } catch (IOException | SAXException | TikaException e) {
            System.out.println(e);
        }
        return null;
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
    private void setUpButtonMapping() {
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
}