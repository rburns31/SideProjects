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
 * 
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
     * 
     */
    //private boolean isPlaying;

    /**
     * The indices of the songs in the library that will be played this round
     */
    private int[] songsInRound;

    /**
     * 
     */
    private MediaPlayer mediaPlayer;

    /**
     * Sets up the game-play scene by:
     *  - setting up the corresponding buttons map
     *  - initializing instance variables
     *  - setting the library size and player name text fields
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

        setupPreviewPane();

        cycleSongs();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(
                ADDPlayer.SONG_LENGTH * 1000), ae -> cycleSongs()));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        timeline.play();
    }

    /**
     * 
     * @param event 
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
     * 
     * @param event 
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
     * 
     * @param event 
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
     */
    private void cycleSongs() {
        // Stop the previous song (if this isn't the first song)
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            ColoredLabelClickHandler handler =
                    new ColoredLabelClickHandler(pointsField);
            for (Label colorBox :
                    ADDPlayer.PREVIEW_BOXES[songsPlayed - 1].colorBoxes) {
                colorBox.setOnMouseClicked(handler);
            }
        }

        // Play the new song (until we hit the passed in number)
        if (songsPlayed < ADDPlayer.NUM_SONGS) {
            resetDisplay();
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
     * 
     * @return 
     */
    private SongDetails playNextSong() {
        SongDetails song = packageIntoSongDetails();

        Media songFile = new Media(new File(song.location).toURI().toString());
        mediaPlayer = new MediaPlayer(songFile);
        mediaPlayer.play();
        return song;
    }

    /**
     * 
     * @return 
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
     * Game is over, set the final points value and show the end screen
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
     * 
     */
    private void resetDisplay() {
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
     * 
     * @param index
     * @param oldColor
     * @param newColor
     * @param whichBox 
     */
    private void changeColorBox(int index, String oldColor,
            String newColor, int whichBox) {

        ADDPlayer.PREVIEW_BOXES[index].colorBoxes[whichBox].getStyleClass()
                .remove(oldColor);
        ADDPlayer.PREVIEW_BOXES[index].colorBoxes[whichBox].getStyleClass()
                .add(newColor);
    }

    /**
     * 
     * @param location
     * @return 
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
     * 
     */
    private void setupPreviewPane() {
        Random random = new Random();
        for (int i = 0; i < songsInRound.length; i++) {
            songsInRound[i] = random.nextInt(ADDPlayer.LIBRARY.size() + 1);

            // Add the next song to the preview pane
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
            previewPane.getChildren().add(songPreview);
            songsPlayed++;
        }
        songsPlayed = 0;
    }
}